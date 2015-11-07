package Handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;
import Messaging.*;
import TV.Channel;
import com.google.gson.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * To handle connection and dispatch appropriate helper threads.
 */
public class ConnectionHandler extends Handler {
    private Socket client;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Gson gson;
    private Logger logger;


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
        this.logger = LoggerFactory.getLogger(ConnectionHandler.class);
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
                logger.info(String.format("REGISTRATION - FROM: %s, NEW CHANNEL: %s", registration.getTelevision().getId(), registration.getNewChannel().getId()));
                logger.info("Dispatching new Register handler.");
                new Thread(new RegisterHandler(client, registration, channelMap, televisionMap)).start();
            } else if(registration == null && chime != null) {
                logger.info(String.format("CHIME - CHANNEL: %s, FROM: %s, TIME SENT: %s MESSAGE: %s", chime.getChannel().getId(), chime.getSender().getId(), chime.getTimeSent(), chime.getMessage()));
                logger.info("Dispatching new Chime handler.");
                new Thread(new ChimeHandler(client, chime, channelMap, televisionMap)).start();
            }


        } catch(Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
    }
}
