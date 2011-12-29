package org.slc.sli.admin.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.slc.sli.admin.util.UrlBuilder;

/**
 * 
 * @author pwolf
 */
@Component("RESTClient")
public class RESTClient {
    
    /** Request parameter key used to pass sessionId to API **/
    private static final String API_SESSION_KEY = "sessionId";
    
    private static Logger logger = LoggerFactory.getLogger(RESTClient.class);
    
    /** URI for the API **/
    @Value("${apiServerUri}")
    private String apiServerUri;
    
    /**
     * Get the Roles and Rights information from the API
     * 
     * @param token
     *            the sessionId
     * @return JsonArray object as described by API documentation
     * @throws NoSessionException
     */
    public JsonArray getRoles(String token) {
        return makeJsonRequest("admin/roles", null).getAsJsonArray();
    }
    
    /**
     * Call the session/check API
     * 
     * @param token
     *            the sessionId or null
     * @return JsonOject as described by API documentation
     * @throws NoSessionException
     */
    public JsonObject sessionCheck(String token) {
        return makeJsonRequest("system/session/check", token).getAsJsonObject();
    }
    
    /**
     * Make a request to a REST service and convert the result to JSON
     * 
     * @param path
     *            the unique portion of the requested REST service URL
     * @param token
     *            not used yet
     * @return a {@link JsonElement} if the request is successful and returns valid JSON, otherwise
     *         null.
     * @throws NoSessionException
     */
    private JsonElement makeJsonRequest(String path, String token) {
        RestTemplate template = new RestTemplate();
        UrlBuilder url = new UrlBuilder(apiServerUri);
        url.addPath(path);
        if (token != null) {
            url.addQueryParam(API_SESSION_KEY, token);
        }
        logger.debug("Accessing API at: " + url.toString());
        String jsonText = template.getForObject(url.toString(), String.class);
        logger.debug("JSON response for roles: " + jsonText);
        JsonParser parser = new JsonParser();
        return parser.parse(jsonText);
        
    }
    
}
