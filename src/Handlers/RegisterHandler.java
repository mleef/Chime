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
     **/
    public RegisterHandler(Channel previousChannel, Channel newChannel, Television television, ChannelMap channelMap) {
        this.previousChannel = previousChannel;
        this.newChannel = newChannel;
        this.television = television;
        this.channelMap = channelMap;
    }

    /**
     * Dispatch thread to adjust television mappings to reflect channel changes.
     **/
    public void run() {
        // Remove tv from its previously associated channel list if it has one
        if(previousChannel != null) {
            channelMap.removeTV(previousChannel, television);
        }

        // Update mappings with new channel
        channelMap.putTV(newChannel, television);
    }
}
