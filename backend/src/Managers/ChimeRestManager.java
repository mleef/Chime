package Managers;
import DataStructures.*;
import Messaging.*;
import Networking.SocketMessageSender;
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
    private SocketMessageSender sender;
    private MapManager mapper;
    private ChannelMap channelMap;
    private SocketMap socketMap;
    private TelevisionMap televisionMap;
    private TelevisionWSMap televisionWSMap;
    private WebSocketMap webSocketMap;
    private Gson gson;
    private Logger logger;

    /**
     * Constructor for the ChimeRestManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param socketMap Mapping of sockets to associated televisions.
     * @param televisionMap Mapping of televisions to associated sockets.
     * @param televisionWSMap Mapping of televisions to associated web sockets.
     * @param webSocketMap Mapping of web sockets to associated televisions.
     **/
    public ChimeRestManager(SocketMessageSender sender, MapManager mapper, ChannelMap channelMap, SocketMap socketMap, WebSocketMap webSocketMap, TelevisionMap televisionMap, TelevisionWSMap televisionWSMap) {
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

    /**
     * Listen for various HTTP requests.
     **/
    @Override
    public void run() {
        // Allow cross origin requests
        options("/*", (request,response)->{
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if(accessControlRequestMethod != null){
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // Add headers to response
        before((request,response)->{
            response.header("Access-Control-Allow-Origin", "*");
        });

        // Get list of all channels
        get("/channel/list", (request, response) -> {
            logger.info("GET CHANNELS - ALL");
            try {
                return gson.toJson(channelMap.getChannels());
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Get total (active) viewers on all channels
        get("/channel/count/all", (request, response) -> {
            logger.info("GET VIEWERS - ALL");
            try {
                return gson.toJson(televisionMap.getViewers() + televisionWSMap.getViewers());
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
            logger.info("POST REGISTRATION");
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
