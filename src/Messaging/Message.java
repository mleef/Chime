package Messaging;

/**
 * Created by marcleef on 10/28/15.
 */
public class Message {
    private Chime chime;
    private Registration registration;

    public Message(Chime chime, Registration registration) {
        this.chime = chime;
        this.registration = registration;
    }

    public Chime getChime() {
        return chime;
    }

    public Registration getRegistration() {
        return registration;
    }
}
