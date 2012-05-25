package org.slc.sli.api.client.impl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
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

import org.scribe.exceptions.OAuthException;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.impl.transform.BasicLinkJsonTypeAdapter;
import org.slc.sli.api.client.impl.transform.GenericEntityFromJson;
import org.slc.sli.api.client.impl.transform.GenericEntityToJson;
import org.slc.sli.common.util.Query;
import org.slc.sli.common.util.URLBuilder;

/**
 * Class defining the methods available to SLI API client applications. It provides
 * basic CRUD operations once the client connection is established.
 *
 * @author asaarela
 * @author rbloh
 */
public class BasicClient implements SLIClient {

    private RESTClient restClient;
    private Gson gson = null;
    private static Logger logger = Logger.getLogger("BasicClient");

    @Override
    public URL getLoginURL() {
        return restClient.getLoginURL();
    }

    @Override
    public String connect(final String authorizationCode) throws OAuthException {
        try {
            return restClient.connect(authorizationCode);
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

    /**
     * CRUD operations
     */

    @Override
    public Response create(final Entity e) throws MalformedURLException, URISyntaxException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).build();
        return restClient.postRequest(this.getToken(), url, gson.toJson(e.getData()));
    }

    @Override
    public Response create(final String sessionToken, final String resourceUrl, final Entity e)
            throws MalformedURLException, URISyntaxException {
        return restClient.postRequest(sessionToken, new URL(restClient.getBaseURL() + resourceUrl),
                gson.toJson(e.getData()));
    }

    @Override
    public Response read(EntityCollection entities, final String type, final Query query) throws MalformedURLException,
            URISyntaxException {

        return read(entities, type, null, query);
    }

    @Override
    public Response read(EntityCollection entities, final String type, final String id, final Query query)
            throws MalformedURLException, URISyntaxException {

        entities.clear();

        URLBuilder builder = URLBuilder.create(restClient.getBaseURL()).entityType(type);
        if (id != null) {
            builder.id(id);
        }

        return getResource(entities, builder.build(), query);
    }

    @Override
    public Response read(final String sessionToken, List entities, final String resourceUrl, Class entityClass)
            throws MalformedURLException, URISyntaxException {

        entities.clear();

        return getResource(sessionToken, entities, new URL(restClient.getBaseURL() + resourceUrl), entityClass);
    }

    @Override
    public Response update(final Entity e) throws MalformedURLException, URISyntaxException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).id(e.getId()).build();
        return restClient.putRequest(this.getToken(), url, gson.toJson(e.getData()));
    }

    @Override
    public Response update(final String sessionToken, final String resourceUrl, final Entity e)
            throws MalformedURLException, URISyntaxException {
        return restClient.putRequest(sessionToken, new URL(restClient.getBaseURL() + resourceUrl),
                gson.toJson(e.getData()));
    }

    @Override
    public Response delete(final Entity e) throws MalformedURLException, URISyntaxException {
        URL url = URLBuilder.create(restClient.getBaseURL()).entityType(e.getEntityType()).id(e.getId()).build();
        return restClient.deleteRequest(this.getToken(), url);
    }

    @Override
    public Response delete(final String sessionToken, final String resourceUrl) throws MalformedURLException,
            URISyntaxException {
        return restClient.deleteRequest(sessionToken, new URL(restClient.getBaseURL() + resourceUrl));
    }

    @Override
    public Response getResource(EntityCollection entities, URL resourceURL, Query query) throws MalformedURLException,
            URISyntaxException {
        entities.clear();

        URLBuilder urlBuilder = URLBuilder.create(resourceURL.toString());
        urlBuilder.query(query);

        Response response = restClient.getRequest(this.getToken(), urlBuilder.build());
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

    @Override
    public Response getResource(final String sessionToken, List entities, URL restURL, Class entityClass)
            throws MalformedURLException, URISyntaxException {
        entities.clear();

        Response response = restClient.getRequest(sessionToken, restURL);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {

            try {
                JsonElement element = gson.fromJson(response.readEntity(String.class), JsonElement.class);

                if (element instanceof JsonArray) {
                    JsonArray array = (JsonArray) element;
                    for (int i = 0; i < array.size(); ++i) {
                        JsonObject jsonObject = array.get(i).getAsJsonObject();
                        Object entity = gson.fromJson(jsonObject, entityClass);
                        entities.add(entity);
                    }
                } else {
                    Object entity = gson.fromJson(element, entityClass);
                    entities.add(entity);
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
        gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Entity.class, new GenericEntityFromJson())
                .registerTypeAdapter(Entity.class, new GenericEntityToJson())
                .registerTypeAdapter(Link.class, new BasicLinkJsonTypeAdapter()).create();
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

    /**
     * Get the sessionToken for all SLI API ReSTful service calls.
     *
     * @return sessionToken
     */
    @Override
    public String getToken() {
        return restClient.getSessionToken();
    }
}
