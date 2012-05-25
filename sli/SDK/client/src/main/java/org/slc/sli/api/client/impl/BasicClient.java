package org.slc.sli.api.client.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MessageProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import org.scribe.exceptions.OAuthException;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.client.util.Query;
import org.slc.sli.api.client.util.URLBuilder;

/**
 * Class defining the methods available to SLI API client applications. It provides
 * basic CRUD operations once the client connection is established.
 * 
 * You must first redirect the user to the URL returned by 'getLoginURL' and handle the
 * response from the IDP in a registered callback URL. Pass the authorization token returned
 * in the callback to the 'connect' method.
 * 
 * @author asaarela
 */
public final class BasicClient implements SLIClient {
    
    private RESTClient restClient;
    private static Logger logger = Logger.getLogger("BasicClient");
    ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public URL getLoginURL() {
        return restClient.getLoginURL();
    }
    
    @Override
    public String connect(final String authorizationToken) throws OAuthException {
        try {
            return restClient.connect(authorizationToken);
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, String.format("Invalid/malformed URL when connecting: %s", e.toString()));
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, String.format("Invalid/malformed URL when connecting: %s", e.toString()));
        }
        return null;
    }
    
    @Override
    public void logout() {
        // TODO - implement this when logout becomes available.
    }
    
    @Override
    public Response create(final Entity e) throws URISyntaxException, IOException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).build();
        return restClient.postRequest(url, mapper.writeValueAsString(e));
    }
    
    @Override
    public Response read(List<Entity> entities, final String type, final Query query) throws URISyntaxException,
            MessageProcessingException, IOException {
        
        return read(entities, type, null, query);
    }
    
    @Override
    public Response read(List<Entity> entities, final String type, final String id, final Query query)
            throws URISyntaxException, MessageProcessingException, IOException {
        
        entities.clear();
        
        URLBuilder builder = URLBuilder.create(restClient.getBaseURL()).entityType(type);
        if (id != null) {
            builder.id(id);
        }
        
        return getResource(entities, builder.build(), query);
    }
    
    @Override
    public Response update(final Entity e) throws URISyntaxException, MessageProcessingException, IOException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).id(e.getId()).build();
        return restClient.putRequest(url, mapper.writeValueAsString(e));
    }
    
    @Override
    public Response delete(final Entity e) throws MalformedURLException, URISyntaxException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).id(e.getId()).build();
        return restClient.deleteRequest(url);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Response getResource(List<Entity> entities, URL resourceURL, Query query) throws URISyntaxException,
            MessageProcessingException, IOException {
        entities.clear();
        
        URLBuilder urlBuilder = URLBuilder.create(resourceURL.toString());
        urlBuilder.query(query);
        
        Response response = restClient.getRequest(urlBuilder.build());
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            try {
                JsonNode element = mapper.readValue(response.readEntity(String.class), JsonNode.class);
                if (element instanceof ArrayNode) {
                    List<Entity> tmp = mapper.readValue(element, new TypeReference<List<GenericEntity>>() {
                    });
                    entities.addAll(tmp);
                } else if (element instanceof ObjectNode) {
                    Entity entity = mapper.readValue(element, GenericEntity.class);
                    entities.add(entity);
                } else {
                    // not what was expected....
                    ResponseBuilder builder = Response.fromResponse(response);
                    builder.status(Response.Status.INTERNAL_SERVER_ERROR);
                    return builder.build();
                }
            } catch (JsonParseException e) {
                // invalid Json, or non-Json response?
                ResponseBuilder builder = Response.fromResponse(response);
                builder.status(Response.Status.INTERNAL_SERVER_ERROR);
                return builder.build();
            }
        }
        return response;
    }
    
    @Override
    public Response getHomeResource(Entity home) throws URISyntaxException, MessageProcessingException, IOException {
        
        URL url = URLBuilder.create(restClient.getBaseURL()).addPath(PathConstants.HOME).build();
        
        Response response = restClient.getRequest(url);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            try {
                JsonNode element = mapper.readValue(response.readEntity(String.class), JsonNode.class);
                Map<String, List<Link>> links = mapper.readValue(element,
                        new TypeReference<Map<String, List<BasicLink>>>() {
                        });
                home.getData().putAll(links);
            } catch (JsonParseException e) {
                // invalid Json, or non-Json response?
                ResponseBuilder builder = Response.fromResponse(response);
                builder.status(Response.Status.INTERNAL_SERVER_ERROR);
                return builder.build();
            }
        }
        return response;
    }
    
    /**
     * Construct a new BasicClient instance, using the JSON message converter.
     * 
     * @param apiServerURL
     *            Fully qualified URL to the root of the API server.
     * @param clientId
     *            Unique client identifier for this application.
     * @param clientSecret
     *            Unique client secret value for this application.
     * @param callbackURL
     *            URL used to redirect after authentication.
     */
    public BasicClient(final URL apiServerURL, final String clientId, final String clientSecret, final URL callbackURL) {
        restClient = new RESTClient(apiServerURL, clientId, clientSecret, callbackURL);
    }
    
    /**
     * Set the sessionToken for all SLI API ReSTful service calls.
     * 
     * @param sessionToken
     */
    public void setToken(String sessionToken) {
        restClient.setSessionToken(sessionToken);
    }
}
