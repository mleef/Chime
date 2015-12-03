package DataStructures;

import TV.Channel;
import TV.Television;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Set;
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

}
