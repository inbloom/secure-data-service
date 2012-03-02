package org.slc.sli.api.client.impl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Constants;
import org.slc.sli.api.client.URLBuilder;

/** TODO -- more documentation around return types */

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
            URLBuilder builder = URLBuilder.create(apiServerUri);
            builder.addPath(Constants.SESSION_CHECK_PATH);
            
            ClientResponse response = getRequest(builder.build());
            
            JsonParser parser = new JsonParser();
            return parser.parse(response.getEntity(String.class)).getAsJsonObject();
            
        } catch (MalformedURLException e2) {
            logger.error("Failed to check the user session with the API server: " + e2.getLocalizedMessage());
            
        } catch (URISyntaxException e3) {
            logger.error("Failed to check the user session with the API server: " + e3.getLocalizedMessage());
        }
        
        return null;
    }
    
    /**
     * Make a synchronous GET request to a REST service.
     * 
     * @param url
     *            full URL to the request.
     * @return ClientResponse containing the status code and return values.
     */
    public ClientResponse getRequest(final URL url) throws MalformedURLException, URISyntaxException {
        
        return getRequestWithHeaders(url, null);
    }
    
    /**
     * Make a synchronous GET request to a REST service. The request includes additional header
     * information.
     * 
     * @param URL
     *            Fully qualified URL to the ReSTful resource.
     * @param headers
     *            key / value pairs of the headers to attach to the request. A key can map
     *            to multiple values.
     * @return ClientResponse containing the status code and return value(s).
     */
    public ClientResponse getRequestWithHeaders(final URL url, final Map<String, Object> headers)
            throws URISyntaxException {
        
        if (sessionToken == null) {
            logger.error("Token is null in call to RESTClient for url" + url);
            return null;
        }
        
        ClientRequest.Builder builder = getCommonRequestBuilder(headers);
        ClientRequest request = builder.build(url.toURI(), HttpMethod.GET);
        return client.handle(request);
    }
    
    
    /**
     * Synchronously post a new entity to the REST service. This corresponds to a create operation.
     * 
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param json
     *            Json entity to post.
     * @return ClientResponse containing the status code and return value(s).
     */
    public ClientResponse postRequest(final URL url, final String json) throws URISyntaxException,
    MalformedURLException {
        
        return postRequestWithHeaders(url, json, null);
    }
    
    /**
     * Synchronously post a new entity to the REST service. This request includes additional header
     * information.
     * 
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param json
     *            JSON to post.
     * @param headers
     *            key / value pairs of the headers to attach to the request. A key can map
     *            to multiple values.
     * @return ClientResponse containing the status code and return value(s).
     */
    public ClientResponse postRequestWithHeaders(final URL url, final String json,
            final Map<String, Object> headers)
                    throws URISyntaxException, MalformedURLException {
        
        if (sessionToken == null) {
            logger.error("Token is null in call to RESTClient for url" + url.toString());
            return null;
        }
        
        ClientRequest.Builder builder = getCommonRequestBuilder(headers);
        builder.entity(json, MediaType.APPLICATION_JSON_TYPE);
        
        ClientRequest request = builder.build(url.toURI(), HttpMethod.POST);
        return client.handle(request);
    }
    
    /**
     * Synchronous Put request to the REST service. This corresponds to an update operation.
     * 
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param json
     *            JSON of the entity to PUT.
     * @return ClientResponse containing the status code and return value(s).
     */
    public ClientResponse putRequest(final URL url, final String json) throws MalformedURLException, URISyntaxException {
        return putRequestWithHeaders(url, json, null);
    }
    
    /**
     * Synchronous Put request to the REST service. This corresponds to an update operation.
     * This request includes additional header information.
     * 
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param json
     *            JSON of the entity to PUT.
     * @param headers
     *            key / value pairs of the headers to attach to the request. A key can map
     *            to multiple values.
     * @return ClientResponse containing the status code and return value(s).
     */
    public ClientResponse putRequestWithHeaders(final URL url, final String json,
            final Map<String, Object> headers)
                    throws MalformedURLException, URISyntaxException {
        
        if (sessionToken == null) {
            logger.error("Token is null in call to RESTClient for url" + url);
            return null;
        }
        
        ClientRequest.Builder builder = getCommonRequestBuilder(headers);
        builder.entity(json, MediaType.APPLICATION_JSON_TYPE);
        
        ClientRequest request = builder.build(url.toURI(), HttpMethod.PUT);
        return client.handle(request);
    }
    
    /**
     * Synchronously delete an existing entity using the REST service.
     * 
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @return ClientResponse containing the status code and return value(s).
     */
    public ClientResponse deleteRequest(final URL url) throws MalformedURLException, URISyntaxException {
        return deleteRequestWithHeaders(url, null);
    }
    
    /**
     * Synchronously delete an existing entity using the REST service. This request includes
     * additional header
     * information.
     * 
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param headers
     *            key / value pairs of the headers to attach to the request. A key can map
     *            to multiple values.
     * @return ClientResponse containing the status code and return value(s).
     */
    public ClientResponse deleteRequestWithHeaders(final URL url, final Map<String, Object> headers)
            throws MalformedURLException, URISyntaxException {
        
        if (sessionToken == null) {
            logger.error("Token is null in call to RESTClient for url" + url);
            return null;
        }
        
        ClientRequest.Builder builder = getCommonRequestBuilder(headers);
        ClientRequest request = builder.build(url.toURI(), HttpMethod.DELETE);
        return client.handle(request);
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
        
        apiServerUri = "http://" + host + ":" + port + "/" + Constants.API_SERVER_PATH;
        
        // TODO -- Log into the IDP and get a Session Token. Waiting on ReST call from LuckyStrike.
        // For now generate a token via a Rest Console in a web browser and pass the resulting token
        // in as 'password'.
        sessionToken = password;
        
    }
    
    /**
     * Get the base URL for all SLI API ReSTful service calls.
     * 
     * @return
     */
    public String getBaseURL() {
        return apiServerUri;
    }
    
    /**
     * Get a ClientRequest.Builder with common properties already set.
     * 
     * @param headers
     * @return
     */
    private ClientRequest.Builder getCommonRequestBuilder(final Map<String, Object> headers) {
        ClientRequest.Builder builder = new ClientRequest.Builder();
        builder.accept(MediaType.APPLICATION_JSON);
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        builder.header(API_SESSION_KEY, sessionToken);
        return builder;
    }
    
}
