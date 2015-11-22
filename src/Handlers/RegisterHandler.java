package Handlers;

import DataStructures.ChannelMap;
import DataStructures.SocketMap;
import DataStructures.TelevisionMap;
import Managers.MapManager;
import Messaging.MessageSender;
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
    private Logger logger;
    private MapManager mapper;

    /**
     * Constructor for ChimeHandler class.
     * @param televisionSockets Sockets of TVs making connections.
     * @param registrationMessages Registration objects that store channel switch info.
     **/
    public RegisterHandler(ArrayList<SocketChannel> televisionSockets, ArrayList<RegistrationMessage> registrationMessages, MapManager mapper) {
        this.televisionSockets = televisionSockets;
        this.registrationMessages = registrationMessages;
        this.logger = LoggerFactory.getLogger(RegisterHandler.class);
        this.mapper = mapper;
    }

    /**
     * Dispatch thread to adjust television mappings to reflect channel/socket changes.
     **/
    public void run() {
        // Adjust mappings according to new registration messages
        for(int i = 0; i < registrationMessages.size(); i++) {
            mapper.moveTelevision(registrationMessages.get(i), televisionSockets.get(i));
        }
    }
}
