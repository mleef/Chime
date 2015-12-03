package Managers;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import Messaging.ChimeMessage;
import Messaging.ClientMessage;
import Messaging.MessageSender;
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
public class ChimeManagerWS extends WebSocketServer implements Runnable {
    private Gson gson;
    private Logger logger;
    private MessageSender sender;
    private MapManager mapManager;

    public ChimeManagerWS(int port, MessageSender sender, MapManager mapManager) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(ChimeManagerWS.class);
        this.sender = sender;
        this.mapManager = mapManager;
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        logger.info("New Connection: " + conn);
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        logger.info("Closed Connection: " + conn);
        mapManager.clearTelevisionWS(conn);

    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        logger.info( conn + ": " + message );
        processMessage(conn, message);
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        logger.error(ex.toString());
    }

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
            updateMappings(conn, registrationMessage);
        } else if(chimeMessage != null && chimeMessage.isValid()) {
            logger.info(String.format("CHIME - CHANNEL: %s, FROM: %s, TIME SENT: %s MESSAGE: %s", chimeMessage.getChannel().getId(), chimeMessage.getSender().getId(), chimeMessage.getTimeSent(), chimeMessage.getMessage()));
            sendChimes(chimeMessage);
        } else {
            logger.error("Registration/Chime Message missing properties");
        }

    }

    private void updateMappings(WebSocket connection, RegistrationMessage registrationMessage) {
        mapManager.moveTelevision(registrationMessage, connection);
    }

    private void sendChimes(ChimeMessage chimeMessage) {
        sender.broadcast(chimeMessage);
    }


}
