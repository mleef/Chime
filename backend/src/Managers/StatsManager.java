package Managers;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;

import java.util.TimerTask;

import TV.Channel;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 11/6/15.
 * To manage the garbage collection of unused sockets.
 */
public class StatsManager extends TimerTask {
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Logger logger;

    /**
     * Constructor for the CleanupManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param televisionMap Mapping of televisions to respective sockets.
     **/
    public StatsManager(ChannelMap channelMap, TelevisionMap televisionMap) {
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.logger = LoggerFactory.getLogger(StatsManager.class);
    }

    @Override
    public void run() {
        logger.info("Total viewers (Channel Map): " + channelMap.getTotalViewers());
        for(Channel channel : channelMap.keySet()) {
            logger.info(String.format("Channel %s Viewers: %s", channel.getId(),channelMap.get(channel)));
        }
        logger.info("Total viewers (Television Map): " + televisionMap.keySet().size());
    }
}
