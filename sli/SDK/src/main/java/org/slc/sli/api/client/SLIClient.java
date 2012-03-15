package org.slc.sli.api.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.core.Response;

/**
 * Interface defining the methods available to SLI API client applications. It provides
 * basic CRUD operations available once the client connection is established.
 * 
 * @author asaarela
 */
public interface SLIClient {
    
    /**
     * Initialize the SLIClient
     * 
     * @param apiURL
     *            URL to the SLI API server. Example: https://localhost:493
     * @param securityURL
     *            URL to the SLI Security server.
     */
    public abstract void init(final URL apiURL, final URL securityURL);
    
    /**
     * Log in to the SLI ReSTful API web service and authenticate with the IDP. This redirects
     * to the client's defined callbackURL when successful.
     * 
     * @return
     *         String session token for the authenticated user, or null if
     *         authentication fails.
     */
    public abstract String login() throws IOException;
    
    /**
     * Logout and invalidate the session.
     */
    public abstract void logout();
    
    /**
     * Validate the SAML token.
     * 
     * @return The SAML token if still valid, or an updated token if the user re-authenticated.
     */
    public abstract String checkSession(String token);
    
    /**
     * Create operation
     * 
     * @param e
     *            Entity to create
     * @return Response to the update request.
     */
    public abstract Response create(final Entity e) throws MalformedURLException, URISyntaxException;
    
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
     */
    public abstract Response read(EntityCollection entities, final EntityType type, final String id,
            final Query query)
                    throws MalformedURLException, URISyntaxException;
    
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
     */
    public abstract Response read(EntityCollection entities, final EntityType type, final Query query)
            throws MalformedURLException,
            URISyntaxException;
    
    /**
     * Update operation
     * 
     * @param e
     *            Entity to update.
     * @return Response to the update request.
     */
    public abstract Response update(final Entity e) throws MalformedURLException, URISyntaxException;
    
    /**
     * Delete operation
     * 
     * @param e
     *            Entity to delete
     * @return Response to the delete request.
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
     */
    public abstract Response getResource(EntityCollection entities, URL resourceURL, Query query)
            throws MalformedURLException, URISyntaxException;
    
}
