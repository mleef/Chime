package Managers;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;
import Handlers.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChimeManager implements Runnable {
    private int portNumber;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Logger logger;

    /**
     * Constructor for the CleanupManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param televisionMap Mapping of televisions to respective sockets.
     **/
    public ChimeManager(int portNumber, ChannelMap channelMap, TelevisionMap televisionMap) {
        this.portNumber = portNumber;
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.logger = LoggerFactory.getLogger(ChimeManager.class);
    }

    /**
     * Listen for connections from clients.
     **/
    public void run() {
        try {
            InetAddress hostIPAddress = InetAddress.getByName("localhost");
            Selector selector = Selector.open();
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            ssChannel.socket().bind(new InetSocketAddress(hostIPAddress, portNumber));
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
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
    public void processSelectedKeys(Set selectedKeys) throws Exception {
        Iterator iterator = selectedKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();
            if (key.isAcceptable()) {
                ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                SocketChannel sChannel = ssChannel.accept();
                sChannel.configureBlocking(false);
                sChannel.register(key.selector(), SelectionKey.OP_READ);
                logger.info(String.format("Received new connection: %s", sChannel.toString()));

            }

            if (key.isReadable()) {
                SocketChannel sChannel = (SocketChannel) key.channel();
                String msg = processSocketRead(sChannel);

                if (msg.length() > 0) {
                    logger.info(String.format("New readable data from: %s", sChannel.toString()));
                    ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                    new Thread(new ConnectionHandler(sChannel, msg, channelMap, televisionMap)).start();
                } else {
                    logger.info(String.format("Closed connection: %s", sChannel.toString()));
                    sChannel.close();
                }
            }
        }
    }

    /**
     * Read data from socket into buffer then string.
     * @param sChannel Television that is being added to channel.
     * @return String representation of byte stream.
     **/
    public String processSocketRead(SocketChannel sChannel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesCount = sChannel.read(buffer);
        if (bytesCount > 0) {
            buffer.flip();
            return new String(buffer.array()).trim();
        }
        return "";
    }
}