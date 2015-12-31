package Handlers;

import java.util.ArrayList;

import Messaging.ChimeMessage;
import Networking.Endpoints;
import Networking.HttpMessageSender;
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
    private SocketMessageSender socketMessageSender;
    private HttpMessageSender httpMessageSender;
    private String masterUrl;


    /**
     * Constructor for ChimeHandler class.
     * @param chimeMessages Messages being sent.
     * @param socketMessageSender To write messages to sockets.
     * @param httpMessageSender To send HTTP messages to master.
     * @param masterUrl To determine set up (monolith vs. worker/master).
     **/
    public ChimeHandler(ArrayList<ChimeMessage> chimeMessages, SocketMessageSender socketMessageSender, HttpMessageSender httpMessageSender, String masterUrl) {
        this.chimeMessages = chimeMessages;
        this.logger = LoggerFactory.getLogger(ChimeHandler.class);
        this.socketMessageSender = socketMessageSender;
        this.httpMessageSender = httpMessageSender;
        this.masterUrl = masterUrl;
    }

    /**
     * Relay new message to all listening clients.
     **/
    public void run() {
        // Master/worker set up
        if (masterUrl != null) {
            logger.info("Relaying Chime Message(s) to master...");
            for (ChimeMessage message : chimeMessages) {
                try {
                    httpMessageSender.post(masterUrl + Endpoints.CHIME, message);
                } catch (Exception e) {
                    logger.error(e.toString());
                }
            }
        } else {
            // Monolith set up so iterate through all messages/clients and send Chimes
            for (ChimeMessage message : chimeMessages) {
                socketMessageSender.broadcast(message);
            }
        }


    }

}
