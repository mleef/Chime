package Networking;

/**
 * Created by marcleef on 12/23/15.
 * Contains paths for easier request construction.
 */
public class Endpoints {
    public static final String TELEVISIONS = "/television";
    public static final String SOCKETS = "/television/sockets";
    public static final String WEB_SOCKETS = "/television/websockets";
    public static final String CHIME = "/television/chime";
    public static final String TV_REGISTRATION = "/television/register";
    public static final String WORKER_REGISTRATION = "/worker/register";
    public static final String WORKER_ASSIGNMENT = "/worker/assignment";
    public static final String LIST_CHANNELS = "/channel/list";
    public static final String COUNT_ALL_CHANNELS = "/channel/count/all";
    public static final String COUNT_CHANNEL = "/channel/count/:channel";
    public static final String WATCHING_TELEVISIONS = "/channel/watching/:channel";
    public static final String MASTER_SHUTDOWN = "/master/shutdown";
    public static final String WORKER_SHUTDOWN = "/worker/shutdown";
    public static final String REMOVE_TELEVISION = "/television/delete/:television/:channel";
}
