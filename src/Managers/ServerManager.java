package Managers;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;

import java.util.Timer;

/**
 * Created by marcleef on 11/6/15.
 */
public class ServerManager {
    public static void main(String[] args) {
        // Create new data structures
        ChannelMap channelMap = new ChannelMap();
        TelevisionMap televisionMap = new TelevisionMap();
        int portNumber = 4444;

        // Initialize chime manager and begin execution
        ChimeManager chimeManager = new ChimeManager(portNumber,channelMap, televisionMap);
        new Thread(chimeManager).start();

        // Start intermittent logger
        Timer timer = new Timer("MyTimer");
        timer.scheduleAtFixedRate(new LoggerManager(channelMap, televisionMap), 100, 1000);
    }

}
