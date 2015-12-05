package Managers;

import Handlers.ConnectionHandler;
import Messaging.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class ChimeSocketManager implements Runnable {
    private ServerSocketChannel serverChannel;
    private int portNumber;
    private Logger logger;
    private MessageSender sender;
    private MapManager mapper;

    /**
     * Constructor for the CleanupManager class.
     **/
    public ChimeSocketManager(int portNumber, MessageSender sender, MapManager mapper) {
        this.portNumber = portNumber;
        this.logger = LoggerFactory.getLogger(ChimeSocketManager.class);
        this.sender = sender;
        this.mapper = mapper;
    }

    /**
     * Listen for connections from clients.
     **/
    public void run() {
        try {
            // Initialize and configure server socket channel
            InetAddress hostIPAddress = InetAddress.getByName("localhost");
            Selector selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(portNumber));

            // Register server socket with selector
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            // Loop forever and wait for actions on selector keys
            while (true) {
                // This blocks and waits for actions on keys, not a busy wait
                if (selector.select() <= 0) {
                    continue;
                }
                processSelectedKeys(selector.selectedKeys());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Responds accordingly to actions performed by selector keys.
     * @param selectedKeys Channel to associate new television object with.
     * @throws Exception
     **/
    private void processSelectedKeys(Set selectedKeys) throws Exception {
        // To store socket channels and associated data
        ArrayList<SocketChannel> sockets = new ArrayList<>();
        ArrayList<String> messages = new ArrayList<>();

        Iterator iterator = selectedKeys.iterator();
        while (iterator.hasNext()) {
            // Store and remove key so we don't reprocess event
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();

            // Check if key is acceptable and register new socket if so
            if (key.isAcceptable()) {
                // Register new socket channel
                SocketChannel sChannel = serverChannel.accept();
                sChannel.configureBlocking(false);
                sChannel.register(key.selector(), SelectionKey.OP_READ);
                logger.info(String.format("Received new connection: %s", sChannel.toString()));
            }

            // Check if key has data to read
            if (key.isReadable()) {
                // Get socket channel to read from and process message
                SocketChannel sChannel = (SocketChannel) key.channel();
                String msg = processSocketRead(sChannel);
                // Data to read
                if (msg.length() > 0) {
                    logger.info(String.format("New readable data from: %s", sChannel.toString()));
                    // Collect socket and channel for later handling
                    sockets.add(sChannel);
                    messages.add(msg);
                } else {
                    // Close socket once its done transmitting
                    logger.info(String.format("Closed connection: %s", sChannel.toString()));
                    sChannel.close();
                    // Update mappings to avoid leaks
                    mapper.clearTelevision(sChannel);
                }
            }
        }
        // Dispatch thread to handle aggregated messages
        new Thread(new ConnectionHandler(sockets, messages, sender, mapper)).start();
    }

    /**
     * Read data from socket into buffer then string.
     * @param sChannel Television that is being added to channel.
     * @return String representation of byte stream.
     **/
    private String processSocketRead(SocketChannel sChannel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesCount = sChannel.read(buffer);
        if (bytesCount > 0) {
            buffer.flip();
            return new String(buffer.array()).trim();
        }
        return "";
    }
}