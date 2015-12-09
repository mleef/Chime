package Managers;

import DataStructures.*;

import java.util.ArrayList;
import java.util.TimerTask;

import TV.Channel;
import TV.Television;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.management.UnixOperatingSystemMXBean;

/**
 * Created by marcleef on 11/6/15.
 * To manage the garbage collection of unused sockets.
 */
public class CleanupManager extends TimerTask {
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private TelevisionWSMap televisionWSMap;
    private WebSocketMap webSocketMap;
    private SocketMap socketMap;
    private Logger logger;
    private OperatingSystemMXBean os;
    /**
     * Constructor for the CleanupManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param televisionMap Mapping of televisions to respective sockets.
     **/
    public CleanupManager(ChannelMap channelMap, SocketMap socketMap, WebSocketMap webSocketMap, TelevisionMap televisionMap, TelevisionWSMap televisionWSMap) {
        this.channelMap = channelMap;
        this.socketMap = socketMap;
        this.webSocketMap = webSocketMap;
        this.televisionMap = televisionMap;
        this.televisionWSMap = televisionWSMap;
        this.logger = LoggerFactory.getLogger(CleanupManager.class);
        this.os = ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public void run() {
        logger.info("*****Cleanup Manager*****");
        logger.info("Total viewers (Channel Map): " + channelMap.getTotalViewers());
        logger.info("Total viewers (Television Socket Map): " + televisionMap.keySet().size());
        logger.info("Total viewers (Television Web Socket Map): " + televisionWSMap.keySet().size());
        logger.info("Total viewers (Socket Map): " + socketMap.keySet().size());
        logger.info("Total viewers (Web Socket Map): " + webSocketMap.keySet().size());
        logger.info("Total file descripors: " + ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount());
        logger.info("*************************");
        clean();
    }

    public void clean() {
        // To store objects to remove
        ArrayList<Channel> channelsToRemoveFrom = new ArrayList<>();
        ArrayList<Television> televisionsToRemove = new ArrayList<>();

        // Determine all unused television objects
        for(Channel channel : channelMap.keySet()) {
            for(Television television : channelMap.get(channel)) {
                if(!televisionMap.containsKey(television) && ! televisionWSMap.containsKey(television)) {
                    channelsToRemoveFrom.add(channel);
                    televisionsToRemove.add(television);
                }
            }
        }

        // Clear unused television objects
        for(int i = 0; i < channelsToRemoveFrom.size(); i++) {
            channelMap.removeTV(channelsToRemoveFrom.get(i), televisionsToRemove.get(i));
        }

        ArrayList<Television> televisionSocketsToRemove = new ArrayList<>();
        ArrayList<Television> televisionWebSocketsToRemove = new ArrayList<>();


        // Determine all closed sockets and web sockets
        for(Television television : televisionMap.keySet()) {
            if(!televisionMap.get(television).isConnected()) {
                televisionSocketsToRemove.add(television);
            }
        }

        for(Television television : televisionWSMap.keySet()) {
            if(televisionWSMap.get(television).isClosed()) {
                televisionWebSocketsToRemove.add(television);
            }
        }

        // Clear unused television/socket objects
        Television curTelevision;
        for(int i = 0; i < televisionSocketsToRemove.size(); i++) {
            curTelevision = televisionSocketsToRemove.get(i);
            if(socketMap.containsKey(televisionMap.get(curTelevision))) {
                socketMap.remove(televisionMap.get(curTelevision));
            }
            televisionMap.remove(curTelevision);
        }

        // Clear unused television/websocket objects
        for(int i = 0; i < televisionWebSocketsToRemove.size(); i++) {
            curTelevision = televisionWebSocketsToRemove.get(i);
            if(webSocketMap.containsKey(televisionWSMap.get(curTelevision))) {
                webSocketMap.remove(televisionWSMap.get(curTelevision));
            }
            televisionWSMap.remove(curTelevision);
        }


    }

}
