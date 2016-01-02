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
        ChannelMap workerMap = new ChannelMap();
        TelevisionMap televisionMap = new TelevisionMap();
        TelevisionWSMap televisionWSMap = new TelevisionWSMap();
        SocketMap socketMap = new SocketMap();
        WebSocketMap webSocketMap = new WebSocketMap();

        // For sending messages to Chime slaves
        HttpMessageSender httpMessageSender = new HttpMessageSender();

        // For managing mapping updates
        MapManager mapper = new MapManager(channelMap, socketMap, webSocketMap, televisionMap, televisionWSMap, null, httpMessageSender);

        // Set port and logger
        int portNumber = args.length > 0 ? Integer.parseInt(args[0]) : 4500;
        Logger logger = LoggerFactory.getLogger(ChimeMasterManager.class);

        logger.info("Initializing new Chime master...");

        // Initialize RESTful API interface to handle HTTP requests
        MasterRestManager masterRestManager = new MasterRestManager(portNumber, channelMap, workerMap, mapper, httpMessageSender);
        logger.info(String.format("Starting Chime Master on port %d...", portNumber));
        new Thread(masterRestManager).start();

        // Add shutdown hook to master
        Runtime.getRuntime().addShutdownHook(new Thread(new MasterShutdownManager(masterRestManager)));

    }
}
