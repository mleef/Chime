package DataStructures;

import TV.Channel;
import TV.Television;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marcleef on 10/28/15.
 * Mapping of televisions to their open sockets.
 */
public final class TelevisionMap extends ConcurrentHashMap<Television, SocketChannel> {
    private Logger logger;

    /**
     * Constructor for ChannelMap class.
     **/
    public TelevisionMap() {
        super();
        this.logger = LoggerFactory.getLogger(TelevisionMap.class);
    }

    @Override
    public SocketChannel put(Television key, SocketChannel value) {
        logger.info(String.format("Putting: (%s) -> (%s)", key.getId(), value.toString()));
        return super.put(key, value);
    }

    /**
     * Number of viewers in the map.
     * @return # of viewers.
     **/
    public int getViewers() {
        return this.keySet().size();
    }

    /**
     * Gets list of all televisions.
     * @return All televisions in map.
     **/
    public ArrayList<Television> getTelevisions() {
        return new ArrayList<>(this.keySet());
    }

}
