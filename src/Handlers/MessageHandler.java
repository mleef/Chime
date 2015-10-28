package Handlers;

import DataStructures.ChannelMap;
import Messaging.Chime;

/**
 * Created by marcleef on 10/28/15.
 * Handles sent messages by broadcasting them to listening clients.
 */
public class MessageHandler implements Runnable {
    private Chime chime;
    private ChannelMap channelMap;

    /**
     * Constructor for MessageHandler class.
     * @param message Message being sent.
     * @param channelMap Mapping of channels to listening clients.
     **/
    public MessageHandler(Chime message, ChannelMap channelMap) {
        this.chime = message;
        this.channelMap = channelMap;
    }

    /**
     * TODO: Broadcast message to all listening televisions.
     **/
    public void run() {

    }
}
