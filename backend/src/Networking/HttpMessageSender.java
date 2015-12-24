package Networking;

import DataStructures.ChannelMap;
import Managers.MapManager;
import Messaging.ChimeMessage;
import Messaging.Message;
import com.google.gson.Gson;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


/**
 * Created by marcleef on 12/22/15.
 * Handles message interfacing between Chime slaves and the master.
 */
public class HttpMessageSender {
    private ChannelMap channelMap;
    private ChannelMap slaveMap;
    private MapManager mapper;
    private Gson gson;
    private Logger logger;
    private CloseableHttpClient client;
    private final String USER_AGENT = "Mozilla/5.0";

    public HttpMessageSender() {
        this.gson = new Gson();
        this.logger = LoggerFactory.getLogger(HttpMessageSender.class);
        this.client = HttpClientBuilder.create().build();
    }

    public String post(String url, Message message) throws Exception {
        HttpPost request = new HttpPost(url);
        if(message != null) {
            StringEntity params = new StringEntity(gson.toJson(message));
            request.setEntity(params);
        }

        request.addHeader("content-type", "application/json");
        HttpResponse result = client.execute(request);
        return EntityUtils.toString(result.getEntity(), "UTF-8");
    }

    public String get(String url) throws Exception {
        HttpGet request = new HttpGet(url);
        request.addHeader("content-type", "application/json");
        HttpResponse result = client.execute(request);
        return EntityUtils.toString(result.getEntity(), "UTF-8");
    }
}
