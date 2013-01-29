/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
import java.net.URL;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.scribe.exceptions.OAuthException;

/**
 * Generic REST client. Provides the ability to connect to a ReSTful web service and make
 * requests.
 *
 */
public interface RESTClient {

    /**
     * Retrieve the resource URL to the identity provider (IDP) used by application users
     * to authenticate. The client application is responsible for redirecting the user
     * to this URL. The response from this URL will contain the authorization token
     * required to connect to the API.
     *
     * @return A URL that directs the user to authenticate with the appropriate IDP. On
     *         successful login, the IDP sends an authorization token to the callbackURL.
     */
    public abstract URL getLoginURL();




    /**
     * Connect to the SLI ReSTful API web service passing the authentication token provided by
     * the IDP. The IDP will redirect successful login attempts to the callbackURL and include
     * an authorization token in the response. You must then pass the authorization token to
     * this call.
     *
     * If the code is invalid, an exception is thrown.
     *
     * @requestCode Code provided to the callbackURL by the IDP.
     * @param authorizationCode
     *            Authorization request code returned by oauth to the callbackURL.
     * @return HTTP Response to the request.
     * @throws OAuthException
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response connect(final String authorizationCode)
            throws OAuthException, MalformedURLException, URISyntaxException;

    /**
     * Connect to the API with a pre authorized token
     *
     * @param sessionToken authorized session token
     */
    public void connectWithToken(final String sessionToken);

    /**
     * Disconnect from the IDP.
     */
    public abstract void disconnect();

    /**
     * Call the session/check API. If the SAML token is invalid or null, this will redirect
     * to the realm selector page.
     *
     * @param token
     *            SAML token or null.
     * @return String containing the authentication token.
     * @throws URISyntaxException
     * @throws IOException
     */
    public abstract String sessionCheck(final String token) throws URISyntaxException, IOException;

    /**
     * Make a synchronous GET request to a REST service.
     *
     * @param url
     *            full URL to the request.
     * @return ClientResponse containing the status code and return values.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response getRequest(final URL url) throws MalformedURLException, URISyntaxException;

    /**
     * Make a synchronous GET request to a REST service. The request includes additional header
     * information.
     *
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param headers
     *            key / value pairs of the headers to attach to the request.
     * @return ClientResponse containing the status code and return value(s).
     * @throws URISyntaxException
     */
    public abstract Response getRequestWithHeaders(final URL url, final Map<String, Object> headers)
            throws URISyntaxException;


    /**
     * Synchronously post a new entity to the REST service. This corresponds to a create operation.
     *
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param json
     *            Json entity to post.
     * @return ClientResponse containing the status code and return value(s).
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public abstract Response postRequest(final URL url, final String json) throws URISyntaxException,
            MalformedURLException;

    /**
     * Synchronously post a new entity to the REST service. This request includes additional header
     * information.
     *
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param json
     *            JSON to post.
     * @param headers
     *            key / value pairs of the headers to attach to the request. A key can map
     *            to multiple values.
     * @return ClientResponse containing the status code and return value(s).
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public abstract Response postRequestWithHeaders(final URL url, final String json, final Map<String, Object> headers)
            throws URISyntaxException, MalformedURLException;

    /**
     * Synchronous Put request to the REST service. This corresponds to an update operation.
     *
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param json
     *            JSON of the entity to PUT.
     * @return ClientResponse containing the status code and return value(s).
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response putRequest(final URL url, final String json) throws MalformedURLException,
            URISyntaxException;

    /**
     * Synchronous Put request to the REST service. This corresponds to an update operation.
     * This request includes additional header information.
     *
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param json
     *            JSON of the entity to PUT.
     * @param headers
     *            key / value pairs of the headers to attach to the request. A key can map
     *            to multiple values.
     * @return ClientResponse containing the status code and return value(s).
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response putRequestWithHeaders(final URL url, final String json, final Map<String, Object> headers)
            throws MalformedURLException, URISyntaxException;

    /**
     * Synchronously delete an existing entity using the REST service.
     *
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @return ClientResponse containing the status code and return value(s).
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response deleteRequest(final URL url) throws MalformedURLException, URISyntaxException;

    /**
     * Synchronously delete an existing entity using the REST service. This request includes
     * additional header
     * information.
     *
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @param headers
     *            key / value pairs of the headers to attach to the request. A key can map
     *            to multiple values.
     * @return ClientResponse containing the status code and return value(s).
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response deleteRequestWithHeaders(final URL url, final Map<String, Object> headers)
            throws MalformedURLException, URISyntaxException;

    /**
     * Get the base URL for all SLI API ReSTful service calls.
     *
     * @return Server URL string.
     */
    public abstract String getBaseURL();

    /**
     * Set the sessionToken for all SLI API ReSTful service calls.
     *
     * @param sessionToken
     */
    public abstract void setSessionToken(String sessionToken);

    /**
     * Get the sessionToken for all SLI API ReSTful service calls.
     *
     * @return sessionToken
     */
    public abstract String getSessionToken();

}
