package Handlers;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;
import Messaging.Registration;
import TV.Channel;
import TV.Television;

import java.net.Socket;

/**
 * Created by marcleef on 10/28/15.
 * Handles the switching of channels and updates map accordingly.
 */
public class RegisterHandler implements Runnable {
    private Socket televisionSocket;
    private Registration registration;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;

    /**
     * Constructor for ChimeHandler class.
     * @param televisionSocket Socket of tv making connection
     * @param registration Registration object that stores channel switch info.
     * @param channelMap Mapping of channels to listening clients.
     **/
    public RegisterHandler(Socket televisionSocket, Registration registration, ChannelMap channelMap, TelevisionMap televisionMap) {
        this.televisionSocket = televisionSocket;
        this.registration = registration;
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
    }

    /**
     * Dispatch thread to adjust television mappings to reflect channel/socket changes.
     **/
    public void run() {
        // Add/update TV's socket
        televisionMap.put(registration.getTelevision(), televisionSocket);

        // Remove tv from its previously associated channel list if it has one
        if(registration.getPreviousChannel() != null) {
            channelMap.removeTV(registration.getPreviousChannel(), registration.getTelevision());
        }

        // Update mappings with new channel
        channelMap.putTV(registration.getNewChannel(), registration.getTelevision());
    }
}
