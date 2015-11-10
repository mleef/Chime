package Messaging;

/**
 * Created by marcleef on 10/28/15.
 * To handle client messaging inputs.
 */
public class ClientMessage extends Message {
    private ChimeMessage chimeMessage;
    private RegistrationMessage registrationMessage;

    public ClientMessage(ChimeMessage chimeMessage, RegistrationMessage registrationMessage) {
        this.chimeMessage = chimeMessage;
        this.registrationMessage = registrationMessage;
    }

    /**
     * Verifies that all fields of object have been deserialized properly.
     * @return True if one of chimeMessage or registrationMessage is not null.
     **/
    public boolean isValid() {
        return chimeMessage != null || registrationMessage != null;
    }

    public ChimeMessage getChimeMessage() {
        return chimeMessage;
    }

    public RegistrationMessage getRegistrationMessage() {
        return registrationMessage;
    }
}
