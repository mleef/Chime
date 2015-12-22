package DataStructures;

import TV.Channel;
import TV.Television;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marcleef on 12/22/15.
 * Maintains mapping of ChimeSlave URLs to registered televisions on that node.
 */
public class SlaveMap extends ConcurrentHashMap<String, Set<Television>> {
    private Logger logger;

    /**
     * Constructor for SlaveMap class.
     **/
    public SlaveMap() {
        super();
        this.logger = LoggerFactory.getLogger(SlaveMap.class);
    }

    @Override
    public Set<Television> put(String key, Set<Television> value) {
        logger.info(String.format("Putting: (%s) -> (%s)", key.toString(), value.size()));
        return super.put(key, value);
    }

    /**
     * Associates television with ChimeSlave.
     * @param slaveURL URL of slave's map to add television to.
     * @param television Television that is being added to slave's mapping.
     * @return Updated set of television objects.
     **/
    public Set<Television> putTV(String slaveURL, Television television) {
        // Check for channel existence and create if it doesn't exist
        if(!this.containsKey(slaveURL)) {
            return null;
        }

        // Overwrite existing version if it exists
        if(this.get(slaveURL).contains(television)) {
            logger.info(String.format("Television (%s) already exists in slave's (%s) map.", television.getId(), slaveURL));
        } else {
            logger.info(String.format("Adding new television (%s) to slave's (%s) map.", television.getId(), slaveURL));
            this.get(slaveURL).add(television);
        }

        return this.get(slaveURL);
    }
}
