package DataStructures;

import TV.Television;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marcleef on 11/18/15.
 * Mapping of televisions to web sockets.
 */
public class TelevisionWSMap extends ConcurrentHashMap<Television, WebSocket> {
    private Logger logger;

    /**
     * Constructor for ChannelMap class.
     **/
    public TelevisionWSMap() {
        super();
        this.logger = LoggerFactory.getLogger(TelevisionWSMap.class);
    }

    @Override
    public WebSocket put(Television key, WebSocket value) {
        logger.info(String.format("Putting: (%s) -> (%s)", key.getId(), value.toString()));
        return super.put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return (key != null && value != null) && this.contains(key) && super.remove(key, value);
    }
}
