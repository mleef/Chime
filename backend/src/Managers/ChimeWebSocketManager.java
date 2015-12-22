package Managers;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import Messaging.ChimeMessage;
import Messaging.ClientMessage;
import Networking.SocketMessageSender;
import Messaging.RegistrationMessage;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by marcleef on 11/17/15.
 * Logic for the web socket flavor of the Chime Manager.
 */
public class ChimeWebSocketManager extends WebSocketServer implements Runnable {
    private Gson gson;
    private Logger logger;
    private SocketMessageSender sender;
    private MapManager mapper;
    private boolean isWorker;

    /**
     * Constructor for the ChimeWebSocketManager class.
     * @param port Port to listen on.
     * @param sender To handle message sending.
     * @param mapper To handle map updates.
     **/
    public ChimeWebSocketManager(int port, SocketMessageSender sender, MapManager mapper, boolean isWorker) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(ChimeWebSocketManager.class);
        this.sender = sender;
        this.mapper = mapper;
        this.isWorker = isWorker;
    }

    /**
     * Event handler for new connections.
     * @param conn New WebSocket connection object.
     * @param handshake Handshake to initiate the connection
     **/
    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        logger.info("New Connection: " + conn);
    }

    /**
     * Event handler for closed connections.
     * @param conn WebSocket to close.
     * @param code Status code.
     * @param reason Justification for close.
     * @param remote Remote flag.
     **/
    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        logger.info("Closed Connection: " + conn);
        mapper.clearTelevisionWS(conn);
    }

    /**
     * Event handler for new messages.
     * @param conn WebSocket connection object.
     * @param message New data to be read from client.
     **/
    @Override
    public void onMessage( WebSocket conn, String message ) {
        logger.info( conn + ": " + message );
        processMessage(conn, message);
    }

    /**
     * Event handler for errors.
     * @param conn WebSocket connection object emitting error.
     * @param err Error type.
     **/
    @Override
    public void onError( WebSocket conn, Exception err ) {
        logger.error(err.toString());
    }

    /**
     * To deserialize client messages and handle them appropriately
     * @param conn WebSocket connection object that sent the data.
     * @param message Data to process.
     **/
    private void processMessage(WebSocket conn, String message) {
        ClientMessage clientMessage;
        // Deserialize message into Message object instance
        try {
            clientMessage = gson.fromJson(message, ClientMessage.class);
        } catch (Exception e) {
            logger.error(e.toString());
            return;
        }

        // Check for proper object properties
        if (clientMessage == null || !clientMessage.isValid()) {
            logger.error("Invalid Client Message");
            return;
        }

        // Get values contained in message to dispatch appropriate thread
        ChimeMessage chimeMessage = clientMessage.getChimeMessage();
        RegistrationMessage registrationMessage = clientMessage.getRegistrationMessage();

        // Determine next action based on message type
        if(registrationMessage != null && registrationMessage.isValid()) {
            logger.info(String.format("REGISTRATION - FROM: %s, NEW CHANNEL: %s", registrationMessage.getTelevision().getId(), registrationMessage.getNewChannel().getId()));
            mapper.moveTelevision(registrationMessage, conn);
        } else if(chimeMessage != null && chimeMessage.isValid()) {
            logger.info(String.format("CHIME - CHANNEL: %s, FROM: %s, TIME SENT: %s MESSAGE: %s", chimeMessage.getChannel().getId(), chimeMessage.getSender().getId(), chimeMessage.getTimeSent(), chimeMessage.getMessage()));
            sender.broadcast(chimeMessage);
        } else {
            logger.error("Registration/Chime Message missing properties");
        }

    }

}
