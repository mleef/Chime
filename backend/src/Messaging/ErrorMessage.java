package Messaging;

/**
 * Created by marcleef on 11/9/15.
 * To handle error messages relayed to the client.
 */
public class ErrorMessage extends Message {
    private String error;

    public ErrorMessage(String error) {
        this.error = error;
    }

    /**
     * Verifies that all fields of object have been deserialized properly.
     * @return True if all fields aren't null, false otherwise.
     **/
    public boolean isValid() {
        return this.error != null;
    }
}
