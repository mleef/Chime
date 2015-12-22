package DistributedManagers;
import DataStructures.ChannelMap;
import DataStructures.SlaveMap;
import Managers.MapManager;
import Networking.HttpMessageSender;

import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.*;
/**
 * Created by marcleef on 12/22/15.
 * Manage HTTP requests that coordinate behaviors of ChimeSlave(s).
 */
public class MasterRestManager implements Runnable {
    private ChannelMap channelMap;
    private SlaveMap slaveMap;
    private MapManager mapper;
    private HttpMessageSender sender;

    /**
     * Constructor for the MasterRestManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param mapper To manage updates to the various maps.
     **/
    public MasterRestManager(ChannelMap channelMap, SlaveMap slaveMap, MapManager mapper, HttpMessageSender sender) {
        this.channelMap = channelMap;
        this.slaveMap = slaveMap;
        this.mapper = mapper;
        this.sender = sender;
    }

    /**
     * Listen for various HTTP requests.
     **/
    @Override
    public void run() {

    }
}
