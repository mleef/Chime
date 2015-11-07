package DataStructures;

import TV.Channel;
import TV.Television;

import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marcleef on 10/28/15.
 * Mapping of televisions to their open sockets.
 */
public final class TelevisionMap extends ConcurrentHashMap<Television, Socket> {


}
