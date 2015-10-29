package Managers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;
import Handlers.ConnectionHandler;

/**
 * Created by marcleef on 10/28/15.
 * Logic to manage client requests and dispatch appropriate handlers.
 */
public class ChimeManager {
    private ServerSocket server;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;

    public static void main(String[] args) {
        ChimeManager manager = new ChimeManager();
        manager.run();
    }
    /**
     * Constructor for the ChimeManager class.
     **/
    public ChimeManager() {
        try {
            server = new ServerSocket(4444);
            channelMap = new ChannelMap();
            televisionMap = new TelevisionMap();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void dispatchThread(Runnable task) {
        new Thread(task).start();
    }

    /**
     * Relay new socket connection to handler to be processed.
     **/
    public void run() {
        while(true) {
            try {
                Socket newClient = server.accept();
                dispatchThread(new ConnectionHandler(newClient));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
