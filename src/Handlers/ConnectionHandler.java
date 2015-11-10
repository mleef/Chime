package Handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;
import Messaging.*;
import Messaging.ErrorMessage;
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
            ClientMessage clientMessage = gson.fromJson(in, ClientMessage.class);

            // Check for proper object properties
            if(clientMessage == null  || !clientMessage.isValid()) {
                sendError("Invalid Client Message");
                logger.info("Aborting thread...");
                Thread.currentThread().interrupt();
                return;
            }

            // Get values contained in message to dispatch appropriate thread
            ChimeMessage chimeMessage = clientMessage.getChimeMessage();
            RegistrationMessage registrationMessage = clientMessage.getRegistrationMessage();

            // Dispatch appropriate worker thread based on message type
            dispatchThread(chimeMessage, registrationMessage);


        } catch(Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
    }

    private void sendError(String error) {
        // Log malformed request
        logger.error(String.format("Malformed request: %s", error));

        // Send message to client indicating bad message
        try {
            OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8);
            out.write(gson.toJson(new ErrorMessage("Invalid Request", "Some properties are missing.")));
            out.flush();
        } catch(Exception e) {
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
            sendError("Registration/Chime Message missing properties");
        }

    }

}
