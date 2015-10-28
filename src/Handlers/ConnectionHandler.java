package Handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import Messaging.Chime;
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
            StringBuilder clientRequest = new StringBuilder();
            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            Gson gson = new Gson();
            Object obj = gson.fromJson(in, Channel.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
