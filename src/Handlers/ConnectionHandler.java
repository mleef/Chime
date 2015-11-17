package Handlers;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import DataStructures.ChannelMap;
import DataStructures.SocketMap;
import DataStructures.TelevisionMap;
import Messaging.*;
import com.google.gson.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * To handle connection and dispatch appropriate helper threads.
 */
public class ConnectionHandler extends Handler {
    private ArrayList<SocketChannel> clients;
    private ArrayList<String> messages;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private SocketMap socketMap;
    private Gson gson;
    private Logger logger;
    private InputStream inputStream;
    private OutputStream outputStream;


    /**
     * Constructor for ChimeHandler class.
     * @param clients Socket of television that sent message.
     * @param channelMap Mapping of channels to listening clients.
     * @param televisionMap Mapping of televisions to associated open sockets.
     **/
    public ConnectionHandler(ArrayList<SocketChannel> clients, ArrayList<String> messages, ChannelMap channelMap, TelevisionMap televisionMap, SocketMap socketMap) {
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.socketMap = socketMap;
        this.clients = clients;
        this.messages = messages;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(ConnectionHandler.class);
    }

    /**
     * Determine content of client messages and dispatch appropriate handler type.
     **/
    public void run() {
        try {
            String curMessage;
            SocketChannel curClient;

            for(int i = 0; i < messages.size(); i++) {
                // Set current message/client combination
                curMessage = messages.get(i);
                curClient = clients.get(i);

                // Deserialize message into Message object instance
                ClientMessage clientMessage = gson.fromJson(curMessage, ClientMessage.class);

                // Check for proper object properties
                if (clientMessage == null || !clientMessage.isValid()) {
                    logger.error("Invalid Client Message");
                    logger.info("Aborting thread...");
                    Thread.currentThread().interrupt();
                    return;
                }

                // Get values contained in message to dispatch appropriate thread
                ChimeMessage chimeMessage = clientMessage.getChimeMessage();
                RegistrationMessage registrationMessage = clientMessage.getRegistrationMessage();

                // Dispatch appropriate worker thread based on message type
                dispatchThread(curClient, chimeMessage, registrationMessage);
            }


        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
    }

    private void dispatchThread(SocketChannel client, ChimeMessage chimeMessage, RegistrationMessage registrationMessage) {
        // Dispatch appropriate thread based on message type
        if(registrationMessage != null && registrationMessage.isValid()) {
            logger.info(String.format("REGISTRATION - FROM: %s, NEW CHANNEL: %s", registrationMessage.getTelevision().getId(), registrationMessage.getNewChannel().getId()));
            logger.info("Dispatching new Register handler.");
            new Thread(new RegisterHandler(client, registrationMessage, channelMap, televisionMap, socketMap)).start();
        } else if(chimeMessage != null && chimeMessage.isValid()) {
            logger.info(String.format("CHIME - CHANNEL: %s, FROM: %s, TIME SENT: %s MESSAGE: %s", chimeMessage.getChannel().getId(), chimeMessage.getSender().getId(), chimeMessage.getTimeSent(), chimeMessage.getMessage()));
            logger.info("Dispatching new Chime handler.");
            new Thread(new ChimeHandler(client, chimeMessage, channelMap, televisionMap, socketMap)).start();
        } else {
            logger.error("Registration/Chime Message missing properties");
        }

    }

}
