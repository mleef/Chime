package Managers;

import DataStructures.ChannelMap;
import DataStructures.SocketMap;
import DataStructures.TelevisionMap;

import java.util.Timer;
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
        SocketMap socketMap = new SocketMap();
        int portNumber = 4444;
        Logger logger = LoggerFactory.getLogger(ServerManager.class);

        // Initialize chime manager and begin execution
        ChimeManager chimeManager = new ChimeManager(portNumber, channelMap, televisionMap, socketMap);
        logger.info("Starting Chime Manager...");
        new Thread(chimeManager).start();

        /*
        // Start intermittent cleanup
        Timer timer = new Timer("Cleaner");
        logger.info("Starting Cleanup Manager...");
        timer.scheduleAtFixedRate(new CleanupManager(channelMap, televisionMap), 1000, 10000);
        */
    }

}
