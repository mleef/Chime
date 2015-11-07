package DataStructures;

import TV.Channel;
import TV.Television;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * Modified thread-safe hash map that maps channels to currently listening televisions.
 */
public class ChannelMap extends ConcurrentHashMap<Channel, Set<Television>> {
    private int NUM_VIEWERS = 0;
    private int NUM_CHANNELS = 0;
    private Logger logger;

    /**
     * Constructor for ChannelMap class.
     **/
    public ChannelMap() {
        super();
        this.logger = LoggerFactory.getLogger(ChannelMap.class);
    }

    /**
     * Adds new associated channel set.
     * @param channel Channel to initialize in map.
     * @return Updated set of television objects.
     **/
    public Set<Television> addChannel(Channel channel) {
        logger.info("Adding new channel to map.", channel);
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
            logger.info("Overwriting existing television.",channel, television);
            this.get(channel).remove(television);
            this.get(channel).add(television);
        } else {
            logger.info("Adding new television.",channel, television);
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
            logger.error("Tried to remove television from nonexistent channel.",channel, television);
            return null;
        } else {
            // Remove television if it exists in the given channel
            if(this.get(channel).contains(television)) {
                logger.info("Removing television.",channel, television);
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
            logger.error("Cannot get viewers of nonexistent channel.",channel);
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
