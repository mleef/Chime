package Managers;

import DataStructures.*;
import Messaging.RegistrationMessage;
import TV.Channel;
import TV.Television;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;

/**
 * Created by marcleef on 11/21/15.
 * Handles updates to the various maps.
 */
public class MapManager {
    private ChannelMap channelMap;
    private SocketMap socketMap;
    private TelevisionMap televisionMap;
    private TelevisionWSMap televisionWSMap;
    private WebSocketMap webSocketMap;
    private Logger logger;

    /**
     * Constructor for the MapManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param socketMap Mapping of sockets to associated televisions.
     * @param televisionMap Mapping of televisions to associated sockets.
     * @param televisionWSMap Mapping of televisions to associated web sockets.
     * @param webSocketMap Mapping of web sockets to associated televisions.
     **/
    public MapManager(ChannelMap channelMap, SocketMap socketMap, WebSocketMap webSocketMap, TelevisionMap televisionMap, TelevisionWSMap televisionWSMap) {
        this.channelMap = channelMap;
        this.socketMap = socketMap;
        this.webSocketMap = webSocketMap;
        this.televisionMap = televisionMap;
        this.televisionWSMap = televisionWSMap;
        this.logger = LoggerFactory.getLogger(MapManager.class);
    }

    /**
     * Removes television object from containing maps (web sockets).
     * @param channel Channel television is on.
     * @param television Television to remove.
     **/
    public void clearTelevisionWS(Channel channel, Television television) {
        logger.info(String.format("Removing %s from %s.", channel.getId(), television.getId()));
        webSocketMap.remove(televisionWSMap.get(television));
        televisionWSMap.remove(television);
        channelMap.removeTV(channel, television);
    }

    /**
     * Removes television object from containing maps using just its associated socket (web sockets).
     * @param socket Socket associated with television to remove.
     **/
    public void clearTelevisionWS(WebSocket socket) {
        logger.info(String.format("Removing %s.", socket.toString()));
        if(webSocketMap.containsKey(socket)) {
            televisionWSMap.remove(webSocketMap.get(socket));
            webSocketMap.remove(socket);
        }
    }

    /**
     * Removes television object from containing maps using just its associated socket (sockets).
     * @param socket Socket associated with television to remove.
     **/
    public void clearTelevision(SocketChannel socket) {
        logger.info(String.format("Removing %s.", socket.toString()));
        if(socketMap.containsKey(socket)) {
            televisionMap.remove(socketMap.get(socket));
            socketMap.remove(socket);
        }
    }

    /**
     * Removes television object from containing maps (sockets).
     * @param channel Channel television is on.
     * @param television Television to remove.
     **/
    public void clearTelevision(Channel channel, Television television) {
        logger.info(String.format("Removing %s from %s.", channel.getId(), television.getId()));
        socketMap.remove(televisionMap.get(television));
        televisionMap.remove(television);
        channelMap.removeTV(channel, television);
    }

    /**
     * Adds television to appropriate maps (sockets).
     * @param television Television to remove.
     * @param client Socket associated with television.
     **/
    public void addTelevision(Television television, SocketChannel client) {
        // Add/update TV's socket
        logger.info(String.format("Updating television %s with %s.", television.getId(), client.toString()));
        televisionMap.put(television, client);
        socketMap.put(client, television);
    }

    /**
     * Adds television to appropriate maps (web sockets).
     * @param television Television to remove.
     * @param client Socket associated with television.
     **/
    public void addTelevisionWS(Television television, WebSocket client) {
        // Add/update TV's socket
        logger.info(String.format("Updating television (%s) socket (%s) in map.", television.getId(), client.toString()));
        televisionWSMap.put(television, client);
        webSocketMap.put(client, television);
    }

    /**
     * Adds television to appropriate maps and channels.
     * @param television Television to remove.
     * @param previousChannel Previous channel to remove television from.
     * @param newChannel Destination channel of television.
     **/
    public void addTelevisionToChannel(Television television, Channel previousChannel, Channel newChannel) {
        // Remove tv from its previously associated channel list if it has one
        try {
            logger.info(String.format("Removing television (%s) from previous channel (%s).", television.getId(), previousChannel.getId()));
            channelMap.removeTV(previousChannel, television);
        } catch(Exception e) {
            logger.error(e.toString());
        }

        logger.info(String.format("Adding television (%s) to channel (%s).", television.getId(), newChannel.getId()));
        // Update mappings with new channel
        channelMap.putTV(newChannel, television);
    }


    /**
     * Moves television to appropriate maps (sockets).
     * @param registrationMessage Message containing information to direct the move.
     * @param client Socket associated with television.
     **/
    public void moveTelevision(RegistrationMessage registrationMessage, SocketChannel client) {
        addTelevision(registrationMessage.getTelevision(), client);
        addTelevisionToChannel(registrationMessage.getTelevision(), registrationMessage.getPreviousChannel(), registrationMessage.getNewChannel());
    }

    /**
     * Moves television to appropriate maps (web sockets).
     * @param registrationMessage Message containing information to direct the move.
     * @param client Socket associated with television.
     **/
    public void moveTelevision(RegistrationMessage registrationMessage, WebSocket client) {
        addTelevisionWS(registrationMessage.getTelevision(), client);
        addTelevisionToChannel(registrationMessage.getTelevision(), registrationMessage.getPreviousChannel(), registrationMessage.getNewChannel());
    }
}
