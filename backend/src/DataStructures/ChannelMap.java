package DataStructures;

import TV.Channel;
import TV.Television;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * Modified thread-safe hash map that maps channels to currently listening televisions.
 */
public class ChannelMap extends ConcurrentHashMap<Channel, Set<Television>> {
    private int NUM_VIEWERS = 0;
    private Logger logger;

    /**
     * Constructor for ChannelMap class.
     **/
    public ChannelMap() {
        super();
        this.logger = LoggerFactory.getLogger(ChannelMap.class);
    }

    /**
     * Gets list of all channels.
     * @return All channels in map.
     **/
    public ArrayList<Channel> getChannels() {
        return new ArrayList<>(this.keySet());
    }

    /**
     * Adds new associated channel set.
     * @param channel Channel to initialize in map.
     * @return Updated set of television objects.
     **/
    public Set<Television> addChannel(Channel channel) {
        logger.info(String.format("Adding new channel (%s) to map.", channel.getId()));
        this.put(channel, new HashSet<>());
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
            logger.info(String.format("Television (%s) already exists in channel (%s).", television.getId(), channel.getId()));
        } else {
            logger.info(String.format("Adding new television (%s) to channel (%s).", television.getId(), channel.getId()));
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
            logger.error(String.format("Tried to remove television (%s) from nonexistent channel (%s).", television.getId(), channel.getId()));
            return null;
        } else {
            // Remove television if it exists in the given channel
            if(this.get(channel).contains(television)) {
                logger.info(String.format("Removing television (%s) from channel (%s).", television.getId(), channel.getId()));
                this.get(channel).remove(television);
                NUM_VIEWERS--;
            }
            return this.get(channel);
        }
    }

    /**
     * Returns number of active users for given channel.
     * @param channel Given input channel.
     * @return Number of users viewing input channel.
     **/
    public int getChannelViewers(Channel channel) {
        if(!this.containsKey(channel)) {
            logger.error(String.format("Cannot get viewer count of nonexistent channel (%s).",channel.getId()));
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
