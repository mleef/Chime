package DistributedManagers;
import DataStructures.*;
import Messaging.*;
import Networking.Endpoints;
import Networking.HttpMessageSender;
import Networking.SocketMessageSender;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;
/**
 * Created by marcleef on 12/22/15.
 * Manage HTTP requests to maintain consistency with master.
 */
public class WorkerRestManager implements Runnable {
    private SocketMessageSender socketMessageSender;
    private HttpMessageSender httpMessageSender;
    private TelevisionMap televisionMap;
    private TelevisionWSMap televisionWSMap;
    private Gson gson;
    private Logger logger;
    private String masterURL;

    /**
     * Constructor for the SlaveRestManager class.
     * @param socketMessageSender For writing messages to sockets.
     * @param httpMessageSender For sending HTTP messages to master.
     * @param televisionMap Mapping of televisions to associated sockets.
     * @param televisionWSMap Mapping of televisions to associated web sockets.
     * @param masterURL URL of master node.
     **/
    public WorkerRestManager(SocketMessageSender socketMessageSender, HttpMessageSender httpMessageSender, TelevisionMap televisionMap, TelevisionWSMap televisionWSMap, String masterURL) {
        this.socketMessageSender = socketMessageSender;
        this.televisionMap = televisionMap;
        this.televisionWSMap = televisionWSMap;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(WorkerRestManager.class);
        this.httpMessageSender = httpMessageSender;
        this.masterURL = masterURL;
    }

    @Override
    /**
     * Listen for various HTTP requests.
     **/
    public void run() {
        // Allow cross origin requests
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // Add headers to response
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        // Get list of all televisions managed by this node
        get(Endpoints.TELEVISIONS, (request, response) -> {
            logger.info("GET: TELEVISIONS - ALL");
            try {
                return gson.toJson(televisionMap.getTelevisions().addAll(televisionWSMap.getTelevisions()));
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Get list of all socket connected televisions managed by this node
        get(Endpoints.SOCKETS, (request, response) -> {
            logger.info("GET: TELEVISIONS - SOCKETS");
            try {
                return gson.toJson(televisionMap.getTelevisions());
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Get list of all web socket connected televisions managed by this node
        get(Endpoints.WEB_SOCKETS, (request, response) -> {
            logger.info("GET: TELEVISIONS - WEB SOCKETS");
            try {
                return gson.toJson(televisionWSMap.getTelevisions());
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Send Chimes to televisions.
        post(Endpoints.CHIME, (request, response) -> {
            logger.info("POST: CHIME");
            try {
                TelevisionsMessage televisionsMessage = gson.fromJson(request.body(), TelevisionsMessage.class);
                socketMessageSender.broadcast(televisionsMessage.getTelevisions(), televisionsMessage.getChimeMessage());
                return gson.toJson(new SuccessMessage("Chime sent"));
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Handle master shut down
        post(Endpoints.MASTER_SHUTDOWN, (request, response) -> {
            logger.info("POST: MASTER SHUTDOWN");
            logger.info("Lost contact with master, shutting down...");
            System.exit(0);
            return new SuccessMessage("Shutting down...");
        });
    }

    /**
     * Alerts master of shutdown event.
     **/
    public void shutdown() {
        try {
            httpMessageSender.post(masterURL + Endpoints.WORKER_SHUTDOWN, null);
        } catch(Exception e) {
            logger.error(e.toString());
        }
    }
}
