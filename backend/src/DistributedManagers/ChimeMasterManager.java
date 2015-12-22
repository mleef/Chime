package DistributedManagers;

import DataStructures.*;
import Managers.CleanupManager;
import Managers.MapManager;
import Networking.HttpMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

/**
 * Created by marcleef on 12/20/15.
 * Handles request delegation and management of ChimeSlaves.
 */
public class ChimeMasterManager {
    public static void main(String[] args) {
        // Create new data structures
        ChannelMap channelMap = new ChannelMap();
        ChannelMap slaveMap = new ChannelMap();
        TelevisionMap televisionMap = new TelevisionMap();
        TelevisionWSMap televisionWSMap = new TelevisionWSMap();
        SocketMap socketMap = new SocketMap();
        WebSocketMap webSocketMap = new WebSocketMap();

        // For managing mapping updates
        MapManager mapper = new MapManager(channelMap, socketMap, webSocketMap, televisionMap, televisionWSMap);

        // For sending messages to Chime slaves
        HttpMessageSender sender = new HttpMessageSender();

        // Set port and logger
        int portNumber = 4444;
        Logger logger = LoggerFactory.getLogger(ChimeWorkerManager.class);

        logger.info("Initializing new Chime master...");

        // Initialize RESTful API interface to handle HTTP requests
        MasterRestManager masterRestManager = new MasterRestManager(channelMap, slaveMap, mapper, sender);
        logger.info(String.format("Starting Chime REST Manager on port %d...", 4567));
        new Thread(masterRestManager).start();

        // Start intermittent cleanup processes
        Timer timer = new Timer("Cleanup");
        logger.info("Starting Cleanup Manager...");
        timer.scheduleAtFixedRate(new CleanupManager(channelMap, socketMap, webSocketMap, televisionMap, televisionWSMap), 0, 30000);

    }
}
