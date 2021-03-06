package Handlers;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import DataStructures.ChannelMap;
import DataStructures.SocketMap;
import DataStructures.TelevisionMap;
import Managers.MapManager;
import Messaging.*;
import Networking.HttpMessageSender;
import Networking.SocketMessageSender;
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
    private String masterUrl;
    private Gson gson;
    private Logger logger;
    private SocketMessageSender socketMessageSender;
    private HttpMessageSender httpMessageSender;
    private MapManager mapper;
    private ArrayList<SocketChannel> registrationClients;
    private ArrayList<RegistrationMessage> registrationMessages;
    private ArrayList<ChimeMessage> chimeMessages;



    /**
     * Constructor for ChimeHandler class.
     * @param clients Socket of television that sent message.
     * @param mapper To manage map updates.
     * @param messages List of client messages to be processed.
     * @param socketMessageSender To write messages to sockets.
     * @param httpMessageSender To send HTTP messages to master.
     * @param masterUrl Determines behavior of handler (worker node vs. monolith).

     **/
    public ConnectionHandler(ArrayList<SocketChannel> clients, ArrayList<String> messages, SocketMessageSender socketMessageSender, HttpMessageSender httpMessageSender, MapManager mapper, String masterUrl) {
        this.clients = clients;
        this.messages = messages;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(ConnectionHandler.class);
        this.registrationClients = new ArrayList<>();
        this.registrationMessages = new ArrayList<>();
        this.chimeMessages = new ArrayList<>();
        this.socketMessageSender = socketMessageSender;
        this.httpMessageSender = httpMessageSender;
        this.mapper = mapper;
        this.masterUrl = masterUrl;
    }

    /**
     * Determine content of client messages and dispatch appropriate handler type(s).
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

                // Classify message to populate lists
                classifyMessage(curClient, chimeMessage, registrationMessage);
            }

            // Dispatch threads to handle new messages
            if(registrationMessages.size() > 0) {
                logger.info("Dispatching Register handler...");
                new Thread(new RegistrationHandler(registrationClients, registrationMessages, mapper, httpMessageSender, masterUrl)).start();
            }
            if(chimeMessages.size() > 0) {
                logger.info("Dispatching Chime handler...");
                new Thread(new ChimeHandler(chimeMessages, socketMessageSender, httpMessageSender, masterUrl)).start();
            }

        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Populates lists of chimes and registrations based on message type.
     * @param client Socket of given client.
     * @param chimeMessage Potentially null chime message to classify.
     * @param registrationMessage Potentially null registration message.
     **/
    private void classifyMessage(SocketChannel client, ChimeMessage chimeMessage, RegistrationMessage registrationMessage) {
        // Update lists based on message type
        if(registrationMessage != null && registrationMessage.isValid()) {
            logger.info(String.format("REGISTRATION - FROM: %s, NEW CHANNEL: %s", registrationMessage.getTelevision().getId(), registrationMessage.getNewChannel().getId()));
            registrationClients.add(client);
            registrationMessages.add(registrationMessage);
        } else if(chimeMessage != null && chimeMessage.isValid()) {
            logger.info(String.format("CHIME - CHANNEL: %s, FROM: %s, TIME SENT: %s MESSAGE: %s", chimeMessage.getChannel().getId(), chimeMessage.getSender().getId(), chimeMessage.getTimeSent(), chimeMessage.getMessage()));
            chimeMessages.add(chimeMessage);
        } else {
            logger.error("Registration/Chime Message missing properties");
        }

    }

}
