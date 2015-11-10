package Messaging;

import TV.Channel;
import TV.Television;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by marcleef on 10/28/15.
 * Primary unit of messaging in the system.
 */
public class ChimeMessage extends Message {
    private Channel channel;
    private String message;
    private Television sender;
    private String timeSent;

    /**
     * Constructor for Chime class.
     * @param channel Channel message was sent from.
     * @param sender Television that sent the message.
     * @param message String content of Chime message.
     * @param timeSent Timestamp from originating client send.
     **/
    public ChimeMessage(Channel channel, Television sender, String message, String timeSent) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        this.timeSent = timeSent;
    }

    /**
     * Verifies that all fields of object have been deserialized properly.
     * @return True if all fields aren't null, false otherwise.
     **/
    public boolean isValid() {
        return (this.channel != null) && (this.sender != null) && (this.message != null) && (this.timeSent != null);
    }

    /**
     * Sender getter.
     * @return Sender of Chime.
     **/
    public Television getSender() {
        return sender;
    }

    /**
     * Time sent getter.
     * @return Time Chime was sent.
     **/
    public String getTimeSent() {
        return timeSent;
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
