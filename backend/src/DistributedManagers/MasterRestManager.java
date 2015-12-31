package DistributedManagers;
import DataStructures.ChannelMap;
import Managers.MapManager;
import Messaging.*;
import Networking.Endpoints;
import Networking.HttpMessageSender;

import TV.Channel;
import TV.Television;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

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
    private HttpMessageSender httpMessageSender;
    private Logger logger;
    private Gson gson;
    private final int SOCKETS_PER_WORKER = 1000;
    private final String WORKER_REQUEST_PORT = "4567";

    /**
     * Constructor for the MasterRestManager class.
     * @param portNumber Default port to listen on.
     * @param channelMap Mapping of channels to watching televisions.
     * @param workerMap Mapping ot worker URLs to associated televisions.
     * @param mapper To manage updates to the various maps.
     * @param httpMessageSender To send HTTP messages to workers.
     **/
    public MasterRestManager(int portNumber, ChannelMap channelMap, ChannelMap workerMap, MapManager mapper, HttpMessageSender httpMessageSender) {
        port(portNumber);
        this.channelMap = channelMap;
        this.workerMap = workerMap;
        this.mapper = mapper;
        this.httpMessageSender = httpMessageSender;
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
            logger.info(String.format("POST: WORKER REGISTRATION - %s", request.url()));
            try {
                // Use default Spark port for workers
                workerMap.addChannel(new Channel(request.ip() + ":" + WORKER_REQUEST_PORT));
                return gson.toJson(new SuccessMessage("Worker Registration confirmed"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Assign new workers to clients
        post(Endpoints.WORKER_ASSIGNMENT, (request, response) -> {
            logger.info(String.format("POST: WORKER REQUESTED - %s", request.url()));
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
            logger.info("POST: CHIME");
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
            logger.info("POST: TV REGISTRATION");
            try {
                RegistrationMessage registrationMessage = gson.fromJson(request.body(), RegistrationMessage.class);
                mapper.addTelevisionToChannel(registrationMessage.getTelevision(), registrationMessage.getPreviousChannel(), registrationMessage.getNewChannel());
                workerMap.putTV(new Channel(request.ip() + ":" + WORKER_REQUEST_PORT), registrationMessage.getTelevision());
                return gson.toJson(new SuccessMessage("Registration confirmed"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Remove television from channel
        post(Endpoints.REMOVE_TELEVISION, (request, response) -> {
            logger.info("POST: TV DELETION");
            try {
                mapper.clearTelevision(new Channel(request.params(":channel")), new Television(request.params(":television")));
                return gson.toJson(new SuccessMessage("Television removed from channel"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Handle worker shut down
        post(Endpoints.WORKER_SHUTDOWN, (request, response) -> {
            logger.info("POST: WORKER SHUTDOWN");
            logger.info("Lost contact with worker, removing from map...");
            try {
                workerMap.remove(new Channel(request.ip() + ":" + WORKER_REQUEST_PORT));
                return new SuccessMessage("Shutting down...");
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage("Failed to remove worker from map."));
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
            // Union worker TVs with watching TVs
            Set<Television> televisions = workerMap.get(channel);
            televisions.retainAll(watchingTelevisions);

            try {
                logger.info(String.format("Worker %s is managing %d relevant televisions, delegating Chime...", channel.getId(), televisions.size()));
                // Relay message to worker
                httpMessageSender.post(channel.getId() + Endpoints.CHIME, new TelevisionsMessage(televisions, chimeMessage));
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
            if(workerMap.get(channel).size() < SOCKETS_PER_WORKER) {
                return channel.getId();
            }
        }
        // All slaves at capacity, can't connect
        return null;
    }


    /**
     * Alerts registered workers of shutdown event.
     **/
    public void shutdown() {
        // Send shutdown message to each worker
        for(Channel channel : workerMap.keySet()) {
            try {
                httpMessageSender.post(channel.getId() + Endpoints.MASTER_SHUTDOWN, null);
            } catch(Exception e) {
                logger.error(e.toString());
            }
        }
    }



}
