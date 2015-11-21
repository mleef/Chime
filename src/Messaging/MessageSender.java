package Messaging;

import DataStructures.*;
import Managers.MapManager;
import TV.Channel;
import TV.Television;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Created by marcleef on 11/21/15.
 * Handles message sending to various client types (sockets/web sockets).
 */
public class MessageSender {
    private ChannelMap channelMap;
    private SocketMap socketMap;
    private TelevisionMap televisionMap;
    private TelevisionWSMap televisionWSMap;
    private WebSocketMap webSocketMap;
    private Gson gson;
    private Logger logger;
    private MapManager mapper;

    /**
     * Constructor for the MessageSender class.
     * @param mapper Handles mapping changes.
     * @param channelMap Mapping of channels to watching televisions.
     * @param socketMap Mapping of sockets to associated televisions.
     * @param webSocketMap Mapping of web sockets to associated televisions.
     * @param televisionMap Mapping of televisions to associated sockets.
     * @param televisionWSMap Mapping of televisions to associated web sockets.
     **/
    public MessageSender(MapManager mapper, ChannelMap channelMap, SocketMap socketMap, WebSocketMap webSocketMap, TelevisionMap televisionMap, TelevisionWSMap televisionWSMap) {
        this.channelMap = channelMap;
        this.socketMap = socketMap;
        this.webSocketMap = webSocketMap;
        this.televisionMap = televisionMap;
        this.televisionWSMap = televisionWSMap;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(MessageSender.class);
        this.mapper = mapper;
    }

    /**
     * Removes television object from containing maps (web sockets).
     * @param chimeMessage Message to relay to watching clients.
     **/
    public void sendChimes(ChimeMessage chimeMessage) {
        // Get corresponding channel
        Channel channel = chimeMessage.getChannel();

        // Get all TVs currently watching given message source channel
        Set<Television> watchingTelevisions = channelMap.get(channel);

        logger.info(String.format("Broadcasting %s to %d clients...", chimeMessage.getMessage(), watchingTelevisions.size()));

        for(Television television : watchingTelevisions) {
            // Check if given television is using a web socket
            if (televisionWSMap.contains(television)) {
                // If socket write fails update maps accordingly
                if (!writeToSocket(televisionWSMap.get(television), chimeMessage)) {
                    mapper.clearTelevisionWS(channel, television);
                }
            }

            // Check if given television is using a normal socket
            if (televisionMap.contains(television)) {
                // If socket write fails update maps accordingly
                if (!writeToSocket(televisionMap.get(television), chimeMessage)) {
                    mapper.clearTelevision(channel, television);
                }
            }
        }
    }

    /**
     * Transmits data to client (web sockets).
     * @param chimeMessage Message to relay to client.
     * @param socket Socket to write data to.
     * @return True on success, false otherwise.
     **/
    public boolean writeToSocket(WebSocket socket, ChimeMessage chimeMessage) {
        try {
            socket.send(ByteBuffer.wrap(gson.toJson(chimeMessage).toString().getBytes()));
            logger.info(String.format("Successfully wrote message to %s", socket.toString()));
            return true;
        } catch(Exception e) {
            logger.error(e.toString());
            return false;
        }
    }

    /**
     * Transmits data to client (sockets).
     * @param chimeMessage Message to relay to client.
     * @param socket Socket to write data to.
     * @return True on success, false otherwise.
     **/
    public boolean writeToSocket(SocketChannel socket, ChimeMessage chimeMessage) {
        try {
            socket.write(ByteBuffer.wrap(gson.toJson(chimeMessage).toString().getBytes()));
            logger.info(String.format("Successfully wrote message to %s", socket.toString()));
            return true;
        } catch(Exception e) {
            logger.error(e.toString());
            return false;
        }
    }


}
