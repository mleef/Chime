package Managers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;
import Handlers.ConnectionHandler;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * Logic to manage client requests and dispatch appropriate handlers.
 */
public class ChimeManager implements Runnable {
    private ServerSocket server;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Logger logger;

    /**
     * Constructor for the ChimeManager class.
     **/
    public ChimeManager(int portNumber, ChannelMap channelMap, TelevisionMap televisionMap) {
        try {
            this.server = new ServerSocket(portNumber);
            this.channelMap = channelMap;
            this.televisionMap = televisionMap;
            this.logger = LoggerFactory.getLogger(ChimeManager.class);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Relay new socket connection to handler to be processed.
     **/
    public void run() {
        while(true) {
            try {
                Socket newClient = server.accept();
                logger.info(String.format("New connection: %s", newClient.toString()));
                new Thread(new ConnectionHandler(newClient, channelMap, televisionMap)).start();
            } catch(Exception e) {
                logger.error(e.toString());
                e.printStackTrace();
            }
        }
    }
}
