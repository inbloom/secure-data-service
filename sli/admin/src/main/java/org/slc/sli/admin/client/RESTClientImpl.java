package org.slc.sli.admin.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * 
 * @author pwolf
 */
public class RESTClientImpl implements RESTClient {
    
    private static Logger logger = LoggerFactory.getLogger(RESTClientImpl.class);    

    @Override
    public JsonArray getRoles(String token) {
        return makeJsonRequest("admin/roles", null).getAsJsonArray();
    }
    
    /**
     * Make a request to a REST service and convert the result to JSON
     * @param path the unique portion of the requested REST service URL
     * @param token not used yet
     * @return a {@link JsonElement} if the request is successful and returns valid JSON, otherwise null.
     */
    private JsonElement makeJsonRequest(String path, String token) {
        //TODO: figure out how to un-hardcode the server and path running API
        RestTemplate template = new RestTemplate();
        String url = "http://localhost:8080/api/rest/" + path;
        String jsonText = template.getForObject(url, String.class);
        JsonParser parser = new JsonParser();
        return parser.parse(jsonText);
    }

}
