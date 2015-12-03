package Messaging;

import TV.Channel;
import TV.Television;


/**
 * Created by marcleef on 10/28/15.
 * To handle registration messages from the client.
 */
public class RegistrationMessage extends Message {
    private Channel previousChannel;
    private Channel newChannel;
    private Television television;

    public RegistrationMessage(Channel previousChannel, Channel newChannel, Television television) {
        this.previousChannel = previousChannel;
        this.newChannel = newChannel;
        this.television = television;
    }

    /**
     * Verifies that all fields of object have been deserialized properly.
     * @return True if all fields aren't null, false otherwise.
     **/
    public boolean isValid() {
        return (this.newChannel != null) && (this.television != null);
    }

    public Channel getPreviousChannel() { return previousChannel; }

    public Channel getNewChannel() {
        return newChannel;
    }

    public Television getTelevision() {
        return television;
    }
}

