package org.slc.sli.api.client.impl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Constants;
import org.slc.sli.api.client.URLBuilder;

/**
 * Generic REST client. Provides the ability to connect to a ReSTful web service and make
 * requests.
 */
public class RESTClient {
    
    /** Request parameter key used to pass sessionId to API **/
    private static final String API_SESSION_KEY = "sessionId";
    
    private static Logger logger = LoggerFactory.getLogger(RESTClient.class);
    
    private String sessionToken = null;
    
    private String apiServerUri;
    
    private Client client = null;
    
    /**
     * Construct a new RESTClient instance, using the JSON message converter.
     */
    protected RESTClient() {
        
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        
        // TODO - implement SSL configuration here...
        
        client = Client.create(config);
    }
    
    /**
     * Call the session/check API
     *
     * @return JsonOject as described by API documentation
     * @throws NoSessionException
     */
    public JsonObject sessionCheck() {
        try {
            String response = getRequest(Constants.SESSION_CHECK_PATH);
            JsonParser parser = new JsonParser();
            return parser.parse(response).getAsJsonObject();
            
        } catch (MalformedURLException e2) {
            logger.error("Failed to check the user session with the API server: " + e2.getLocalizedMessage());
            
        } catch (URISyntaxException e3) {
            logger.error("Failed to check the user session with the API server: " + e3.getLocalizedMessage());
        }
        
        return null;
    }
    
    /**
     * Make a request to a REST service and convert the result to JSON
     * 
     * @param path
     *            the unique portion of the requested REST service URL
     * @return valid JSON if the request is successful, otherwise null.
     * @throws MalformedURLException
     *             URISyntaxException
     */
    public String getRequest(final String path) throws MalformedURLException,
    URISyntaxException {
        
        URLBuilder url = URLBuilder.create(apiServerUri);
        url.addPath(path);
        return getRequest(url.build());
    }
    
    /**
     * Make a request to a REST service and convert the result to JSON
     * 
     * @param url
     *            full URL to the request.
     * @return valid JSON if the request is successful, otherwise null.
     * @throws MalformedURLException
     *             URISyntaxException
     */
    public String getRequest(final URL url) throws MalformedURLException, URISyntaxException {
        
        return getRequestWithHeaders(url, null);
    }
    
    /**
     * Make a request to a REST service and convert the result to JSON. The request
     * includes additional header information.
     * 
     * @param path
     *            the unique portion of the requested REST service URL
     * @param headers
     *            key / value pairs of the headers to attach to the request. A key can map
     *            to multiple values.
     * @return valid JSON if the request is successful, otherwise null.
     */
    public String getRequestWithHeaders(final URL url, final Map<String, List<String>> headers)
            throws URISyntaxException {
        
        if (sessionToken != null) {
            ClientRequest.Builder builder = new ClientRequest.Builder();
            builder.header(API_SESSION_KEY, sessionToken);
            
            if (headers != null) {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    builder.header(entry.getKey(), entry.getValue());
                }
            }
            
            builder.build(url.toURI(), "GET");
            ClientRequest request = builder.build(url.toURI(), HttpMethod.GET);
            
            WebResource resource = client.resource(url.toURI());
            
            ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, request);
            
            return response.toString();
        }
        
        logger.error("Token is null in call to RESTClient for url" + url);
        return null;
    }
    
    /**
     * @param host
     *            Host running the SLI API ReSTful service.
     * @param port
     *            Port for the service.
     * @param user
     *            SLI authorized username.
     * @param password
     *            user password.
     * @param realm
     *            IDP authentication realm.
     */
    public void openSession(final String host, final int port, final String user, final String password,
            final String realm) {
        
        apiServerUri = new String("https://" + host + ":" + port + "/" + Constants.API_SERVER_PATH);
        
        // TODO -- Log into the IDP and get a Session Token. Waiting on ReST call from LuckyStrike.
        sessionToken = "TODO";
        
    }
    
    /**
     * Get the base URL for all SLI API ReSTful service calls.
     * 
     * @return
     */
    public String getBaseURL() {
        return apiServerUri;
    }
}
