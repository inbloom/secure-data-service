package org.slc.sli.client;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.slc.sli.util.URLBuilder;
import org.slc.sli.util.Constants;
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
    private String apiServerUri = Constants.API_SERVER_URI;
    
    /**
     * Get the Roles and Rights information from the API
     * 
     * @param token
     *            the sessionId
     * @return JsonArray object as described by API documentation
     * @throws NoSessionException
     */
    public JsonArray getRoles(String token) {
        String jsonText = makeJsonRequest(Constants.GET_ROLES_URL, token);
        JsonParser parser = new JsonParser();
        return parser.parse(jsonText).getAsJsonObject().getAsJsonArray(); 
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
        String jsonText = makeJsonRequest(Constants.SESSION_CHECK_URL, token);
        JsonParser parser = new JsonParser();
        return parser.parse(jsonText).getAsJsonObject();
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
    public String makeJsonRequest(String path, String token) {
        System.out.println("IN MAKEJSONREQUEST");
        RestTemplate template = new RestTemplate();
        URLBuilder url = new URLBuilder(apiServerUri);
        url.addPath(path);
        if (token != null) {
            url.addQueryParam(API_SESSION_KEY, token);
           
        }
        System.out.println("Accessing API at: " + url.toString());
        String jsonText = template.getForObject(url.toString(), String.class);
        System.out.println("JSON response for roles: " + jsonText);
        return jsonText;
    }
    
    
    public String makeJsonRequestWHeaders(String url, String token) {
        RestTemplate template = new RestTemplate();

        if (token != null) {
            //url.addQueryParam(API_SESSION_KEY, token);
            HttpHeaders headers = new HttpHeaders();
            headers.add(API_SESSION_KEY, token);
            HttpEntity entity = new HttpEntity(headers);
            logger.debug("Accessing API at: " + url);            
            HttpEntity<String> response = template.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        }
        logger.debug("Token is null in call to RESTClient for url" + url);

        return null;
    }    
    
    
    
    
}
