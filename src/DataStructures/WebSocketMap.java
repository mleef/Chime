package DataStructures;

import TV.Television;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marcleef on 11/18/15.
 * Mapping of web sockets to watching televisions.
 */
public class WebSocketMap extends ConcurrentHashMap<WebSocket, Television> {
    private Logger logger;

    /**
     * Constructor for SocketMap class.
     **/
    public WebSocketMap() {
        super();
        this.logger = LoggerFactory.getLogger(WebSocketMap.class);
    }

    @Override
    public Television put(WebSocket key, Television value) {
        logger.info(String.format("Putting: (%s) -> (%s)", key.toString(), value.getId()));
        return super.put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return this.contains(key) && super.remove(key, value);
    }
}
