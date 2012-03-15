package org.slc.sli.api.client.impl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slc.sli.api.client.Constants;

/**
 * Generic REST client. Provides the ability to connect to a ReSTful web service and make
 * requests.
 */
public class RESTClient {
    
    public static final String SESSION_CHECK_PREFIX = "api/rest/system/session/check";
    
    
    private static Logger logger = Logger.getLogger("RESTClient");
    protected String apiServerUri = null;
    private Client client = null;
    private String sessionToken = null;
    protected String securityServerUri = null;
    
    /**
     * Construct a new RESTClient instance, using the JSON message converter.
     */
    public RESTClient(final URL apiServerURL, final URL securityServerURL) {
        client = ClientFactory.newClient();
        apiServerUri = apiServerURL.toString() + Constants.API_SERVER_PATH;
        securityServerUri = securityServerURL.toString();
    }
    
    /**
     * Connect to the IDP and redirect to the callback URL.
     * 
     * @return String sessionId
     */
    public String connect() throws MalformedURLException, URISyntaxException {
        
        return sessionCheck(null);
    }
    
    /**
     * Disconnect from the IDP.
     */
    public void disconnect() {
        // TODO...
    }
    
    /**
     * Call the session/check API. If the SAML token is invalid or null, this will redirect
     * to the realm selector page.
     * 
     * @param token
     *            SAML token or null.
     * @param redirectUrl
     *            The redirect URL after a successful authentication - set by the Security API.
     * @return String containing the authentication token.
     * @throws NoSessionException
     */
    public String sessionCheck(final String token) throws MalformedURLException, URISyntaxException {
        logger.info("Session check URL = " + SESSION_CHECK_PREFIX);
        
        URL url = new URL(securityServerUri + "/" + SESSION_CHECK_PREFIX);
        
        Response response = getRequest(url);
        
        String jsonText = response.readEntity(String.class);
        logger.info("jsonText = " + jsonText);
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(jsonText).getAsJsonObject();
        
        if (obj.has("authenticated")) {
            JsonElement e = obj.get("authenticated");
            if (e.getAsBoolean()) {
                e = obj.get("sessionId");
                sessionToken = e.getAsString();
            }
        }
        
        return sessionToken;
    }
    
    /**
     * Make a synchronous GET request to a REST service.
     * 
     * @param url
     *            full URL to the request.
     * @return ClientResponse containing the status code and return values.
     */
    public Response getRequest(final URL url) throws MalformedURLException, URISyntaxException {
        
        return getRequestWithHeaders(url, null);
    }
    
    /**
     * Make a synchronous GET request to a REST service. The request includes additional header
     * information.
     * 
     * @param URL
     *            Fully qualified URL to the ReSTful resource.
     * @param headers
     *            key / value pairs of the headers to attach to the request.
     * @return ClientResponse containing the status code and return value(s).
     */
    public Response getRequestWithHeaders(final URL url, final Map<String, Object> headers)
            throws URISyntaxException {
        
        if (sessionToken == null) {
            logger.log(Level.SEVERE, "Token is null in call to RESTClient for url" + url);
            return null;
        }
        
        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(builder, headers);
        
        Invocation i = builder.buildGet();
        return i.invoke();
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
    public Response postRequest(final URL url, final String json) throws URISyntaxException,
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
    public Response postRequestWithHeaders(final URL url, final String json,
            final Map<String, Object> headers)
                    throws URISyntaxException, MalformedURLException {
        
        if (sessionToken == null) {
            logger.log(Level.SEVERE, "Token is null in call to RESTClient for url" + url.toString());
            return null;
        }
        
        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(builder, headers);
        
        Invocation i = builder.buildPost(javax.ws.rs.client.Entity.entity(json, MediaType.APPLICATION_JSON));
        return i.invoke();
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
    public Response putRequest(final URL url, final String json) throws MalformedURLException, URISyntaxException {
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
    public Response putRequestWithHeaders(final URL url, final String json,
            final Map<String, Object> headers)
                    throws MalformedURLException, URISyntaxException {
        
        if (sessionToken == null) {
            logger.log(Level.SEVERE, "Token is null in call to RESTClient for url" + url);
            return null;
        }
        
        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(builder, headers);
        
        Invocation i = builder.buildPut(javax.ws.rs.client.Entity.entity(json, MediaType.APPLICATION_JSON));
        return i.invoke();
    }
    
    /**
     * Synchronously delete an existing entity using the REST service.
     * 
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @return ClientResponse containing the status code and return value(s).
     */
    public Response deleteRequest(final URL url) throws MalformedURLException, URISyntaxException {
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
    public Response deleteRequestWithHeaders(final URL url, final Map<String, Object> headers)
            throws MalformedURLException, URISyntaxException {
        
        if (sessionToken == null) {
            logger.log(Level.SEVERE, "Token is null in call to RESTClient for url" + url);
            return null;
        }
        
        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(builder, headers);
        
        Invocation i = builder.buildDelete();
        return i.invoke();
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
    private Invocation.Builder getCommonRequestBuilder(Invocation.Builder builder, Map<String, Object> headers) {
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        } else {
            headers = new HashMap<String, Object>();
        }
        
        if (sessionToken != null) {
            headers.put("Authorization", "Bearer" + sessionToken);
        }
        
        return builder;
    }
    
}
