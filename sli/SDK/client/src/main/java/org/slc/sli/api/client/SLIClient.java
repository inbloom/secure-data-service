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

package org.slc.sli.api.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.core.Response;

import org.slc.sli.api.client.util.Query;

/**
 * Interface defining the methods available to SLI API client applications. It provides
 * basic CRUD operations available once the client connection is established. To establish
 * a connect, call the 'connect' method.
 *
 * Each client application has a client id and a secret that uniquely identifies the
 * application. These values are provided during the application registration process.
 * These values are passed to SLI API and validated against the registered application.
 * The SLI security layer will verify the clientid and secret match.
 *
 * Each client application must provide a callbackURL that the SLI API uses to return the
 * result of the users authentication attempt. This must be exactly the same as the callback
 * defined in the SLI database for the application. The callbackURL receives an oauth2
 * authorization token on successful login.
 *
 * @author asaarela
 */
public interface SLIClient {

    /**
     * Create operation
     *
     * @param e
     *            Entity to create
     * @return Response to the update request.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public abstract String create(final Entity e) throws IOException, URISyntaxException, SLIClientException;

    /**
     * Create operation
     *
     * @param e
     *            Entity to create
     * @param resourceUrl the url to post on
     * @return Response to the update request.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public abstract String create(final Entity e, String resourceUrl) throws IOException, URISyntaxException, SLIClientException;

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
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws MessageProcessingException
     * @throws SLIClientException
     */
    public abstract void read(List<Entity> entities, final String type, final String id, final Query query)
            throws URISyntaxException, MessageProcessingException, IOException, SLIClientException;

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
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws MessageProcessingException
     */
    public abstract void read(List<Entity> entities, final String type, final Query query) throws URISyntaxException,
            MessageProcessingException, IOException, SLIClientException;

    /**
     * Read operation
     *
     * @param resourceUrl
     *            The ReST resource url suffix
     * @return ClientResponse from the ReST call.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract List<Entity> read(final String resourceUrl) throws URISyntaxException,
            MessageProcessingException, IOException, SLIClientException;

    /**
     * Read operation
     *
     * @param resourceUrl
     *            The ReST resource url suffix
     * @return ClientResponse from the ReST call.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract List<Entity> read(final String resourceUrl, Query query) throws URISyntaxException,
            MessageProcessingException, IOException, SLIClientException;

    /**
     * Update operation
     *
     * @param e
     *            Entity to update.
     * @return Response to the update request.
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws MessageProcessingException
     */
    public abstract void update(final Entity e) throws URISyntaxException, MessageProcessingException, IOException,
            SLIClientException;

     /**
     * Delete operation
     *
     * @param e
     *            Entity to delete
     * @return Response to the delete request.
     *
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract void delete(final String entityType, final String entityId) throws MalformedURLException,
            URISyntaxException, SLIClientException;

    /**
     * Delete operation
     *
     * @param e
     *            Entity to delete
     * @return Response to the delete request.
     *
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract void delete(Entity e) throws MalformedURLException, URISyntaxException, SLIClientException;

    /**
     * Get a RESTClient for more low level operations
     *
     * @return
     */
    public abstract RESTClient getRESTClient();

    // Deprecated
    /**
     * Get the home resource for the authenticated user.
     *
     * @return ClientResponse from the ReST call.
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws MessageProcessingException
     */
    public abstract Response getHomeResource(Entity home) throws URISyntaxException, MessageProcessingException,
            IOException;

}
