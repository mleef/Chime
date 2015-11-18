package Managers;

import DataStructures.*;

import java.util.Timer;

import org.glassfish.tyrus.server.Server;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.websocket.*;


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

        // Set port and logger
        int portNumber = 4444;
        Logger logger = LoggerFactory.getLogger(ServerManager.class);

        // Initialize socket based chime manager and begin execution
        ChimeManager chimeManager = new ChimeManager(portNumber, channelMap, televisionMap, socketMap);
        logger.info(String.format("Starting Chime Manager on port %d...", portNumber));
        new Thread(chimeManager).start();

        // Initialize web socket based chime manager and begin execution
        try {
            ChimeManagerWS chimeManagerWS = new ChimeManagerWS(portNumber + 1, channelMap, televisionWSMap, webSocketMap);
            logger.info(String.format("Starting Chime Manager WS on port %d...", portNumber + 1));
            chimeManagerWS.start();
        } catch(Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }

    }

}
