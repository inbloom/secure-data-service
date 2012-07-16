/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.client.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

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
import org.slc.sli.api.client.SLIClientException;
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
public class BasicClient implements SLIClient {

    private RESTClient restClient;
    private static Logger logger = Logger.getLogger("BasicClient");
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public URL getLoginURL() {
        return restClient.getLoginURL();
    }

    @Override
    public Response connect(final String requestCode, String authorizationToken) throws OAuthException {
        try {
            return restClient.connect(requestCode, authorizationToken);
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
    public String create(final Entity e) throws URISyntaxException, IOException, SLIClientException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).build();
        Response response = restClient.postRequest(url, mapper.writeValueAsString(e));
        checkResponse(response, Status.CREATED, "Could not created entity.");

        // extract the id of the newly created entity from the header.
        String location = response.getHeader("Location");
        return location.substring(location.lastIndexOf("/") + 1);
        //  return restClient.postRequest(this.getToken(), url, mapper.writeValueAsString(e)); NOTE: added
    }

    @Override
    public Response create(final String sessionToken, final String resourceUrl, final Entity e)
            throws URISyntaxException, IOException {
        return restClient.postRequest(sessionToken, new URL(restClient.getBaseURL() + resourceUrl),
                mapper.writeValueAsString(e));
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
    public Response read(final String sessionToken, List entities, final String resourceUrl, Class entityClass)
            throws URISyntaxException, MessageProcessingException, IOException {
        entities.clear();
        return getResource(sessionToken, entities, new URL(restClient.getBaseURL() + resourceUrl), entityClass);
    }

    @Override
    public Response update(final Entity e) throws URISyntaxException, MessageProcessingException, IOException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).id(e.getId()).build();
        return restClient.putRequest(url, mapper.writeValueAsString(e));
    }

    @Override
    public void delete(final String entityType, final String entityId) throws MalformedURLException, URISyntaxException, SLIClientException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(entityType).id(entityId).build();
        checkResponse(restClient.deleteRequest(url), Status.NO_CONTENT, "Could not delete entity.");
    }

    @Override
    public Response update(final String sessionToken, final String resourceUrl, final Entity e)
            throws IOException, URISyntaxException {
        return restClient.putRequest(sessionToken, new URL(restClient.getBaseURL() + resourceUrl),
                mapper.writeValueAsString(e));
    }


    @SuppressWarnings("unchecked")
    @Override
    public Response deleteByToken(final String sessionToken, final String resourceUrl) throws URISyntaxException, MalformedURLException {
        return restClient.deleteRequest(sessionToken, new URL(restClient.getBaseURL() + resourceUrl));
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

    @SuppressWarnings("unchecked")
	@Override
    public Response getResource(final String sessionToken, List entities, URL restURL, Class entityClass)
            throws URISyntaxException, MessageProcessingException, IOException {
        entities.clear();

        Response response = restClient.getRequest(sessionToken, restURL);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {

            try {
                JsonNode element = mapper.readValue(response.readEntity(String.class), JsonNode.class);

                if (element.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) element;
                    for (int i = 0; i < arrayNode.size(); ++i) {
                        JsonNode jsonObject = arrayNode.get(i);
                        Object entity =  mapper.readValue(jsonObject, entityClass);
                        entities.add(entity);
                    }
                } else  if (element instanceof ObjectNode) {
                    Object entity = mapper.readValue(element, entityClass);
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
    @Override
    public void setToken(String sessionToken) {
        restClient.setSessionToken(sessionToken);
    }

    @Override
    public String getToken() {
        return restClient.getSessionToken();
    }

    private void checkResponse(Response response, Status status, String msg) throws SLIClientException {
        if (response.getStatus() != status.getStatusCode()) {
           throw new SLIClientException(msg + "Receveived status code " + response.getStatus() + ". Expected " + status.getStatusCode() + ".");
        }
    }
}
