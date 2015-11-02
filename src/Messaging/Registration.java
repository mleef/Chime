package Messaging;

import TV.Channel;
import TV.Television;


/**
 * Created by marcleef on 10/28/15.
 */
public class Registration {
    private Channel previousChannel;
    private Channel newChannel;
    private Television television;

    public Registration(Channel previousChannel, Channel newChannel, Television television) {
        this.previousChannel = previousChannel;
        this.newChannel = newChannel;
        this.television = television;
    }

    public Channel getPreviousChannel() { return previousChannel; }

    public Channel getNewChannel() {
        return newChannel;
    }

    public Television getTelevision() {
        return television;
    }
}

