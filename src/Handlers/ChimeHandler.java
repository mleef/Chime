package Handlers;

import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import DataStructures.ChannelMap;
import DataStructures.SocketMap;
import DataStructures.TelevisionMap;
import Messaging.ChimeMessage;
import TV.Television;
import com.google.gson.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * Handles sent messages by broadcasting them to listening clients.
 */
public class ChimeHandler extends Handler {
    private ArrayList<SocketChannel> televisionSockets;
    private ArrayList<ChimeMessage> chimeMessages;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private SocketMap socketMap;
    private Gson gson;
    private Logger logger;


    /**
     * Constructor for ChimeHandler class.
     * @param televisionSockets Sockets of television that sent chimes.
     * @param chimeMessages Messages being sent.
     * @param channelMap Mapping of channels to listening clients.
     * @param televisionMap Mapping of televisions to associated open sockets.
     **/
    public ChimeHandler(ArrayList<SocketChannel> televisionSockets, ArrayList<ChimeMessage> chimeMessages, ChannelMap channelMap, TelevisionMap televisionMap, SocketMap socketMap) {
        this.televisionSockets = televisionSockets;
        this.chimeMessages = chimeMessages;
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.socketMap = socketMap;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(ChimeHandler.class);
    }

    /**
     * Relay new message to all listening clients.
     **/
    public void run() {
        ChimeMessage curChimeMessage;
        SocketChannel curClient;

        // Iterate through all messages/clients and send Chimes
        for(int i = 0; i < chimeMessages.size(); i++) {
            curChimeMessage = chimeMessages.get(i);
            //curClient = televisionSockets.get(i);

            // Get all TVs currently watching given message source channel
            Set<Television> watchingTelevisions = channelMap.get(curChimeMessage.getChannel());

            logger.info(String.format("Preparing to broadcast message to %d viewers.", watchingTelevisions.size()));

            // To write chimes to
            SocketChannel currentSocket;
            OutputStreamWriter out;

            // Broadcast message to each watching television
            for(Television television : watchingTelevisions) {
                // Get socket associated with given television
                currentSocket = televisionMap.get(television);
                try {
                    // Check if connection is still alive
                    if(currentSocket != null && currentSocket.isConnected()) {
                        // Write json output to socket stream
                        currentSocket.write(ByteBuffer.wrap(gson.toJson(curChimeMessage).toString().getBytes()));
                        logger.info(String.format("Successfully sent Chime to %s.", television.getId()));
                    } else {
                        logger.error(String.format("Tried to broadcast to closed socket, removing %s from map.", television.getId()));
                        // Update television socket mappings
                        televisionMap.remove(television);
                        socketMap.remove(currentSocket);
                        channelMap.removeTV(curChimeMessage.getChannel(), television);
                    }

                } catch(Exception e) {
                    logger.error(e.toString());
                }
            }
        }

    }

}
