package Handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import Messaging.*;
import TV.Channel;
import com.google.gson.*;


/**
 * Created by marcleef on 10/28/15.
 * To handle connection and dispatch appropriate helper threads.
 */
public class ConnectionHandler implements Runnable {
    private Socket client;

    public ConnectionHandler(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            Gson gson = new Gson();

            // Normalize message
            Message message = gson.fromJson(in, Message.class);

            // Get values contained in messages to dispatch thread
            Chime chime = message.getChime();
            Registration registration = message.getRegistration();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
