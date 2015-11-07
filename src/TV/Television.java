package TV;

/**
 * Created by marcleef on 10/28/15.
 * Organizes television metadata and associated socket.
 */
public class Television {
    private String id;

    /**
     * Constructor for Television class.
     * @param id Unique television identifier.
     **/
    public Television(String id) {
        this.id = id;
    }

    /**
     * Getter for television id.
     * @return ID of television.
     **/
    public String getId() {
        return id;
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
