package Managers;

import DataStructures.*;

import Messaging.MessageSender;
import TV.Channel;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import static spark.Spark.*;

/**
 * Created by marcleef on 11/6/15.
 * Main driver of the program.
 */
public class ServerManager {
    public static void main(String[] args) {

        // Create new data structures
        ChannelMap channelMap = new ChannelMap();
        // For testing
        channelMap.addChannel(new Channel("ESPN"));
        channelMap.addChannel(new Channel("NBC"));
        channelMap.addChannel(new Channel("CNN"));
        channelMap.addChannel(new Channel("TNT"));
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
        ChimeSocketManager chimeSocketManager = new ChimeSocketManager(portNumber, sender, mapper);
        logger.info(String.format("Starting Chime Socket Manager on port %d...", portNumber));
        new Thread(chimeSocketManager).start();

        // Initialize web socket based chime manager and begin execution
        try {
            ChimeWebSocketManager chimeWebSocketManager = new ChimeWebSocketManager(portNumber + 1, sender, mapper);
            logger.info(String.format("Starting Chime WebSocket Manager on port %d...", portNumber + 1));
            chimeWebSocketManager.start();
        } catch(Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }


        // Initialize RESTful API interface to handle HTTP requests
        ChimeRestManager chimeRestManager = new ChimeRestManager(sender, mapper, channelMap, socketMap, webSocketMap, televisionMap, televisionWSMap);
        logger.info(String.format("Starting Chime REST Manager on port %d...", 4567));
        new Thread(chimeRestManager).start();

        // Start intermittent statistics generation
        // Timer timer = new Timer("Stats");
        // logger.info("Starting Stats Manager...");
        // timer.scheduleAtFixedRate(new StatsManager(channelMap, televisionMap), 1000, 10000);

    }

}
