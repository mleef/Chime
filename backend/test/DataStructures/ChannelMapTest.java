package DataStructures;

import TV.Channel;
import TV.Television;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by marcleef on 11/2/15.
 */
public class ChannelMapTest {
    private ChannelMap channelMap;
    private Channel c1;
    private Channel c2;
    private Channel c3;
    private Channel c4;


    @Before
    public void setUp() throws Exception {
        this.channelMap = new ChannelMap();
        this.c1 = new Channel("1");
        this.c2 = new Channel("2");
        this.c3 = new Channel("3");
        this.c4 = new Channel("4");
        channelMap.addChannel(c1);
        channelMap.addChannel(c2);
        channelMap.addChannel(c3);
        channelMap.addChannel(c4);

    }

    @Test
    public void testPutTV() throws Exception {
        channelMap.putTV(c1, new Television("tv-1"));
        channelMap.putTV(c1, new Television("tv-2"));
        channelMap.putTV(c1, new Television("tv-3"));
        channelMap.putTV(c1, new Television("tv-4"));

        // Check that all TVs populated properly
        assertEquals(channelMap.get(c1).size(), 4);

        // Check that nothing was added to other channels
        assertEquals(channelMap.get(c2).size(), 0);
        assertEquals(channelMap.get(c3).size(), 0);
        assertEquals(channelMap.get(c4).size(), 0);

        // Check that accessing nonexistent channel returns null
        assertNull(channelMap.get(new Channel("5")));
    }

    @Test
    public void testRemoveTV() throws Exception {
        Television tv1 = new Television("tv-1");
        Television tv2 = new Television("tv-2");
        Television tv3 = new Television("tv-3");
        Television tv4 = new Television("tv-4");

        channelMap.putTV(c1, tv1);
        channelMap.putTV(c1, tv2);
        channelMap.putTV(c1, tv3);
        channelMap.putTV(c1, tv4);

        channelMap.removeTV(c1, tv1);
        channelMap.removeTV(c1, tv2);
        channelMap.removeTV(c1, tv3);
        channelMap.removeTV(c1, tv4);

        // Check that all tv objects were removed
        assertEquals(channelMap.get(c1).size(), 0);

        // Check that invalid channels return null
        assertNull(channelMap.removeTV(new Channel("5"), tv1));

        // Check that trying to remove nonexistent TV doesn't error
        assertNotNull(channelMap.removeTV(c1, new Television("tv-5")));
    }
}