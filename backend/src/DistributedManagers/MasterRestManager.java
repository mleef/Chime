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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.*;
import static spark.Spark.delete;
import static spark.Spark.post;

/**
 * Created by marcleef on 12/22/15.
 * Manage HTTP requests that coordinate behaviors of ChimeSlave(s).
 */
public class MasterRestManager implements Runnable {
    private ChannelMap channelMap;
    private ChannelMap workerMap;
    private MapManager mapper;
    private HttpMessageSender sender;
    private Logger logger;
    private Gson gson;
    private final int SOCKETS_PER_WORKER = 1000;

    /**
     * Constructor for the MasterRestManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param mapper To manage updates to the various maps.
     **/
    public MasterRestManager(int portNumber, ChannelMap channelMap, ChannelMap workerMap, MapManager mapper, HttpMessageSender sender) {
        port(portNumber);
        this.channelMap = channelMap;
        this.workerMap = workerMap;
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

        // Process new worker registrations
        post(Endpoints.WORKER_REGISTRATION, (request, response) -> {
            logger.info(String.format("POST SLAVE REGISTRATION - %s", request.url()));
            try {
                workerMap.addChannel(new Channel(request.ip()));
                return gson.toJson(new SuccessMessage("Slave Registration confirmed"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Assign new workers to clients
        post(Endpoints.WORKER_ASSIGNMENT, (request, response) -> {
            logger.info(String.format("POST SLAVE REGISTRATION - %s", request.url()));
            try {
                SuccessMessage successMessage = new SuccessMessage(getNextWorker());
                // If there are available workers
                if(successMessage != null) {
                    return gson.toJson(successMessage);
                } else {
                    return gson.toJson(new ErrorMessage("System at capacity"));
                }
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Send Chimes
        post(Endpoints.CHIME, (request, response) -> {
            logger.info("POST CHIME");
            try {
                sendChimes(gson.fromJson(request.body(), ChimeMessage.class));
                return gson.toJson(new SuccessMessage("Chimes sent"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Process new television registrations
        post(Endpoints.TV_REGISTRATION, (request, response) -> {
            logger.info("POST REGISTRATION");
            try {
                SuccessMessage successMessage;
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


    /**
     * Determine which workers contain the relevant televisions and forward message.
     * @param chimeMessage Message to send to clients.
     **/
    private void sendChimes(ChimeMessage chimeMessage) {
        // Get currently watching televisions
        Set<Television> watchingTelevisions = channelMap.get(chimeMessage.getChannel());

        for(Channel channel : workerMap.keySet()) {
            // Union slave TVs with watching TVs
            Set<Television> televisions = workerMap.get(channel);
            televisions.retainAll(watchingTelevisions);

            try {
                // Relay message to worker
                sender.post(channel.getId(), new TelevisionsMessage(televisions, chimeMessage));
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
    }

    /**
     * Hackey "load balancing", direct clients to machines based on number of sockets
     * @return URL of client to connect with.
     **/
    private String getNextWorker() {
        for(Channel channel : workerMap.keySet()) {
            if(workerMap.getTotalViewers() < SOCKETS_PER_WORKER) {
                return channel.getId();
            }
        }
        // All slaves at capacity, can't connect
        return null;
    }



}
