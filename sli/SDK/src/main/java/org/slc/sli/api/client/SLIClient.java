package org.slc.sli.api.client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import com.sun.jersey.api.client.ClientResponse;

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
     */
    public abstract void connect(final String host, final int port, final String user, final String password,
            final String realm);

    /**
     * Create operation
     * 
     * @param e
     *            Entity to create
     * @return Response to the update request.
     */
    public abstract ClientResponse create(final Entity e);
    
    /**
     * Read operation by ID.
     * 
     * @param type
     *            The type of entity
     * @param id
     *            The ID of the entity to read.
     * @param query
     *            Query parameters.
     * @return EntityCollection collection of entities of EntityType that match the query.
     */
    public abstract EntityCollection read(final EntityType type, final String id, final Query query)
            throws MalformedURLException, URISyntaxException;
    
    /**
     * Read operation
     * 
     * @param type
     *            The type of entity
     * @param query
     *            Query parameters.
     * @return EntityCollection collection of entities of EntityType that match the query.
     */
    public abstract EntityCollection read(final EntityType type, final Query query) throws MalformedURLException,
    URISyntaxException;
    
    /**
     * Update operation
     * 
     * @param e
     *            Entity to update.
     * @return Response to the update request.
     */
    public abstract ClientResponse update(final Entity e);
    
    /**
     * Delete operation
     * 
     * @param e
     *            Entity to delete
     * @return Response to the delete request.
     */
    public abstract ClientResponse delete(final Entity e);
    
}