package Handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;
import Messaging.*;
import TV.Channel;
import com.google.gson.*;


/**
 * Created by marcleef on 10/28/15.
 * To handle connection and dispatch appropriate helper threads.
 */
public class ConnectionHandler implements Runnable {
    private Socket client;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;

    public ConnectionHandler(Socket client, ChannelMap channelMap, TelevisionMap televisionMap) {
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.client = client;
    }

    private void dispatchThread(Runnable task) {
        new Thread(task).start();
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

            // Dispatch appropriate worker thread based on message type
            if(chime == null && registration != null) {
                dispatchThread(new RegisterHandler(client, registration, channelMap, televisionMap));
            } else if(registration == null && chime != null) {
                dispatchThread(new ChimeHandler(client, chime, channelMap, televisionMap));
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
