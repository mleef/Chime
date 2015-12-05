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
    private TelevisionMap televisionMap;
    private TelevisionWSMap televisionWSMap;
    private Gson gson;
    private Logger logger;
    private MapManager mapper;

    /**
     * Constructor for the MessageSender class.
     * @param mapper Handles mapping changes.
     * @param channelMap Mapping of channels to watching televisions.
     * @param televisionMap Mapping of televisions to associated sockets.
     * @param televisionWSMap Mapping of televisions to associated web sockets.
     **/
    public MessageSender(MapManager mapper, ChannelMap channelMap, TelevisionMap televisionMap, TelevisionWSMap televisionWSMap) {
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.televisionWSMap = televisionWSMap;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(MessageSender.class);
        this.mapper = mapper;
    }

    /**
     * Broadcast messages to clients
     * @param chimeMessage Message to relay to watching clients.
     **/
    public void broadcast(ChimeMessage chimeMessage) {
        // Get corresponding channel
        Channel channel = chimeMessage.getChannel();

        Set<Television> watchingTelevisions;
        if(channelMap.containsKey(channel)) {
            // Get all TVs currently watching given message source channel
            watchingTelevisions = channelMap.get(channel);
        } else {
            return;
        }

        logger.info(String.format("Broadcasting %s to %d clients...", chimeMessage.getMessage(), watchingTelevisions.size()));
        for(Television television : watchingTelevisions) {
            // Check if given television is using a web socket
            if (televisionWSMap.containsKey(television)) {
                // If socket write fails update maps accordingly
                if (!writeToSocket(televisionWSMap.get(television), chimeMessage)) {
                    mapper.clearTelevisionWS(channel, television);
                }
            }

            // Check if given television is using a normal socket
            if (televisionMap.containsKey(television)) {
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
            socket.send((gson.toJson(chimeMessage).toString()));
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
