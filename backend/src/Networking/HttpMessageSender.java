package Networking;

import DataStructures.ChannelMap;
import Managers.MapManager;
import Messaging.ChimeMessage;
import Messaging.Message;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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

    public HttpMessageSender() {
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(HttpMessageSender.class);
    }

    public void postMessage(String dest, Message message) {

    }
}
