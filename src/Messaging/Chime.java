package Messaging;

import TV.Channel;
import TV.Television;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by marcleef on 10/28/15.
 * Primary unit of messaging in the system.
 */
public class Chime {
    private Channel channel;
    private String message;
    private Television sender;
    private Timestamp timeReceived;

    /**
     * Constructor for Chime class.
     * @param channel Channel message was sent from.
     * @param sender Television that sent the message.
     **/
    public Chime(Channel channel, Television sender, String message) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        this.timeReceived = new Timestamp(new Date().getTime());
    }

    /**
     * Channel getter.
     * @return Current channel.
     **/
    public Channel getChannel() {
        return channel;
    }

    /**
     * Message getter.
     * @return Raw message.
     **/
    public String getMessage() {
        return message;
    }
}
