package DistributedManagers;
import DataStructures.*;
import Managers.MapManager;
import Messaging.*;
import Networking.SocketMessageSender;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;
/**
 * Created by marcleef on 12/22/15.
 * Manage HTTP requests to maintain consistency with master.
 */
public class SlaveRestManager implements Runnable {
    private SocketMessageSender sender;
    private TelevisionMap televisionMap;
    private TelevisionWSMap televisionWSMap;
    private Gson gson;
    private Logger logger;

    /**
     * Constructor for the SlaveRestManager class.
     * @param televisionMap Mapping of televisions to associated sockets.
     * @param televisionWSMap Mapping of televisions to associated web sockets.
     **/
    public SlaveRestManager(SocketMessageSender sender,TelevisionMap televisionMap, TelevisionWSMap televisionWSMap) {
        this.sender = sender;
        this.televisionMap = televisionMap;
        this.televisionWSMap = televisionWSMap;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(SlaveRestManager.class);
    }

    @Override
    /**
     * Listen for various HTTP requests.
     **/
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

        // Get list of all televisions managed by this node
        get("/television", (request, response) -> {
            logger.info("GET TELEVISIONS - ALL");
            try {
                return gson.toJson(televisionMap.getTelevisions().addAll(televisionWSMap.getTelevisions()));
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Get list of all socket connected televisions managed by this node
        get("/television/sockets", (request, response) -> {
            logger.info("GET TELEVISIONS - SOCKETS");
            try {
                return gson.toJson(televisionMap.getTelevisions());
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Get list of all web socket connected televisions managed by this node
        get("/television/websockets", (request, response) -> {
            logger.info("GET TELEVISIONS - WEB SOCKETS");
            try {
                return gson.toJson(televisionWSMap.getTelevisions());
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Send Chimes to televisions.
        post("/television/chime", (request, response) -> {
            logger.info("POST CHIME");
            try {
                TelevisionsMessage televisionsMessage = gson.fromJson(request.body(), TelevisionsMessage.class);
                sender.broadcast(televisionsMessage.getTelevisions(), televisionsMessage.getChimeMessage());
                return gson.toJson(new SuccessMessage("Chime sent"));
            } catch(Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });


    }
}
