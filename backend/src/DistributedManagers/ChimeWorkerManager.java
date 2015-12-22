package DistributedManagers;

import DataStructures.*;
import Managers.*;
import Networking.HttpMessageSender;
import Networking.SocketMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

/**
 * Created by marcleef on 12/20/15.
 * Chime edge nodes maintaining socket connections.
 */
public class ChimeWorkerManager {

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
        HttpMessageSender httpSender = new HttpMessageSender();

        // Set port and logger
        int portNumber = 4444;
        Logger logger = LoggerFactory.getLogger(ChimeWorkerManager.class);

        logger.info("Initializing new Chime slave...");

        // TODO: Register with master to validate before continuing


        // Initialize socket based chime manager and begin execution
        ChimeSocketManager chimeSocketManager = new ChimeSocketManager(portNumber, sender, mapper, true);
        logger.info(String.format("Starting Chime Socket Manager on port %d...", portNumber));
        new Thread(chimeSocketManager).start();

        // Initialize web socket based chime manager and begin execution
        try {
            ChimeWebSocketManager chimeWebSocketManager = new ChimeWebSocketManager(portNumber + 1, sender, mapper, true);
            logger.info(String.format("Starting Chime WebSocket Manager on port %d...", portNumber + 1));
            chimeWebSocketManager.start();
        } catch(Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }

        // Initialize RESTful API interface to handle HTTP requests
        SlaveRestManager slaveRestManager = new SlaveRestManager(sender, televisionMap, televisionWSMap);
        logger.info(String.format("Starting Chime REST Manager on port %d...", 4567));
        new Thread(slaveRestManager).start();

        // Start intermittent cleanup processes
        Timer timer = new Timer("Cleanup");
        logger.info("Starting Cleanup Manager...");
        timer.scheduleAtFixedRate(new CleanupManager(channelMap, socketMap, webSocketMap, televisionMap, televisionWSMap), 0, 30000);

    }



}
