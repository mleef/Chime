package TV;

import java.net.Socket;

/**
 * Created by marcleef on 10/28/15.
 * Organizes television metadata and associated socket.
 */
public class Television {
    private String id;
    private Socket socket;

    /**
     * Constructor for Television class.
     * @param id Unique television identifier.
     * @param socket Socket to broadcast messages to.
     **/
    public Television(String id, Socket socket) {
        this.id = id;
        this.socket = socket;
    }

    /**
     * Override equals so equality is just based on id.
     **/
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Television) {
            Television tv = (Television) obj;
            return this.id.equals(tv.id);
        }
        return false;
    }

    /**
     * Override hashcode to preserve equals consistency.
     **/
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
