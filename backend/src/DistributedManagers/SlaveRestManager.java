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
    private MapManager mapper;
    private ChannelMap channelMap;
    private SocketMap socketMap;
    private TelevisionMap televisionMap;
    private TelevisionWSMap televisionWSMap;
    private WebSocketMap webSocketMap;
    private Gson gson;
    private Logger logger;

    /**
     * Constructor for the SlaveRestManager class.
     * @param channelMap Mapping of channels to watching televisions.
     * @param socketMap Mapping of sockets to associated televisions.
     * @param televisionMap Mapping of televisions to associated sockets.
     * @param televisionWSMap Mapping of televisions to associated web sockets.
     * @param webSocketMap Mapping of web sockets to associated televisions.
     **/
    public SlaveRestManager(SocketMessageSender sender, MapManager mapper, ChannelMap channelMap, SocketMap socketMap, WebSocketMap webSocketMap, TelevisionMap televisionMap, TelevisionWSMap televisionWSMap) {
        this.sender = sender;
        this.mapper = mapper;
        this.channelMap = channelMap;
        this.socketMap = socketMap;
        this.webSocketMap = webSocketMap;
        this.televisionMap = televisionMap;
        this.televisionWSMap = televisionWSMap;
        gson = new Gson();
        this.logger = LoggerFactory.getLogger(SlaveRestManager.class);
    }

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

        before((request,response)->{
            response.header("Access-Control-Allow-Origin", "*");
        });

        // Get list of all televisions managed by this node
        get("/televisions", (request, response) -> {
            logger.info("GET TELEVISIONS - ALL");
            try {
                return gson.toJson(televisionMap.getTelevisions().addAll(televisionWSMap.getTelevisions()));
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Get list of all socket connected televisions managed by this node
        get("/televisions/sockets", (request, response) -> {
            logger.info("GET TELEVISIONS - SOCKETS");
            try {
                return gson.toJson(televisionMap.getTelevisions());
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Get list of all web socket connected televisions managed by this node
        get("/televisions/websockets", (request, response) -> {
            logger.info("GET TELEVISIONS - WEB SOCKETS");
            try {
                return gson.toJson(televisionWSMap.getTelevisions());
            } catch (Exception e) {
                logger.error(e.toString());
                return gson.toJson(new ErrorMessage(e.toString()));
            }
        });

        // Send Chimes to televisions.
        post("/televisions/chime", (request, response) -> {
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
