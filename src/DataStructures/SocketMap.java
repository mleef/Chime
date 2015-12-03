package DataStructures;

import TV.Television;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marcleef on 11/16/15.
 * Mapping of socket channels to televisions.
 */
public class SocketMap extends ConcurrentHashMap<SocketChannel, Television> {
    private Logger logger;

    /**
     * Constructor for SocketMap class.
     **/
    public SocketMap() {
        super();
        this.logger = LoggerFactory.getLogger(SocketMap.class);
    }

    @Override
    public Television put(SocketChannel key, Television value) {
        logger.info(String.format("Putting: (%s) -> (%s)", key.toString(), value.getId()));
        return super.put(key, value);
    }

}
