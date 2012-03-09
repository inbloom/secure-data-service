package org.slc.sli.api.client;

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
     * Connect to the SLI ReSTful API web service and authenticate with the IDP.
     * 
     * @param host
     *            Host running the SLI API.
     * @param port
     *            Port to connect to.
     * @param username
     *            Name of an authorized SLI user.
     * @param password
     *            Password for this user.
     * @param realm
     *            IDP realm the user is associated with.
     * @return
     *         String containing the sessionId for the authenticated user, or null if
     *         authentication fails.
     */
    public abstract String connect(final String host, final int port, final String user, final String password,
            final String realm);
    
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
