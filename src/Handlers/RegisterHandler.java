package Handlers;

import DataStructures.ChannelMap;
import TV.Channel;
import TV.Television;

/**
 * Created by marcleef on 10/28/15.
 * Handles the switching of channels and updates map accordingly.
 */
public class RegisterHandler {
    private Channel previousChannel;
    private Channel newChannel;
    private Television television;
    private ChannelMap channelMap;

    /**
     * Constructor for MessageHandler class.
     * @param previousChannel Previously associated channel.
     * @param newChannel Currently watched channel.
     * @param television Television sending the registration message.
     * @param channelMap Mapping of channels to listening clients.
     * @return New register handler instance.
     **/
    public RegisterHandler(Channel previousChannel, Channel newChannel, Television television, ChannelMap channelMap) {
        this.previousChannel = previousChannel;
        this.newChannel = newChannel;
        this.television = television;
        this.channelMap = channelMap;
    }

    /**
     * TODO: Update mappings to reflect changes.
     **/
    public void run() {

    }
}
