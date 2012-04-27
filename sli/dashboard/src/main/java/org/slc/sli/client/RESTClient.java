package org.slc.sli.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.slc.sli.util.Constants;
import org.slc.sli.util.URLBuilder;

/**
 * 
 * @author pwolf
 */
@Component("RESTClient")
public class RESTClient {

    private String securityUrl;

    private static Logger logger = LoggerFactory.getLogger(RESTClient.class);
    
    private RestTemplate template;

    public RESTClient() {
        template = new RestTemplate();
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
        logger.info("Session check URL = {}", Constants.SESSION_CHECK_PREFIX);
        // String jsonText = makeJsonRequest(Constants.SESSION_CHECK_PREFIX, token);
        String jsonText = makeJsonRequestWHeaders(Constants.SESSION_CHECK_PREFIX, token, true);
        logger.info("jsonText = {}", jsonText);
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
     *
     * @param fullEntities 
     *             flag for returning expanded entities from the API
     *            
     * @return a {@link JsonElement} if the request is successful and returns valid JSON, otherwise
     *         null.
     * @throws NoSessionException
     */
     public String makeJsonRequestWHeaders(String path, String token, boolean fullEntities) {

        if (token != null) {
            // url.addQueryParam(API_SESSION_KEY, token);
            URLBuilder url = null;
            if (!path.startsWith("http")) {
                url = new URLBuilder(getSecurityUrl());
                url.addPath(path);
            } else {
                url = new URLBuilder(path);
            }
            //TODO probably should use media types
            if (fullEntities)
                url.addQueryParam("full-entities", "true");

            HttpHeaders headers = new HttpHeaders();
            // headers.add(API_SESSION_KEY, token);
            headers.add("Authorization", "Bearer " + token);
            HttpEntity entity = new HttpEntity(headers);
            logger.debug("Accessing API at: {}", url);
            HttpEntity<String> response = null;
            try {
                response = exchange(url.toString(), HttpMethod.GET, entity, String.class);
            } catch (HttpClientErrorException e) {
                logger.debug("Catch HttpClientException: {}",  e.getStatusCode());
            }
            if (response == null) {
                return null;
            }
            return response.getBody();
        }
        logger.debug("Token is null in call to RESTClient for path {}", path);

        return null;
    }

    public String getJsonRequest(String path) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> response = null;
        try {
        response = exchange(path, HttpMethod.GET, new HttpEntity(headers), String.class);
        } catch (HttpClientErrorException e) {
            logger.debug("Catch HttpClientException: {}",  e.getStatusCode());
        }
        
        if (response == null) {
            return null;
        }
        
        JsonParser parser = new JsonParser();
        String jsonText = parser.parse(response.getBody()).getAsString();
        return jsonText;
        
    }
     
    public String getSecurityUrl() {
        return securityUrl;
    }

    public void setSecurityUrl(String securityUrl) {
        this.securityUrl = securityUrl;
    }

    public RestTemplate getTemplate() {
        return template;
    }

    public void setTemplate(RestTemplate template) {
        this.template = template;
    }
    
    public HttpEntity<String> exchange(String url, HttpMethod method, HttpEntity entity, Class cl) {
        return this.template.exchange(url, method, entity, cl);
    }
}
