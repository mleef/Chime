package Handlers;

import Managers.MapManager;
import Networking.Endpoints;
import Messaging.RegistrationMessage;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import Networking.HttpMessageSender;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * Handles the switching of channels and updates map accordingly.
 */
public class RegistrationHandler extends Handler {
    private ArrayList<SocketChannel> televisionSockets;
    private ArrayList<RegistrationMessage> registrationMessages;
    private Logger logger;
    private MapManager mapper;
    private HttpMessageSender httpMessageSender;
    private String masterUrl;

    /**
     * Constructor for ChimeHandler class.
     *
     * @param televisionSockets    Sockets of TVs making connections.
     * @param registrationMessages Registration objects that store channel switch info.
     * @param mapper               To manage map updates.
     * @param httpMessageSender    To send HTTP messages to master.
     * @param masterUrl            Determines behavior of manager (worker node vs. monolith)
     **/
    public RegistrationHandler(ArrayList<SocketChannel> televisionSockets, ArrayList<RegistrationMessage> registrationMessages, MapManager mapper, HttpMessageSender httpMessageSender, String masterUrl) {
        this.televisionSockets = televisionSockets;
        this.registrationMessages = registrationMessages;
        this.logger = LoggerFactory.getLogger(RegistrationHandler.class);
        this.mapper = mapper;
        this.masterUrl = masterUrl;
        this.httpMessageSender = httpMessageSender;
    }

    /**
     * Dispatch thread to adjust television mappings to reflect channel/socket changes.
     **/
    public void run() {

        if (masterUrl != null) {
            logger.info("Relaying Registration Message(s) to master...");
            for (int i = 0; i < registrationMessages.size(); i++) {
                try {
                    mapper.addTelevision(registrationMessages.get(i).getTelevision(), televisionSockets.get(i));
                    httpMessageSender.post(masterUrl + Endpoints.TV_REGISTRATION, registrationMessages.get(i));
                } catch (Exception e) {
                    logger.error(e.toString());
                }
            }
        } else {
            // Adjust mappings according to new registration messages
            for (int i = 0; i < registrationMessages.size(); i++) {
                mapper.moveTelevision(registrationMessages.get(i), televisionSockets.get(i));
            }
        }


    }
}
