package org.slc.sli.api.client.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    private Client client = null;
    
    /**
     * Construct a new RESTClient instance, using the JSON message converter.
     */
    protected RESTClient() {
        client = ClientFactory.newClient();
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
            
            Response response = getRequest(builder.build());
            
            JsonParser parser = new JsonParser();
            return parser.parse(response.readEntity(String.class)).getAsJsonObject();
            
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
     *            key / value pairs of the headers to attach to the request. A key can map
     *            to multiple values.
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
            final String realm) throws IOException {
        
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
        // Ideally we'd use the JAX-RS client here; but the current implementation doesn't
        // support disabling automatically redirect.
        URL url = new URL(protocol + host + ":" + port + "/disco/realms/list?RealmName=" + URLEncoder.encode(realm));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("GET");
        String tmp = connection.getHeaderField("Location");
        
        String idp = "";
        try {
            if (tmp != null) {
                // find the idpEntityId
                int start = tmp.indexOf("idpEntityID=");
                int end = tmp.indexOf('&', start);
                idp = URLDecoder.decode(tmp.substring(start + 12, end));
            }
        } finally {
            connection.disconnect();
        }
        
        // Attempt to authenticate
        Client c2 = ClientFactory.newClient();
        String rstring = "";
        try {
            Invocation.Builder builder = c2.target(idp + "/identity/authenticate").request(
                    MediaType.APPLICATION_FORM_URLENCODED);
            
            Form f = new Form().param("username", user).param("password", password);
            Invocation i = builder.buildPost(Entity.form(f));
            Response response = i.invoke();
            rstring = response.readEntity(String.class);
            
        } finally {
            c2.close();
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
    private Invocation.Builder getCommonRequestBuilder(Invocation.Builder builder, final Map<String, Object> headers) {
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        builder.header(API_SESSION_KEY, sessionToken);
        return builder;
    }
    
}
