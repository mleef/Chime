package DataStructures;

import TV.Channel;
import TV.Television;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by marcleef on 10/28/15.
 * Modified hash map that maps channels to currently listening televisions.
 */
public class ChannelMap extends HashMap<Channel, Set<Television>> {

    /**
     * Constructor for ChannelMap class.
     * @return New channel map object.
     **/
    public ChannelMap() {
        super();
    }

}
