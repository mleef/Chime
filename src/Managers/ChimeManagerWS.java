package Managers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Exchanger;

import DataStructures.*;
import Messaging.ChimeMessage;
import Messaging.ClientMessage;
import Messaging.RegistrationMessage;
import TV.Television;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by marcleef on 11/17/15.
 * Logic for the web socket flavor of the Chime Manager.
 */
public class ChimeManagerWS extends WebSocketServer implements Runnable {
    private int portNumber;
    private ChannelMap channelMap;
    private TelevisionWSMap televisionWSMap;
    private WebSocketMap webSocketMap;
    private Gson gson;
    private Logger logger;

    public ChimeManagerWS(int port, ChannelMap channelMap, TelevisionWSMap televisionWSMap, WebSocketMap webSocketMap) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
        this.portNumber = port;
        this.channelMap = channelMap;
        this.televisionWSMap = televisionWSMap;
        this.webSocketMap = webSocketMap;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(ChimeManagerWS.class);
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        logger.info("New Connection: " + conn);
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        logger.info("Closed Connection: " + conn);
        if(webSocketMap.contains(conn)) {
            televisionWSMap.remove(webSocketMap.get(conn));
            webSocketMap.remove(conn);
        }

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
        // Add/update TV's socket
        televisionWSMap.put(registrationMessage.getTelevision(), connection);
        webSocketMap.put(connection, registrationMessage.getTelevision());

        logger.info(String.format("Updating television (%s) socket (%s) in map.", registrationMessage.getTelevision().getId(), connection.toString()));

        // Remove tv from its previously associated channel list if it has one
        if(registrationMessage.getPreviousChannel() != null) {
            logger.info(String.format("Removing television (%s) from previous channel (%s).", registrationMessage.getTelevision().getId(), registrationMessage.getPreviousChannel().getId()));
            channelMap.removeTV(registrationMessage.getPreviousChannel(), registrationMessage.getTelevision());
        }

        logger.info(String.format("Adding television (%s) to channel (%s).", registrationMessage.getTelevision().getId(), registrationMessage.getNewChannel().getId()));

        // Update mappings with new channel
        channelMap.putTV(registrationMessage.getNewChannel(), registrationMessage.getTelevision());
    }

    private void sendChimes(ChimeMessage chimeMessage) {
        // Get all TVs currently watching given message source channel
        Set<Television> watchingTelevisions = channelMap.get(chimeMessage.getChannel());

        logger.info(String.format("Preparing to broadcast message to %d viewers.", watchingTelevisions.size()));

        // To write chimes to
        WebSocket currentSocket;
        OutputStreamWriter out;

        // Broadcast message to each watching television
        for(Television television : watchingTelevisions) {
            // Get socket associated with given television
            currentSocket = televisionWSMap.get(television);
            try {
                // Check if connection is still alive
                if(currentSocket.isOpen()) {
                    // Write json output to socket stream
                    currentSocket.send(ByteBuffer.wrap(gson.toJson(chimeMessage).toString().getBytes()));
                    logger.info(String.format("Successfully sent Chime to %s.", television.getId()));
                } else {
                    logger.error(String.format("Tried to broadcast to closed socket, removing %s from map.", television.getId()));
                    // Update television socket mappings
                    televisionWSMap.remove(television);
                    webSocketMap.remove(currentSocket);
                }

            } catch(Exception e) {
                logger.error(e.toString());
            }
        }
    }


}
