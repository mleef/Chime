package Managers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by marcleef on 10/28/15.
 * Logic to manage client requests and dispatch appropriate handlers.
 */
public class ChimeManager {
    private ServerSocket server;

    /**
     * Constructor for the ChimeManager class.
     **/
    public ChimeManager() {
        try {
            server = new ServerSocket(80);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * TODO: Process message type and dispatch appropriate thread handler.
     **/
    public void run() {
        while(true) {
            try {
                Socket newClient = server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(newClient.getInputStream()));
                // determine what kind of request this is (register, send, etc.)
                // dispatch thread to handle client
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
