package Managers;
import DataStructures.*;
import Messaging.MessageSender;
import TV.Channel;
import TV.Television;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

/**
 * Created by marcleef on 12/2/15.
 * Exposes map functions via RESTful API
 */
public class ChimeRestManager implements Runnable {
    private MessageSender sender;
    private MapManager mapper;
    private ChannelMap channelMap;
    private SocketMap socketMap;
    private TelevisionMap televisionMap;
    private TelevisionWSMap televisionWSMap;
    private WebSocketMap webSocketMap;
    private Gson gson;
    private Logger logger;

    /**
     * Constructor for the MapManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param socketMap Mapping of sockets to associated televisions.
     * @param televisionMap Mapping of televisions to associated sockets.
     * @param televisionWSMap Mapping of televisions to associated web sockets.
     * @param webSocketMap Mapping of web sockets to associated televisions.
     **/
    public ChimeRestManager(MessageSender sender, MapManager mapper, ChannelMap channelMap, SocketMap socketMap, WebSocketMap webSocketMap, TelevisionMap televisionMap, TelevisionWSMap televisionWSMap) {
        this.sender = sender;
        this.mapper = mapper;
        this.channelMap = channelMap;
        this.socketMap = socketMap;
        this.webSocketMap = webSocketMap;
        this.televisionMap = televisionMap;
        this.televisionWSMap = televisionWSMap;
        gson = new Gson();
        this.logger = LoggerFactory.getLogger(MapManager.class);
    }

    public void run() {
        // Get televisions watching queried channel
        get("/channel/watching/:channel", (request, response) -> gson.toJson(channelMap.get(new Channel(request.params(":channel")))));

        // Get number of viewers on given channel
        get("/channel/count/:channel", (request, response) -> gson.toJson(channelMap.getChannelViewers(new Channel(request.params(":channel")))));

        // Process registration messages from client
        post("/television/registration/:television/:previousChannel/:newChannel", (request, response) -> {
            mapper.addTelevisionToChannel(new Television(request.params(":television")), new Channel(request.params("previousChannel")), new Channel(request.params("newChannel")));
            return gson.toJson(channelMap.get(new Channel(request.params("newChannel"))));
        });


    }
}
