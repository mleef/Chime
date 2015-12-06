package Messaging;

/**
 * Created by marcleef on 11/9/15.
 * To handle success messages relayed to the client.
 */
public class SuccessMessage extends Message {
    private String success;

    public SuccessMessage(String success) {
        this.success = success;
    }

    /**
     * Verifies that all fields of object have been deserialized properly.
     * @return True if all fields aren't null, false otherwise.
     **/
    public boolean isValid() {
        return this.success != null;
    }
}
