package Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.Exchanger;

import DataStructures.*;
import Messaging.ClientMessage;
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
        webSocketMap.put(conn, null);
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        logger.info("Closed Connection: " + conn);
        webSocketMap.remove(conn);
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        logger.info( conn + ": " + message );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
    }


}
