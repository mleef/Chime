package DataStructures;

import TV.Channel;
import TV.Television;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marcleef on 10/28/15.
 * Modified thread-safe hash map that maps channels to currently listening televisions.
 */
public class ChannelMap extends ConcurrentHashMap<Channel, Set<Television>> {

    /**
     * Constructor for ChannelMap class.
     **/
    public ChannelMap() {
        super();
    }

}
