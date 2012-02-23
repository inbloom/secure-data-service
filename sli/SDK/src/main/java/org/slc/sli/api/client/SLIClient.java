package org.slc.sli.api.client;

import javax.management.Query;

/**
 * Interface defining the methods available to SLI API client applications.  The SLIClient
 * follows a builder pattern for creating a connection to the SLI API server.  It provides
 * basic CRUD operations once the client connection is established.
 *
 * @author asaarela
 */
public final class SLIClient {
    
    /**
     * TODO - move this to a public class and implement.
     */
    class Entity {
    }
    
    /**
     * TODO - move this to a public class and implement.
     */
    class Response {
    }
    
    /**
     * TODO - move this to a public class and implement.
     */
    class EntityCollection {
    }
    
    /**
     * TODO - move this to a public class and implement.
     */
    enum EntityType {
    }
    
    /**
     * SLIClientBuilder Builder for an SLIClient instance.
     */
    public static class SLIClientBuilder {
        /**
         * Initialize the builder connection host.
         * @param host for the API server.
         * @return an SLIClientBuilder
         */
        public SLIClientBuilder host(String host) {
            return this;
        }
        
        /**
         * Initialize the builder connection port.
         * @param port for the API server.
         * @return an SLIClientBuilder
         */
        public SLIClientBuilder port(int port) {
            return this;
        }
        
        /**
         * Initialize the builder user name.
         * @param username
         * @return SLIClientBuilder
         */
        public SLIClientBuilder user(String username) {
            return this;
        }
        
        /**
         * Initialize the builder user password.
         * @param password user password.
         * @return SLIClientBuilder
         */
        public SLIClientBuilder password(String password) {
            return this;
        }
        
        /**
         * Initialize the user's IDP realm.
         * @param ream Users IDP realm.
         * @return SLIClientBuilder
         */
        public SLIClientBuilder realm(String realm) {
            return this;
        }
        
        /**
         * Create an SLIClient instance.
         * @return SLIClient
         */
        public SLIClient build() {
            return new SLIClient();
        }
    };
    
    /**
     * CRUD operations
     */
    
    /**
     * Create operation
     * @param e Entity to create
     * @return Response to the update request.
     */
    public Response post(Entity e) {
        return new Response();
    }
    
    /**
     * Retrieve operation
     * @param type The type of entity
     * @param query Query parameters.
     * @return EntityCollection collection of entities of EntityType that match the query.
     */
    public EntityCollection get(EntityType type, Query query) {
        return new EntityCollection();
    }
    
    /**
     * Update operation
     * @param e Entity to update.
     * @return Response to the update request.
     */
    public Response put(Entity e) {
        return new Response();
    }
    
    /**
     * Delete operation
     * @param e Entity to delete
     * @return Response to the delete request.
     */
    public Response delete(Entity e) {
        return new Response();
    }
    
    
    /**
     * Don't allow direct instantiation of SLIClient instances; use the builder instead.
     */
    private SLIClient() {
    }
}
