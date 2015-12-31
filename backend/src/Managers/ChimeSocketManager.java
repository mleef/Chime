package Managers;

import Handlers.ConnectionHandler;
import Networking.HttpMessageSender;
import Networking.SocketMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private SocketMessageSender socketMessageSender;
    private HttpMessageSender httpMessageSender;
    private MapManager mapper;
    private String masterUrl;

    /**
     * Constructor for the ChimeSocketManager class.
     * @param  portNumber Port to listen on.
     * @param socketMessageSender To handle messaging.
     * @param httpMessageSender To send HTTP messages to master.
     * @param mapper To handle map updates.
     * @param masterUrl Determines behavior of manager (worker node vs. monolith)
     **/
    public ChimeSocketManager(int portNumber, SocketMessageSender socketMessageSender, HttpMessageSender httpMessageSender, MapManager mapper, String masterUrl) {
        this.portNumber = portNumber;
        this.logger = LoggerFactory.getLogger(ChimeSocketManager.class);
        this.socketMessageSender = socketMessageSender;
        this.httpMessageSender = httpMessageSender;
        this.mapper = mapper;
        this.masterUrl = masterUrl;
    }

    /**
     * Listen for connections from clients.
     **/
    @Override
    public void run() {
        try {
            // Initialize and configure server socket channel
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
                handleKeyChangeEvents(selector.selectedKeys());
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
    private void handleKeyChangeEvents(Set selectedKeys) throws Exception {
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
                SocketChannel socketChannel = serverChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(key.selector(), SelectionKey.OP_READ);
                logger.info(String.format("Received new connection: %s", socketChannel.toString()));
            }

            // Check if key has data to read
            if (key.isReadable()) {
                // Get socket channel to read from and process message
                SocketChannel socketChannel = (SocketChannel) key.channel();

                String msg = "";
                // Handle dead connections
                try {
                    msg = getSocketMessage(socketChannel);
                } catch(Exception e) {
                    logger.error(e.toString());
                    socketChannel.close();
                    mapper.clearTelevision(socketChannel);
                }

                // Data to read
                if (msg.length() > 0) {
                    logger.info(String.format("New readable data from: %s", socketChannel.toString()));
                    // Collect socket and channel for later handling
                    sockets.add(socketChannel);
                    messages.add(msg);
                } else {
                    // Close socket once its done transmitting
                    logger.info(String.format("Closed connection: %s", socketChannel.toString()));
                    socketChannel.close();
                    // Update mappings to avoid leaks
                    mapper.clearTelevision(socketChannel);
                }
            }
        }
        // Dispatch thread to handle aggregated messages
        new Thread(new ConnectionHandler(sockets, messages, socketMessageSender, httpMessageSender, mapper, masterUrl)).start();
    }

    /**
     * Read data from socket into buffer then string.
     * @param socketChannel Television that is being added to channel.
     * @return String representation of byte stream.
     **/
    private String getSocketMessage(SocketChannel socketChannel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        if (socketChannel.isConnected()) {
            int bytesCount = socketChannel.read(buffer);
            if (bytesCount > 0) {
                buffer.flip();
                return new String(buffer.array()).trim();
            }
        }
        return "";
    }
}