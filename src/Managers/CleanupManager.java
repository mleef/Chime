package Managers;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;

import java.util.Date;
import java.util.TimerTask;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 11/6/15.
 * To manage the garbage collection of unused sockets.
 */
public class CleanupManager extends TimerTask {
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Logger logger;

    /**
     * Constructor for the CleanupManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param televisionMap Mapping of televisions to respective sockets.
     **/
    public CleanupManager(ChannelMap channelMap, TelevisionMap televisionMap) {
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.logger = LoggerFactory.getLogger(CleanupManager.class);
    }

    @Override
    public void run() {
        logger.info("Total viewers: " + channelMap.getTotalViewers());
    }
}
