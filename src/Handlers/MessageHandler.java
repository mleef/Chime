package Handlers;

import java.util.Set;
import DataStructures.ChannelMap;
import Messaging.Chime;
import TV.Television;

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
     * Relay new message to all listening clients.
     **/
    public void run() {
        // Get all TVs currently watching given message source channel
        Set<Television> tvList = channelMap.get(chime.getChannel());

        // Get message to broadcast (should be JSONified)
        String message = chime.getMessage();

        for(Television recipient : tvList) {
            // broadcast message
        }
    }
}
