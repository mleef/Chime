package Managers;

import DataStructures.*;

import Networking.SocketMessageSender;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Timer;

/**
 * Created by marcleef on 11/6/15.
 * Main driver of the prototype, monolithic, chime backend.
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
        SocketMessageSender sender = new SocketMessageSender(mapper, channelMap, televisionMap, televisionWSMap);

        // Set port and logger
        int portNumber = 4444;
        Logger logger = LoggerFactory.getLogger(ServerManager.class);

        // Initialize socket based chime manager and begin execution
        ChimeSocketManager chimeSocketManager = new ChimeSocketManager(portNumber, sender, mapper, false);
        logger.info(String.format("Starting Chime Socket Manager on port %d...", portNumber));
        new Thread(chimeSocketManager).start();

        // Initialize web socket based chime manager and begin execution
        try {
            ChimeWebSocketManager chimeWebSocketManager = new ChimeWebSocketManager(portNumber + 1, sender, mapper, false);
            logger.info(String.format("Starting Chime WebSocket Manager on port %d...", portNumber + 1));
            chimeWebSocketManager.start();
        } catch(Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }


        // Initialize RESTful API interface to handle HTTP requests
        ChimeRestManager chimeRestManager = new ChimeRestManager(sender, mapper, channelMap, televisionMap, televisionWSMap);
        logger.info(String.format("Starting Chime REST Manager on port %d...", 4567));
        new Thread(chimeRestManager).start();

        // Start intermittent cleanup processes.
        Timer timer = new Timer("Cleanup");
        logger.info("Starting Cleanup Manager...");
        timer.scheduleAtFixedRate(new CleanupManager(channelMap, socketMap, webSocketMap, televisionMap, televisionWSMap), 0, 30000);

    }

}
