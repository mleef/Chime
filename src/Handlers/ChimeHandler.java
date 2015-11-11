package Handlers;

import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import DataStructures.ChannelMap;
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
    private SocketChannel televisionSocket;
    private ChimeMessage chimeMessage;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Gson gson;
    private Logger logger;


    /**
     * Constructor for ChimeHandler class.
     * @param televisionSocket Socket of television that sent chime.
     * @param chimeMessage Message being sent.
     * @param channelMap Mapping of channels to listening clients.
     * @param televisionMap Mapping of televisions to associated open sockets.
     **/
    public ChimeHandler(SocketChannel televisionSocket, ChimeMessage chimeMessage, ChannelMap channelMap, TelevisionMap televisionMap) {
        this.televisionSocket = televisionSocket;
        this.chimeMessage = chimeMessage;
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(ChimeHandler.class);
    }

    /**
     * Relay new message to all listening clients.
     **/
    public void run() {
        // Get all TVs currently watching given message source channel
        Set<Television> watchingTelevisions = channelMap.get(chimeMessage.getChannel());

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
                if(currentSocket.isConnected()) {
                    // Write json output to socket stream
                    currentSocket.write(ByteBuffer.wrap(gson.toJson(chimeMessage).toString().getBytes()));
                    logger.info(String.format("Successfully sent Chime to %s.", television.getId()));
                } else {
                    logger.error(String.format("Tried to broadcast to closed socket, removing %s from map.", television.getId()));
                    televisionMap.remove(television);
                }

            } catch(Exception e) {
                logger.error(e.toString());
                e.printStackTrace();
            }
        }
    }

}
