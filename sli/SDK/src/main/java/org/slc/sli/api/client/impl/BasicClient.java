package org.slc.sli.api.client.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.EntityType;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.Query;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.URLBuilder;
import org.slc.sli.api.client.impl.transform.BasicLinkJsonTypeAdapter;
import org.slc.sli.api.client.impl.transform.GenericEntityFromJson;
import org.slc.sli.api.client.impl.transform.GenericEntityToJson;

/**
 * Class defining the methods available to SLI API client applications. The BasicClient
 * follows a builder pattern for creating a connection to the SLI API server. It provides
 * basic CRUD operations once the client connection is established.
 * 
 * @author asaarela
 */
public final class BasicClient implements SLIClient {
    
    private RESTClient restClient;
    private Gson gson = null;
    private static Logger logger = Logger.getLogger("BasicClient");
    
    /**
     * SLIClientBuilder Builder for an BasicClient instance.
     */
    public static class Builder {
        private String apiHost = "http://localhost:8080";
        private String securityHost = "http://localhost:8080";
        
        /**
         * Construct a new Builder.
         */
        public static Builder create() {
            return new Builder();
        }
        
        /**
         * Initialize the builder connection host. This should include protocol and optional
         * port. For example: https://localhost:443
         * 
         * @param host
         *            for the API server.
         * @return an BasicClient.Builder
         */
        public Builder apiHost(final String host) {
            apiHost = host;
            return this;
        }
        
        /**
         * Initialize the builder security host. This should include protocol and optional
         * port. For example: https://localhost:443
         * 
         * @param host
         *            for the SLI security server.
         * @return an BasicClient.Builder
         */
        public Builder securityHost(final String host) {
            securityHost = host;
            return this;
        }
        
        /**
         * Create an BasicClient instance.
         * @return BasicClient
         */
        public SLIClient build() throws IOException {
            BasicClient client = new BasicClient();
            client.init(new URL(apiHost), new URL(securityHost));
            return client;
        }
    }
    
    /**
     * CRUD operations
     */
    
    @Override
    public Response create(final Entity e) throws MalformedURLException, URISyntaxException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).id(e.getId()).build();
        return restClient.postRequest(url, gson.toJson(e));
    }
    
    @Override
    public Response read(EntityCollection entities, final EntityType type, final Query query)
            throws MalformedURLException,
            URISyntaxException {
        
        return read(entities, type, null, query);
    }
    
    @Override
    public Response read(EntityCollection entities, final EntityType type, final String id, final Query query)
            throws MalformedURLException, URISyntaxException {
        
        entities.clear();
        
        URLBuilder builder = URLBuilder.create(restClient.getBaseURL()).entityType(type);
        if (id != null) {
            builder.id(id);
        }
        
        return getResource(entities, builder.build(), query);
    }
    
    
    @Override
    public Response update(final Entity e) throws MalformedURLException, URISyntaxException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).id(e.getId()).build();
        return restClient.putRequest(url, gson.toJson(e));
    }
    
    @Override
    public Response delete(final Entity e) throws MalformedURLException, URISyntaxException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).id(e.getId()).build();
        return restClient.deleteRequest(url);
    }
    
    @Override
    public Response getResource(EntityCollection entities, URL resourceURL, Query query)
            throws MalformedURLException, URISyntaxException {
        entities.clear();
        
        URLBuilder urlBuilder = URLBuilder.create(resourceURL.toString());
        urlBuilder.query(query);
        
        Response response = restClient.getRequest(urlBuilder.build());
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            
            try {
                JsonElement element = gson.fromJson(response.readEntity(String.class), JsonElement.class);
                
                if (element instanceof JsonArray) {
                    entities.fromJsonArray(element.getAsJsonArray());
                    
                } else if (element instanceof JsonObject) {
                    Entity entity = gson.fromJson(element, Entity.class);
                    entities.add(entity);
                    
                } else {
                    // not what was expected....
                    ResponseBuilder builder = Response.fromResponse(response);
                    builder.status(Response.Status.INTERNAL_SERVER_ERROR);
                    return builder.build();
                }
            } catch (JsonSyntaxException e) {
                // invalid Json, or non-Json response?
                ResponseBuilder builder = Response.fromResponse(response);
                builder.status(Response.Status.INTERNAL_SERVER_ERROR);
                return builder.build();
            }
        }
        return response;
    }
    
    /*
     * Don't allow direct instantiation of BasicClient instances; use the builder instead.
     */
    private BasicClient() {
        gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Entity.class, new GenericEntityFromJson())
                .registerTypeAdapter(Entity.class, new GenericEntityToJson())
                .registerTypeAdapter(Link.class, new BasicLinkJsonTypeAdapter())
                .create();
    }
    
    @Override
    public void init(URL apiURL, URL securityURL) {
        restClient = new RESTClient(apiURL, securityURL);
    }
    
    @Override
    public String login() throws IOException {
        try {
            return restClient.connect();
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "Failed to log into security server: {}", restClient.securityServerUri);
            return null;
        }
    }
    
    @Override
    public void logout() {
        restClient.disconnect();
    }
    
    @Override
    public String checkSession(final String token) {
        try {
            return restClient.sessionCheck(token);
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Failed to log into call session check: {}", e.toString());
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "Failed to log into security server: {}", restClient.securityServerUri);
        }
        return null;
    }
    
}
