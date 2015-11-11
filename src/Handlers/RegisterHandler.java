package Handlers;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;
import Messaging.RegistrationMessage;

import java.net.Socket;
import java.nio.channels.SocketChannel;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * Handles the switching of channels and updates map accordingly.
 */
public class RegisterHandler extends Handler {
    private SocketChannel televisionSocket;
    private RegistrationMessage registrationMessage;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Logger logger;

    /**
     * Constructor for ChimeHandler class.
     * @param televisionSocket Socket of tv making connection
     * @param registrationMessage Registration object that stores channel switch info.
     * @param channelMap Mapping of channels to listening clients.
     * @param televisionMap Mapping of televisions to associated open sockets.
     **/
    public RegisterHandler(SocketChannel televisionSocket, RegistrationMessage registrationMessage, ChannelMap channelMap, TelevisionMap televisionMap) {
        this.televisionSocket = televisionSocket;
        this.registrationMessage = registrationMessage;
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.logger = LoggerFactory.getLogger(RegisterHandler.class);
    }

    /**
     * Dispatch thread to adjust television mappings to reflect channel/socket changes.
     **/
    public void run() {
        // Add/update TV's socket
        televisionMap.put(registrationMessage.getTelevision(), televisionSocket);

        logger.info(String.format("Updating television (%s) socket (%s) in map.", registrationMessage.getTelevision().getId(), televisionSocket.toString()));

        // Remove tv from its previously associated channel list if it has one
        if(registrationMessage.getPreviousChannel() != null) {
            logger.info(String.format("Removing television (%s) from previous channel (%s).", registrationMessage.getTelevision().getId(), registrationMessage.getPreviousChannel().getId()));
            channelMap.removeTV(registrationMessage.getPreviousChannel(), registrationMessage.getTelevision());
        }

        logger.info(String.format("Adding television (%s) to channel (%s).", registrationMessage.getTelevision().getId(), registrationMessage.getNewChannel().getId()));

        // Update mappings with new channel
        channelMap.putTV(registrationMessage.getNewChannel(), registrationMessage.getTelevision());


    }
}
