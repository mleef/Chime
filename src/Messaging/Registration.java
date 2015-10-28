package Messaging;

import TV.Channel;
import TV.Television;


/**
 * Created by marcleef on 10/28/15.
 */
public class Registration {
    private Channel previousChannel;
    private Channel newChannel;
    private String televisionId;

    public Registration(Channel previousChannel, Channel newChannel, String televisionId) {
        this.previousChannel = previousChannel;
        this.newChannel = newChannel;
        this.televisionId = televisionId;
    }
}

