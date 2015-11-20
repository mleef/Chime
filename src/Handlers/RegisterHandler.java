package Handlers;

import DataStructures.ChannelMap;
import DataStructures.SocketMap;
import DataStructures.TelevisionMap;
import Messaging.RegistrationMessage;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * Handles the switching of channels and updates map accordingly.
 */
public class RegisterHandler extends Handler {
    private ArrayList<SocketChannel> televisionSockets;
    private ArrayList<RegistrationMessage> registrationMessages;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private SocketMap socketMap;
    private Logger logger;

    /**
     * Constructor for ChimeHandler class.
     * @param televisionSockets Sockets of TVs making connections.
     * @param registrationMessages Registration objects that store channel switch info.
     * @param channelMap Mapping of channels to listening clients.
     * @param televisionMap Mapping of televisions to associated open sockets.
     **/
    public RegisterHandler(ArrayList<SocketChannel> televisionSockets, ArrayList<RegistrationMessage> registrationMessages, ChannelMap channelMap, TelevisionMap televisionMap, SocketMap socketMap) {
        this.televisionSockets = televisionSockets;
        this.registrationMessages = registrationMessages;
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.socketMap = socketMap;
        this.logger = LoggerFactory.getLogger(RegisterHandler.class);
    }

    /**
     * Dispatch thread to adjust television mappings to reflect channel/socket changes.
     **/
    public void run() {
        RegistrationMessage curRegistrationMessage;
        SocketChannel curClient;

        for(int i = 0; i < registrationMessages.size(); i++) {
            curRegistrationMessage = registrationMessages.get(i);
            curClient = televisionSockets.get(i);

            // Add/update TV's socket
            televisionMap.put(curRegistrationMessage.getTelevision(), curClient);
            socketMap.put(curClient, curRegistrationMessage.getTelevision());

            logger.info(String.format("Updating television (%s) socket (%s) in map.", curRegistrationMessage.getTelevision().getId(), curClient.toString()));

            // Remove tv from its previously associated channel list if it has one
            if(curRegistrationMessage.getPreviousChannel() != null) {
                logger.info(String.format("Removing television (%s) from previous channel (%s).", curRegistrationMessage.getTelevision().getId(), curRegistrationMessage.getPreviousChannel().getId()));
                channelMap.removeTV(curRegistrationMessage.getPreviousChannel(), curRegistrationMessage.getTelevision());
            }

            logger.info(String.format("Adding television (%s) to channel (%s).", curRegistrationMessage.getTelevision().getId(), curRegistrationMessage.getNewChannel().getId()));

            // Update mappings with new channel
            channelMap.putTV(curRegistrationMessage.getNewChannel(), curRegistrationMessage.getTelevision());
        }



    }
}
