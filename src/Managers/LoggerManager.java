package Managers;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;

import java.util.Date;
import java.util.TimerTask;

/**
 * Created by marcleef on 11/6/15.
 * To manage the collection and writing of various statistics about the system.
 */
public class LoggerManager extends TimerTask {
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;

    public LoggerManager(ChannelMap channelMap, TelevisionMap televisionMap) {
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
    }

    @Override
    public void run() {
        System.out.println("Total Viewers: " + channelMap.getTotalViewers());
    }
}
