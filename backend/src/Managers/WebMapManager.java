package Managers;
import DataStructures.*;
import TV.Channel;
import TV.Television;
import com.google.gson.Gson;
import org.slf4j.Logger;

import static spark.Spark.*;

/**
 * Created by marcleef on 12/2/15.
 * Exposes map functions via RESTful API
 */
public class WebMapManager {
    private MapManager mapper;
    private ChannelMap channelMap;
    private SocketMap socketMap;
    private TelevisionMap televisionMap;
    private TelevisionWSMap televisionWSMap;
    private WebSocketMap webSocketMap;
    private Logger logger;
    private Gson gson;

    public WebMapManager() {
        this.channelMap = new ChannelMap();
        this.socketMap =  new SocketMap();
        this.webSocketMap = new WebSocketMap();
        this.televisionMap = new TelevisionMap();
        this.televisionWSMap = new TelevisionWSMap();
        this.mapper = new MapManager(channelMap, socketMap, webSocketMap, televisionMap, televisionWSMap);
        this.gson = new Gson();
    }

    public void start() {
        // Get televisions watching queried channel
        get("/channel/watching/:channel", (request, response) -> gson.toJson(channelMap.get(new Channel(request.params(":channel")))));

        // Get number of viewers on given channel
        get("/channel/count/:channel", (request, response) -> gson.toJson(channelMap.getChannelViewers(new Channel(request.params(":channel")))));

        // Process registration messages from client
        post("/television/registration/:television/:previousChannel/:newChannel", (request, response) -> {
            mapper.addTelevisionToChannel(new Television(request.params(":television")), new Channel(request.params("previousChannel")), new Channel(request.params("newChannel")));
            return gson.toJson("Success");
        });


    }
}
