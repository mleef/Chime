package Messaging;

import TV.Channel;
import TV.Television;

import java.sql.Time;
import java.util.Date;

/**
 * Created by marcleef on 10/28/15.
 * Primary unit of messaging in the system.
 */
public class Chime {
    private Channel channel;
    private Television sender;
    private Time timeSent;

    /**
     * Constructor for Chime class.
     * @param channel Channel message was sent from.
     * @param sender Television that sent the message.
     **/
    public Chime(Channel channel, Television sender) {
        this.channel = channel;
        this.sender = sender;
    }
}
