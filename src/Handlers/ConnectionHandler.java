package Handlers;

import java.io.*;
import java.nio.channels.SocketChannel;

import DataStructures.ChannelMap;
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
    private SocketChannel client;
    private String message;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Gson gson;
    private Logger logger;
    private InputStream inputStream;
    private OutputStream outputStream;


    /**
     * Constructor for ChimeHandler class.
     * @param client Socket of television that sent message.
     * @param channelMap Mapping of channels to listening clients.
     * @param televisionMap Mapping of televisions to associated open sockets.
     **/
    public ConnectionHandler(SocketChannel client, String message, ChannelMap channelMap, TelevisionMap televisionMap) {
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.client = client;
        this.message = message;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(ConnectionHandler.class);
    }

    /**
     * Determine content of client message and dispatch appropriate handler type.
     **/
    public void run() {
        try {
            // Get socket's input stream
            // BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // Deserialize message into Message object instance
            ClientMessage clientMessage = gson.fromJson(message, ClientMessage.class);

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
            dispatchThread(chimeMessage, registrationMessage);

        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
    }

    private void dispatchThread(ChimeMessage chimeMessage, RegistrationMessage registrationMessage) {
        // Dispatch appropriate thread based on message type
        if(registrationMessage != null && registrationMessage.isValid()) {
            logger.info(String.format("REGISTRATION - FROM: %s, NEW CHANNEL: %s", registrationMessage.getTelevision().getId(), registrationMessage.getNewChannel().getId()));
            logger.info("Dispatching new Register handler.");
            new Thread(new RegisterHandler(client, registrationMessage, channelMap, televisionMap)).start();
        } else if(chimeMessage != null && chimeMessage.isValid()) {
            logger.info(String.format("CHIME - CHANNEL: %s, FROM: %s, TIME SENT: %s MESSAGE: %s", chimeMessage.getChannel().getId(), chimeMessage.getSender().getId(), chimeMessage.getTimeSent(), chimeMessage.getMessage()));
            logger.info("Dispatching new Chime handler.");
            new Thread(new ChimeHandler(client, chimeMessage, channelMap, televisionMap)).start();
        } else {
            logger.error("Registration/Chime Message missing properties");
        }

    }

}
