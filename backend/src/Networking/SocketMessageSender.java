package Networking;

import DataStructures.*;
import Managers.MapManager;
import Messaging.ChimeMessage;
import TV.Channel;
import TV.Television;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by marcleef on 11/21/15.
 * Handles message sending to various client types (sockets/web sockets).
 */
public class SocketMessageSender {
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
    public SocketMessageSender(MapManager mapper, ChannelMap channelMap, TelevisionMap televisionMap, TelevisionWSMap televisionWSMap) {
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.televisionWSMap = televisionWSMap;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(SocketMessageSender.class);
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

        // Cast Set to ArrayList
        ArrayList<Television> televisions = new ArrayList<Television>();
        televisions.addAll(watchingTelevisions);

        logger.info(String.format("Broadcasting %s to %d clients...", chimeMessage.getMessage(), watchingTelevisions.size()));

        sendChimes(televisions, chimeMessage);
    }

    /**
     * Broadcast messages to clients
     * @param televisions Televisions to deliver new Chime Message to.
     **/
    public void broadcast(ArrayList<Television> televisions, ChimeMessage chimeMessage) {
        logger.info(String.format("Broadcasting %s to %d clients...", chimeMessage.getMessage(), televisions.size()));
        sendChimes(televisions, chimeMessage);
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

    /**
     * Writes messages to appropriate sockets/websockets.
     * @param televisions List of televisions to send Chimes to.
     * @param chimeMessage Message to relay to client.
     **/
    public void sendChimes(ArrayList<Television> televisions, ChimeMessage chimeMessage) {
        boolean exists = false;
        ArrayList<Television> televisionsToRemove = new ArrayList<>();
        Channel channel = chimeMessage.getChannel();

        for(Television television : televisions) {
            // Check if given television is using a web socket
            if (televisionWSMap.containsKey(television)) {
                exists = true;
                // If socket write fails update maps accordingly
                if (!writeToSocket(televisionWSMap.get(television), chimeMessage)) {
                    mapper.clearTelevisionWS(channel, television);
                }
            }

            // Check if given television is using a normal socket
            if (televisionMap.containsKey(television)) {
                exists = true;
                // If socket write fails update maps accordingly
                if (!writeToSocket(televisionMap.get(television), chimeMessage)) {
                    mapper.clearTelevision(channel, television);
                }
            }

            // Remove from channel mapping if it doesn't exist
            if(!exists) {
                televisionsToRemove.add(television);
            }
            exists = false;
        }

        // Remove all closed connection televisions from map
        for(Television television : televisionsToRemove) {
            channelMap.removeTV(channel, television);
        }
    }


}
