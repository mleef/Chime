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
public class ChannelMap extends ConcurrentHashMap<Channel, Set<Television>> {
    private int NUM_VIEWERS = 0;
    private int NUM_CHANNELS = 0;

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
        // Check for channel existence and create if it doesn't exist
        if(!this.containsKey(channel)) {
            this.addChannel(channel);
        }

        // Overwrite existing version if it exists
        if(this.get(channel).contains(television)) {
            this.get(channel).remove(television);
            this.get(channel).add(television);
        } else {
            this.get(channel).add(television);
            NUM_VIEWERS++;
        }

        return this.get(channel);

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
            // Remove television if it exists in the given channel
            if(this.get(channel).contains(television)) {
                this.get(channel).remove(television);
                NUM_VIEWERS--;
            }
            return this.get(channel);
        }
    }

    /**
     * Returns number of active users for given channel.
     * @return Number of users viewing input channel.
     **/
    public int getChannelViewers(Channel channel) {
        if(!this.containsKey(channel)) {
            return -1;
        }
        // Return number of watching televisions for channel
        return this.get(channel).size();
    }

    /**
     * Returns total number of active users.
     * @return Total number of current users.
     **/
    public int getTotalViewers() {
        return NUM_VIEWERS;
    }



}
