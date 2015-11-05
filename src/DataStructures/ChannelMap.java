package DataStructures;

import TV.Channel;
import TV.Television;

import java.util.HashMap;
import java.util.HashSet;
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
     * Adds new associated channel set.
     * @param channel Channel to initialize in map.
     * @return Updated set of television objects.
     **/
    public Set<Television> addChannel(Channel channel) {
        this.put(channel, new HashSet<Television>());
        return this.get(channel);
    }


    /**
     * Adds television to channel watch set.
     * @param channel Channel to associate new television object with.
     * @param television Television that is being added to channel.
     * @return Updated set of television objects.
     **/
    public Set<Television> putTV(Channel channel, Television television) {
        // Check for channel existence
        if(!this.containsKey(channel)) {
            return null;
        } else {
            // Overwrite existing version if it exists
            if(this.get(channel).contains(television)) {
                this.get(channel).remove(television);
            }
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
        // Check for channel existence
        if(!this.containsKey(channel)) {
            return null;
        } else {
            // Remove television if it exists in teh given channel
            if(this.get(channel).contains(television)) {
                this.get(channel).remove(television);
            }
            return this.get(channel);
        }
    }

    /**
     * Sums all current users.
     * @return Total number of current users.
     **/
    public int getTotalViewers() {
        int totalViewers = 0;

        // Sum length all viewer sets
        for(Channel channel : this.keySet()) {
            totalViewers += this.get(channel).size();
        }

        return totalViewers;

    }



}
