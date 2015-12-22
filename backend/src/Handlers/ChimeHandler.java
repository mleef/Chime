package Handlers;

import java.util.ArrayList;

import Messaging.ChimeMessage;
import Networking.SocketMessageSender;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * Handles sent messages by broadcasting them to listening clients.
 */
public class ChimeHandler extends Handler {
    private ArrayList<ChimeMessage> chimeMessages;
    private Logger logger;
    private SocketMessageSender sender;


    /**
     * Constructor for ChimeHandler class.
     * @param chimeMessages Messages being sent.
     **/
    public ChimeHandler(ArrayList<ChimeMessage> chimeMessages, SocketMessageSender sender) {
        this.chimeMessages = chimeMessages;
        this.logger = LoggerFactory.getLogger(ChimeHandler.class);
        this.sender = sender;
    }

    /**
     * Relay new message to all listening clients.
     **/
    public void run() {
        // Iterate through all messages/clients and send Chimes
        for(ChimeMessage message : chimeMessages) {
            sender.broadcast(message);
        }


    }

}
