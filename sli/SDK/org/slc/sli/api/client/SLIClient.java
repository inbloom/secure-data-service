package org.slc.sli.api.client;

/**
 * Interface defining the methods available to SLI API client applications.  The SLIClient
 * follows a builder pattern for creating a connection to the SLI API server.  It provides
 * basic CRUD operations once the client connection is established.
 *
 * @author asaarela
 */
public interface SLIClient {
    
    /**
     * SLIClientBuilder Builder for an SLIClient instance.
     */
    public static class SLIClientBuilder() {
        /**
         * Initialize the builder connection host.
         * @param host for the API server.
         * @return an SLIClientBuilder
         */
        public SLIClientBuilder host(String host);
        
        /**
         * Initialize the builder connection port.
         * @param port for the API server.
         * @return an SLIClientBuilder
         */
        public SLIClientBuilder port(int port);
        
        /**
         * Initialize the builder user name.
         * @param username
         * @return SLIClientBuilder
         */
        public SLIClientBuilder user(String username);
        
        /**
         * Initialize the builder user password.
         * @param password user password.
         * @return SLIClientBuilder
         */        
        public SLIClientBuilder password(password);
        
        /**
         * Initialize the user's IDP realm.
         * @param ream Users IDP realm.
         * @return SLIClientBuilder
         */
        public SLIClientBuilder realm(realm);
        
        /**
         * Create an SLIClient instance.
         * @return SLIClient
         */
        public SLIClient build();
    }
    
    /**
     * CRUD operations
     */
    
    /**
     * Create operation
     * @param e Entity to create
     * @return Response to the update request.
     */
    public Response post(Entity e);
    
    /**
     * Retrieve operation
     * @param type The type of entity
     * @param query Query parameters.
     * @return EntityCollection collection of entities of EntityType that match the query.
     */        
    public EntityCollection get(EntityType type, Query query);
    
    /**
     * Update operation
     * @param e Entity to update.
     * @return Response to the update request.
     */
    public Response put(Entity e);
    
    /**
     * Delete operation
     * @param e Entity to delete
     * @return Response to the delete request.
     */
    public Response delete(Entity e);
    
    
    /**
     * Don't allow direct instantiation of SLIClient instances; use the builder instead.
     */
    private SLIClient();
}
