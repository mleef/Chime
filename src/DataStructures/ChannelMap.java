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
public final class ChannelMap extends ConcurrentHashMap<Channel, Set<Television>> {

    /**
     * Constructor for ChannelMap class.
     **/
    public ChannelMap() {
        super();
    }


    /**
     * Adds television to channel watch set.
     * @param channel Channel to associate new television object with.
     * @param television Television that is being added to channel.
     * @return Updated set of television objects.
     **/
    public Set<Television> putTV(Channel channel, Television television) {
        if(!this.containsKey(channel)) {
            return null;
        } else {
            this.get(channel).add(television);
            return this.get(channel);
        }
    }

    /**
     * Remove television from channel watch set.
     * @param channel Channel to remove television from.
     * @param television Television that is being removed from channel.
     * @return Updated set of television objects.
     **/
    public Set<Television> removeTV(Channel channel, Television television) {
        if(!this.containsKey(channel)) {
            return null;
        } else {
            this.get(channel).remove(television);
            return this.get(channel);
        }
    }



}
