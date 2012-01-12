package org.slc.sli.security;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.slc.sli.util.URLBuilder;

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
        String jsonText = makeJsonRequest("admin/roles", token);
        JsonParser parser = new JsonParser();
        return parser.parse(jsonText).getAsJsonObject().getAsJsonArray(); 
    }
    
    public String getApiServerUri() {
        return apiServerUri;
    }

    public void setApiServerUri(String apiServerUri) {
        this.apiServerUri = apiServerUri;
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
        String jsonText = makeJsonRequest("system/session/check", token);
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
        RestTemplate template = new RestTemplate();
        URLBuilder url = new URLBuilder(apiServerUri);
        url.addPath(path);
        if (token != null) {
            url.addQueryParam(API_SESSION_KEY, token);
           
        }
        logger.debug("Accessing API at: " + url.toString());
        String jsonText = template.getForObject(url.toString(), String.class);
        logger.debug("JSON response for roles: " + jsonText);
        return jsonText;
    }
    
    public String getStudent(String id, String token) {
        String url = apiServerUri + "/students/" + id;
        return makeJsonRequestWHeaders(url, token);
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
