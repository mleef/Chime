package Messaging;

import TV.Television;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by marcleef on 11/9/15.
 * To handle success messages relayed to the client.
 */
public class TelevisionsMessage extends Message {
    private Set<Television> televisions;
    private ChimeMessage chimeMessage;

    public TelevisionsMessage(Set<Television> televisions, ChimeMessage chimeMessage) {
        this.televisions = televisions;
        this.chimeMessage = chimeMessage;
    }

    /**
     * Verifies that all fields of object have been deserialized properly.
     * @return True if all fields aren't null, false otherwise.
     **/
    public boolean isValid() {
        return this.televisions != null;
    }

    /**
     * Gets list of televisions.
     * @return List of contained television objects.
     **/
    public Set<Television> getTelevisions() {
        return televisions;
    }

    /**
     * Gets associate ChimeMessage.
     * @return Chime message to send.
     **/
    public ChimeMessage getChimeMessage() {
        return chimeMessage;
    }
}
