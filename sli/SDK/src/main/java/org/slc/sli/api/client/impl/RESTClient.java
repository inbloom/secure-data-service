package org.slc.sli.api.client.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;

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
    private static Logger logger = Logger.getLogger("RESTClient");
    private String sessionToken = null;
    private String apiServerUri;
    private ApacheHttpClient4 client = null;
    
    /**
     * Construct a new RESTClient instance, using the JSON message converter.
     */
    protected RESTClient() {
        ApacheHttpClient4Config config = new DefaultApacheHttpClient4Config();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        config.getProperties().put(ApacheHttpClient4Config.PROPERTY_PREEMPTIVE_BASIC_AUTHENTICATION, Boolean.TRUE);
        client = ApacheHttpClient4.create(config);
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
            logger.log(Level.SEVERE,
                    "Failed to check the user session with the API server: " + e2.getLocalizedMessage());
            
        } catch (URISyntaxException e3) {
            logger.log(Level.SEVERE,
                    "Failed to check the user session with the API server: " + e3.getLocalizedMessage());
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
            logger.log(Level.SEVERE, "Token is null in call to RESTClient for url" + url);
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
            logger.log(Level.SEVERE, "Token is null in call to RESTClient for url" + url.toString());
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
            logger.log(Level.SEVERE, "Token is null in call to RESTClient for url" + url);
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
            logger.log(Level.SEVERE, "Token is null in call to RESTClient for url" + url);
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
     * @return
     *         String containing the sessionToken for the authenticated user.
     */
    public String openSession(final String host, final int port, final String user, final String password,
            final String realm) {
        
        /**
         * TODO - replace this with oauth when it's ready
         */
        
        String protocol = "";
        if (host.equalsIgnoreCase("localhost")) {
            protocol = "http://";
        } else {
            protocol = "https://";
        }
        apiServerUri = protocol + host + ":" + port + "/" + Constants.API_SERVER_PATH;
        
        /**
         * TODO -- determine if we can pass credentials as part of clientState and bypass
         * hitting the IDP directly.
         */
        
        // Find the IDP for the realm
        ClientRequest.Builder builder = new ClientRequest.Builder();
        String idp = "";
        builder.accept(MediaType.TEXT_HTML);
        try {
            ClientConfig cc = new DefaultClientConfig();
            cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
            Client c = Client.create(cc);
            
            ClientResponse response = c.handle(builder.build(new URI(protocol + host + ":" + port
                    + "/disco/realms/list?RealmName=" + URLEncoder.encode(realm)), HttpMethod.GET));
            
            String tmp = response.getHeaders().getFirst("Location");
            
            if (tmp != null) {
                // find the idpEntityId
                int start = tmp.indexOf("idpEntityID=");
                int end = tmp.indexOf('&', start);
                idp = URLDecoder.decode(tmp.substring(start + 12, end));
            }
            
            c.destroy();
            
        } catch (ClientHandlerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Attempt to authenticate
        builder = new ClientRequest.Builder();
        builder.accept(MediaType.TEXT_PLAIN);
        builder.entity("username=" + user + "&password=" + password,
                MediaType.APPLICATION_FORM_URLENCODED);
        
        // String url = idp + "/identity/authenticate?username=" + user + "&password=" + password;
        
        ClientRequest request;
        String rstring = "";
        try {
            request = builder.build(new URI(idp + "/identity/authenticate"), HttpMethod.POST);
            ClientResponse response = client.handle(request);
            rstring = response.getEntity(String.class);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if (rstring.startsWith("token.id=")) {
            sessionToken = rstring.substring(rstring.indexOf('=') + 1).trim();
        } else {
            sessionToken = null;
        }
        
        return sessionToken;
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
