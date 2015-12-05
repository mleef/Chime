package Managers;
import DataStructures.*;
import Messaging.*;
import TV.Channel;
import TV.Television;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

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
        this.logger = LoggerFactory.getLogger(ChimeRestManager.class);
    }

    public void run() {
        // Get total viewers on all channels
        get("/channel/count/all", (request, response) -> {
            logger.info("GET VIEWERS - ALL");
            try {
                return gson.toJson(channelMap.getTotalViewers());
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Get number of viewers on given channel
        get("/channel/count/:channel", (request, response) -> {
            logger.info(String.format("GET # VIEWERS - %s", request.params(":channel")));
            try {
                return gson.toJson(channelMap.getChannelViewers(new Channel(request.params(":channel"))));
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }

        });

        // Get televisions watching queried channel
        get("/channel/watching/:channel", (request, response) -> {
            logger.info(String.format("GET VIEWERS - %s", request.params(":channel")));
            try {
                return gson.toJson(channelMap.get(new Channel(request.params(":channel"))));
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Process registration messages from client
        post("/television/register", (request, response) -> {
            logger.info(String.format("POST REGISTRATION - %s: (%s -> %s)", request.params(":television"), request.params(":previousChannel"), request.params(":newChannel")));
            try {
                RegistrationMessage registrationMessage = gson.fromJson(request.body(), RegistrationMessage.class);
                mapper.addTelevisionToChannel(registrationMessage.getTelevision(), registrationMessage.getPreviousChannel(), registrationMessage.getNewChannel());
                return gson.toJson(new SuccessMessage("Registration confirmed"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Remove television from channel
        delete("/television/register/:television/:channel", (request, response) -> {
            logger.info(String.format("POST REGISTRATION - %s: (%s -> %s)", request.params(":television"), request.params(":previousChannel"), request.params(":newChannel")));
            try {
                mapper.clearTelevision(new Channel(request.params(":channel")), new Television(request.params(":television")));
                return gson.toJson(new SuccessMessage("Television removed from channel"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Send Chimes
        post("/television/chime", (request, response) -> {
            logger.info("POST CHIME");
            try {
                sender.broadcast(gson.fromJson(request.body(), ClientMessage.class).getChimeMessage());
                return gson.toJson(new SuccessMessage("Chime sent"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });





    }
}
