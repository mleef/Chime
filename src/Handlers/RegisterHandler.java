package Handlers;

import DataStructures.ChannelMap;
import DataStructures.TelevisionMap;
import Messaging.Registration;
import TV.Channel;
import TV.Television;
import com.google.gson.Gson;

import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by marcleef on 10/28/15.
 * Handles the switching of channels and updates map accordingly.
 */
public class RegisterHandler extends Handler {
    private Socket televisionSocket;
    private Registration registration;
    private ChannelMap channelMap;
    private TelevisionMap televisionMap;
    private Gson gson;
    private Logger logger;

    /**
     * Constructor for ChimeHandler class.
     * @param televisionSocket Socket of tv making connection
     * @param registration Registration object that stores channel switch info.
     * @param channelMap Mapping of channels to listening clients.
     * @param televisionMap Mapping of televisions to associated open sockets.
     **/
    public RegisterHandler(Socket televisionSocket, Registration registration, ChannelMap channelMap, TelevisionMap televisionMap) {
        this.televisionSocket = televisionSocket;
        this.registration = registration;
        this.channelMap = channelMap;
        this.televisionMap = televisionMap;
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(RegisterHandler.class);
    }

    /**
     * Dispatch thread to adjust television mappings to reflect channel/socket changes.
     **/
    public void run() {
        // Add/update TV's socket
        televisionMap.put(registration.getTelevision(), televisionSocket);
        logger.info("Updating television socket in map: ", registration.getTelevision(), televisionSocket);

        // Remove tv from its previously associated channel list if it has one
        if(registration.getPreviousChannel() != null) {
            logger.info("Removing television from previous channel set", registration.getPreviousChannel(), registration.getTelevision());
            channelMap.removeTV(registration.getPreviousChannel(), registration.getTelevision());
        }

        logger.info("Adding television to new channel set", registration.getNewChannel(), registration.getTelevision());
        // Update mappings with new channel
        channelMap.putTV(registration.getNewChannel(), registration.getTelevision());

        /*
        // To write back to client
        Socket currentSocket;
        OutputStreamWriter out;

        try {
            // Check if connection is still alive
            if(!televisionSocket.isClosed()) {
                // Write json output to socket stream
                out = new OutputStreamWriter(televisionSocket.getOutputStream(), StandardCharsets.UTF_8);
                out.write("Received.\n");
                out.flush();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        */


    }
}
