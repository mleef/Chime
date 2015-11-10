package Messaging;

/**
 * Created by marcleef on 11/9/15.
 * To handle error messages relayed to the client.
 */
public class ErrorMessage extends Message {
    private String type;
    private String info;

    public ErrorMessage(String type, String info) {
        this.type = type;
        this.info = info;
    }

    /**
     * Verifies that all fields of object have been deserialized properly.
     * @return True if all fields aren't null, false otherwise.
     **/
    public boolean isValid() {
        return (this.type != null) && (this.info != null);
    }
}
