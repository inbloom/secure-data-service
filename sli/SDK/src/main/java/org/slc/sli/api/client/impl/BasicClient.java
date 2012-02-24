package org.slc.sli.api.client.impl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.EntityType;
import org.slc.sli.api.client.Query;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.URLBuilder;

/**
 * Class defining the methods available to SLI API client applications. The BasicClient
 * follows a builder pattern for creating a connection to the SLI API server. It provides
 * basic CRUD operations once the client connection is established.
 * 
 * @author asaarela
 */
public final class BasicClient implements SLIClient {
    
    /**
     * SLIClientBuilder Builder for an BasicClient instance.
     */
    public static class BasicClientBuilder {
        private String host = "localhost";
        private int port = 8080;
        private String username;
        private String password;
        private String realm;
        
        /**
         * Initialize the builder connection host.
         * @param host for the API server.
         * @return an SLIClientBuilder
         */
        public BasicClientBuilder host(final String host) {
            this.host = host;
            return this;
        }
        
        /**
         * Initialize the builder connection port.
         * @param port for the API server.
         * @return an SLIClientBuilder
         */
        public BasicClientBuilder port(final int port) {
            this.port = port;
            return this;
        }
        
        /**
         * Initialize the builder user name.
         * @param username
         * @return SLIClientBuilder
         */
        public BasicClientBuilder user(final String username) {
            this.username = username;
            return this;
        }
        
        /**
         * Initialize the builder user password.
         * @param password user password.
         * @return SLIClientBuilder
         */
        public BasicClientBuilder password(final String password) {
            this.password = password;
            return this;
        }
        
        /**
         * Initialize the user's IDP realm.
         * @param ream Users IDP realm.
         * @return SLIClientBuilder
         */
        public BasicClientBuilder realm(final String realm) {
            this.realm = realm;
            return this;
        }
        
        /**
         * Create an BasicClient instance.
         * @return BasicClient
         */
        public SLIClient build() {
            return new BasicClient(host, port, username, password, realm);
        }
    }
    
    private RESTClient restClient;
    
    /**
     * CRUD operations
     */
    
    @Override
    public ClientResponse create(final Entity e) {
        // TODO - return a response
        return new ClientResponse(0, null, null, null);
    }
    
    @Override
    public EntityCollection read(final EntityType type, final Query query) throws MalformedURLException,
    URISyntaxException {
        
        EntityCollection rval = new EntityCollection();
        
        // build the URL
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(type).query(query).build();
        
        String response = restClient.getRequest(url);
        
        Gson gson = new Gson();
        rval = gson.fromJson(response, EntityCollection.class);
        return rval;
    }
    
    @Override
    public ClientResponse update(final Entity e) {
        // TODO - return a response
        return new ClientResponse(0, null, null, null);
    }
    
    @Override
    public ClientResponse delete(final Entity e) {
        // TODO - return a response
        return new ClientResponse(0, null, null, null);
    }
    
    @Override
    public void connect(final String host, final int port, final String user, final String password, final String realm) {
        restClient = new RESTClient();
        restClient.openSession(host, port, user, password, realm);
    }
    
    /*
     * Don't allow direct instantiation of BasicClient instances; use the builder instead.
     */
    private BasicClient() {
    }
    
    private BasicClient(final String host, final int port, final String user, final String password,
            final String realm) {
        connect(host, port, user, password, realm);
    }
    
}
