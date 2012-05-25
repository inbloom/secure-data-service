package org.slc.sli.api.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.core.MessageProcessingException;
import javax.ws.rs.core.Response;

import org.scribe.exceptions.OAuthException;

import org.slc.sli.client.util.Query;

/**
 * Interface defining the methods available to SLI API client applications. It provides
 * basic CRUD operations available once the client connection is established. To establish
 * a connect, call the 'connect' method.
 * 
 * Each client application has a client id and a secret that uniquely identifies the
 * application. These values are provided during the application registration process.
 * These values are passed to SLI API and validated against the registered application.
 * The SLI security layer will verify the clientid and secret match.
 * 
 * Each client application must provide a callbackURL that the SLI API uses to return the
 * result of the users authentication attempt. This must be exactly the same as the callback
 * defined in the SLI database for the application. The callbackURL receives an oauth2
 * authorization token on successful login.
 * 
 * @author asaarela
 */
public interface SLIClient {
    
    /**
     * Retrieve the resource URL to the identity provider (IDP) used by application users
     * to authenticate. The client application is responsible for redirecting the user
     * to this URL. The response from this URL will contain the authorization token
     * required to connect to the API.
     * 
     * @return A URL that directs the user to authenticate with the appropriate IDP. On
     *         successful login, the IDP sends an authorization token to the callbackURL.
     */
    public abstract URL getLoginURL();
    
    /**
     * Connect to the SLI ReSTful API web service passing the authentication token provided by
     * the IDP. The IDP will redirect successful login attempts to the callbackURL and include
     * an authorization token in the response. You must then pass the authorization token to
     * this call.
     * 
     * If the code is invalid, an exception is thrown.
     * 
     * @param authorizationToken
     *            Oauth2 authorization token returned by the login URL in the callbackURL.
     * @return String authorization token for the authenticated user, or null if
     *         authentication fails.
     * @throws OAuthException
     */
    public abstract String connect(final String authorizationToken) throws OAuthException;
    
    /**
     * Logout and invalidate the session.
     */
    public abstract void logout();
    
    /**
     * Create operation
     * 
     * @param e
     *            Entity to create
     * @return Response to the update request.
     * 
     * @throws URISyntaxException
     * @throws IOException
     */
    public abstract Response create(final Entity e) throws IOException, URISyntaxException;
    
    /**
     * Read operation by ID.
     * 
     * @param entities
     *            Entities returned by the API.
     * @param type
     *            The type of entity
     * @param id
     *            The ID of the entity to read.
     * @param query
     *            Query parameters.
     * @return ClientResponse from the ReST call.
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws MessageProcessingException
     */
    public abstract Response read(List<Entity> entities, final String type, final String id, final Query query)
            throws URISyntaxException, MessageProcessingException, IOException;
    
    /**
     * Read operation
     * 
     * @param entities
     *            Entities returned by the API.
     * @param type
     *            The type of entity
     * @param query
     *            Query parameters.
     * @return ClientResponse from the ReST call.
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws MessageProcessingException
     */
    public abstract Response read(List<Entity> entities, final String type, final Query query)
            throws URISyntaxException, MessageProcessingException, IOException;
    
    /**
     * Update operation
     * 
     * @param e
     *            Entity to update.
     * @return Response to the update request.
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws MessageProcessingException
     */
    public abstract Response update(final Entity e) throws URISyntaxException, MessageProcessingException, IOException;
    
    /**
     * Delete operation
     * 
     * @param e
     *            Entity to delete
     * @return Response to the delete request.
     * 
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response delete(final Entity e) throws MalformedURLException, URISyntaxException;
    
    /**
     * Perform a get operation against a generic resource. This is useful when following links
     * returned by other resources, for example.
     * 
     * @param entities
     *            Entities returned by the API in response to this request.
     * @param resourceURL
     *            URL to get
     * @param query
     *            Query to append to the resource.
     * @return ClientResponse from the ReST call.
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws MessageProcessingException
     */
    public abstract Response getResource(List<Entity> entities, URL resourceURL, Query query)
            throws URISyntaxException, MessageProcessingException, IOException;
    
    /**
     * Get the home resource for the authenticated user.
     * 
     * @return ClientResponse from the ReST call.
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws MessageProcessingException
     */
    public abstract Response getHomeResource(Entity home) throws URISyntaxException, MessageProcessingException,
            IOException;
    
}
