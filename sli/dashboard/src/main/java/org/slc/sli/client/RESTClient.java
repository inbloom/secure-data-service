package org.slc.sli.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slc.sli.util.Constants;
import org.slc.sli.util.URLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
        String jsonText = makeJsonRequestWHeaders(Constants.SESSION_CHECK_PREFIX, token);
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
     public String makeJsonRequestWHeaders(String path, String token) {

        if (token != null) {

            URLBuilder url = null;
            if (!path.startsWith("http")) {
                url = new URLBuilder(getSecurityUrl());
                url.addPath(path);
            } else {
                url = new URLBuilder(path);
            }

            HttpHeaders headers = new HttpHeaders();
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

    /**
     * Make a PUT request to a REST service
     * 
     * @param path
     *            the unique portion of the requested REST service URL
     * @param token
     *            not used yet
     * 
     * @param entity
     *            entity used for update
     * 
     * @throws NoSessionException
     */
    public void putJsonRequestWHeaders(String path, String token, Object entity) {
        
        if (token != null) {
            URLBuilder url = null;
            if (!path.startsWith("http")) {
                url = new URLBuilder(getSecurityUrl());
                url.addPath(path);
            } else {
                url = new URLBuilder(path);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            headers.add("Content-Type", "application/json");
            HttpEntity requestEntity = new HttpEntity(entity, headers);
            logger.debug("Updating API at: {}", url);
            try {
                template.put(url.toString(), requestEntity);
            } catch (HttpClientErrorException e) {
                logger.debug("Catch HttpClientException: {}", e.getStatusCode());
            }
        }
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
