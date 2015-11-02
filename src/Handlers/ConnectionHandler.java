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
public class ConnectionHandler extends Handler {
    private Socket client;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Gson gson;

    /**
     * Constructor for ChimeHandler class.
     * @param client Socket of television that sent message.
     * @param channelMap Mapping of channels to listening clients.
     * @param televisionMap Mapping of televisions to associated open sockets.
     **/
    public ConnectionHandler(Socket client, ChannelMap channelMap, TelevisionMap televisionMap) {
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.client = client;
        this.gson = new Gson();
    }

    private void dispatchThread(Runnable task) {
        new Thread(task).start();
    }

    /**
     * Determine content of client message and dispatch appropriate handler type.
     **/
    public void run() {
        try {
            // Get socket's input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // Deserialize message into Message object instance
            Message message = gson.fromJson(in, Message.class);

            // Get values contained in message to dispatch appropriate thread
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
