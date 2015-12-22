package Networking;

import DataStructures.ChannelMap;
import Managers.MapManager;
import Messaging.ChimeMessage;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;

/**
 * Created by marcleef on 12/22/15.
 * Handles message interfacing between Chime slaves and the master.
 */
public class HttpMessageSender {
    private ChannelMap channelMap;
    private ChannelMap slaveMap;
    private MapManager mapper;
    private Gson gson;
    private Logger logger;

    public HttpMessageSender(ChannelMap channelMap, ChannelMap slaveMap, MapManager mapper) {
        this.channelMap = channelMap;
        this.slaveMap = slaveMap;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(HttpMessageSender.class);
        this.mapper = mapper;
    }
    public void broadcast(ChimeMessage chimeMessage) {

    }
}
