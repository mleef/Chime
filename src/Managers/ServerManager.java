package Managers;

import DataStructures.*;

import Messaging.MessageSender;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * Created by marcleef on 11/6/15.
 * Main driver of the program.
 */
public class ServerManager {
    public static void main(String[] args) {

        // Create new data structures
        ChannelMap channelMap = new ChannelMap();
        TelevisionMap televisionMap = new TelevisionMap();
        TelevisionWSMap televisionWSMap = new TelevisionWSMap();
        SocketMap socketMap = new SocketMap();
        WebSocketMap webSocketMap = new WebSocketMap();

        // For managing mapping updates
        MapManager mapper = new MapManager(channelMap, socketMap, webSocketMap, televisionMap, televisionWSMap);

        // For sending messages to clients
        MessageSender sender = new MessageSender(mapper, channelMap, televisionMap, televisionWSMap);

        // Set port and logger
        int portNumber = 4444;
        Logger logger = LoggerFactory.getLogger(ServerManager.class);

        // Initialize socket based chime manager and begin execution
        ChimeManager chimeManager = new ChimeManager(portNumber, sender, mapper);
        logger.info(String.format("Starting Chime Manager on port %d...", portNumber));
        new Thread(chimeManager).start();

        // Initialize web socket based chime manager and begin execution
        try {
            ChimeManagerWS chimeManagerWS = new ChimeManagerWS(portNumber + 1, sender, mapper);
            logger.info(String.format("Starting Chime Manager WS on port %d...", portNumber + 1));
            chimeManagerWS.start();
        } catch(Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }

    }

}
