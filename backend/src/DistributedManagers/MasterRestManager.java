package DistributedManagers;
import DataStructures.ChannelMap;
import Managers.MapManager;
import Messaging.*;
import Networking.HttpMessageSender;

import TV.Channel;
import TV.Television;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static spark.Spark.*;
import static spark.Spark.delete;
import static spark.Spark.post;

/**
 * Created by marcleef on 12/22/15.
 * Manage HTTP requests that coordinate behaviors of ChimeSlave(s).
 */
public class MasterRestManager implements Runnable {
    private ChannelMap channelMap;
    private ChannelMap slaveMap;
    private MapManager mapper;
    private HttpMessageSender sender;
    private Logger logger;
    private Gson gson;

    /**
     * Constructor for the MasterRestManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param mapper To manage updates to the various maps.
     **/
    public MasterRestManager(ChannelMap channelMap, ChannelMap slaveMap, MapManager mapper, HttpMessageSender sender) {
        this.channelMap = channelMap;
        this.slaveMap = slaveMap;
        this.mapper = mapper;
        this.sender = sender;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(MasterRestManager.class);
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


        // Process new slave registrations
        post("/slave/register", (request, response) -> {
            logger.info("POST SLAVE REGISTRATION");
            try {
                slaveMap.addChannel(new Channel(request.url()));
                return gson.toJson(new SuccessMessage("Slave Registration confirmed"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Send Chimes
        post("/television/chime", (request, response) -> {
            logger.info("POST CHIME");
            try {
                sender.broadcast(gson.fromJson(request.body(), ChimeMessage.class));
                return gson.toJson(new SuccessMessage("Chimes sent"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Process new television registrations
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
            logger.info("POST DELETION - %s: (%s -> %s)", request.params(":television"), request.params(":previousChannel"), request.params(":newChannel"));
            try {
                mapper.clearTelevision(new Channel(request.params(":channel")), new Television(request.params(":television")));
                return gson.toJson(new SuccessMessage("Television removed from channel"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

    }

}
