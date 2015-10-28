package TV;

/**
 * Created by marcleef on 10/28/15.
 * Organizes channel metadata and information.
 */
public class Channel {
    private String id;

    /**
     * Constructor for Channel class.
     * @param id Unique channel identifier (could just be the channel number).
     * @return New channel object.
     **/
    public Channel(String id) {
        this.id = id;
    }


    /**
     * Override equals so equality is just based on id.
     **/
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Channel) {
            Channel ch = (Channel) obj;
            return this.id.equals(ch.id);
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
