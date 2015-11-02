package Handlers;

import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;
import Messaging.Chime;
import TV.Television;
import com.google.gson.*;

/**
 * Created by marcleef on 10/28/15.
 * Handles sent messages by broadcasting them to listening clients.
 */
public class ChimeHandler extends Handler {
    private Socket televisionSocket;
    private Chime chime;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Gson gson;

    /**
     * Constructor for ChimeHandler class.
     * @param televisionSocket Socket of television that sent chime.
     * @param chime Message being sent.
     * @param channelMap Mapping of channels to listening clients.
     * @param televisionMap Mapping of televisions to associated open sockets.
     **/
    public ChimeHandler(Socket televisionSocket, Chime chime, ChannelMap channelMap, TelevisionMap televisionMap) {
        this.televisionSocket = televisionSocket;
        this.chime = chime;
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.gson = new Gson();
    }

    /**
     * Relay new message to all listening clients.
     **/
    public void run() {
        // Get all TVs currently watching given message source channel
        Set<Television> watchingTelevisions = channelMap.get(chime.getChannel());

        // To write chimes to
        Socket currentSocket;
        OutputStreamWriter out;

        // Broadcast message to each watching television
        for(Television television : watchingTelevisions) {
            // Get socket associated with given television
            currentSocket = televisionMap.get(television);
            try {
                // Write json output to socket stream
                out = new OutputStreamWriter(currentSocket.getOutputStream(), StandardCharsets.UTF_8);
                out.write(gson.toJson(chime));

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

}