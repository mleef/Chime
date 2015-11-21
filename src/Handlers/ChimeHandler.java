package Handlers;

import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import DataStructures.ChannelMap;
import DataStructures.SocketMap;
import DataStructures.TelevisionMap;
import Messaging.ChimeMessage;
import Messaging.MessageSender;
import TV.Television;
import com.google.gson.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * Handles sent messages by broadcasting them to listening clients.
 */
public class ChimeHandler extends Handler {
    private ArrayList<ChimeMessage> chimeMessages;
    private Logger logger;
    private MessageSender sender;


    /**
     * Constructor for ChimeHandler class.
     * @param chimeMessages Messages being sent.
     **/
    public ChimeHandler(ArrayList<ChimeMessage> chimeMessages, MessageSender sender) {
        this.chimeMessages = chimeMessages;
        this.logger = LoggerFactory.getLogger(ChimeHandler.class);
        this.sender = sender;
    }

    /**
     * Relay new message to all listening clients.
     **/
    public void run() {
        logger.info("Beginning Chime broadcast...");
        // Iterate through all messages/clients and send Chimes
        for(ChimeMessage message : chimeMessages) {
            sender.sendChimes(message);
        }


    }

}
