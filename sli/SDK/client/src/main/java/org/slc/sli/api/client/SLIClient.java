package org.slc.sli.api.client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.core.Response;

import org.scribe.exceptions.OAuthException;

import org.slc.sli.common.util.Query;

/**
 * Interface defining the methods available to SLI API client applications. It provides
 * basic CRUD operations available once the client connection is established.
 *
 * @author asaarela
 * @author rbloh
 */
public interface SLIClient {

    /**
     * Retrieve the login URL a consuming application needs to hit to locate the IDP
     * landing zone. The client application is responsible for redirecting the user
     * to this URL. The response from this URL will contain the authorization code
     * required to connect to the API.
     *
     * Each client application has a client id and a secret that uniquely identifies the
     * application. These must be passed here to form the correct login URL. The SLI security
     * layer will verify the clientid and secret match.
     *
     * The callbackURL is the URL the authentication system redirects to after a successful
     * login. THis must be exactly the same as the callback defined in the SLI database
     * for the application.
     *
     * @return A URL that directs the user to authenticate with the appropriate IDP.
     */
    public abstract URL getLoginURL();

    /**
     * Connect to the SLI ReSTful API web service and v with the IDP. The IDP will redirect
     * successful login attempts to the callbackURL and include an authorization code in the
     * response. This auth code is then passed to this call and verified. If the code is
     * invalid, an exception is thrown.
     *
     * @param authorizationCode
     *
     * @param requestToken
     *            Oauth2 authorization code returned by the login URL.
     * @return String authorization token for the authenticated user, or null if
     *         authentication fails.
     * @throws OAuthException
     */
    public abstract String connect(final String authorizationCode) throws OAuthException;

    /**
     * Set access token
     *
     * @param sessionToken
     *            Session token
     */
    public abstract void setToken(String sessionToken);

    /**
     * Get access token
     *
     * @return sessionToken
     *         Session token
     */
    public abstract String getToken();

    /**
     * Logout and invalidate the session.
     */
    public abstract void logout();

    /**
     * Create operation
     *
     * @param sessionToken
     *            Session token.
     * @param e
     *            Entity to create
     * @return Response to the create request.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response create(final Entity e) throws MalformedURLException, URISyntaxException;

    /**
     * Create operation
     *
     * @param sessionToken
     *            Session token.
     * @param resourceUrl
     *            The ReST resource url suffix
     * @param e
     *            Entity to create
     * @return Response to the create request.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response create(final String sessionToken, final String resourceUrl, final Entity e)
            throws MalformedURLException, URISyntaxException;

    /**
     * Read operation by ID.
     *
     * @param sessionToken
     *            Session token.
     * @param entities
     *            Entities returned by the API.
     * @param type
     *            The type of entity
     * @param id
     *            The ID of the entity to read.
     * @param query
     *            Query parameters.
     * @return ClientResponse from the ReST call.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response read(EntityCollection entities, final String type, final String id, final Query query)
            throws MalformedURLException, URISyntaxException;

    /**
     * Read operation
     *
     * @param sessionToken
     *            Session token.
     * @param entities
     *            Entities returned by the API.
     * @param type
     *            The type of entity
     * @param query
     *            Query parameters.
     * @return ClientResponse from the ReST call.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response read(EntityCollection entities, final String type, final Query query)
            throws MalformedURLException, URISyntaxException;

    /**
     * Read operation
     *
     * @param sessionToken
     *            Session token.
     * @param entities
     *            Entities returned by the API.
     * @param resourceUrl
     *            The ReST resource url suffix
     * @param entityClass
     *            Entity class.
     * @return ClientResponse from the ReST call.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response read(final String sessionToken, List entities, final String resourceUrl, Class entityClass)
            throws MalformedURLException, URISyntaxException;

    /**
     * Update operation
     *
     * @param sessionToken
     *            Session token.
     * @param e
     *            Entity to update.
     * @return Response to the update request.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response update(final Entity e) throws MalformedURLException, URISyntaxException;

    /**
     * Update operation
     *
     * @param sessionToken
     *            Session token.
     * @param resourceUrl
     *            The ReST resource url suffix
     * @param e
     *            Entity to update.
     * @return Response to the update request.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response update(final String sessionToken, final String resourceUrl, final Entity e)
            throws MalformedURLException, URISyntaxException;

    /**
     * Delete operation
     *
     * @param sessionToken
     *            Session token.
     * @param e
     *            Entity to delete
     * @return Response to the delete request.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response delete(final Entity e) throws MalformedURLException, URISyntaxException;

    /**
     * Delete operation
     *
     * @param sessionToken
     *            Session token.
     * @param resourceUrl
     *            The ReST resource url suffix
     * @return Response to the delete request.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response delete(final String sessionToken, final String resourceUrl) throws MalformedURLException,
            URISyntaxException;

    /**
     * Perform a get operation against a generic resource. This is useful when following links
     * returned by other resources, for example.
     *
     * @param sessionToken
     *            Session token.
     * @param entities
     *            Entities returned by the API in response to this request.
     * @param resourceURL
     *            URL to get
     * @param query
     *            Query to append to the resource.
     * @return ClientResponse from the ReST call.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response getResource(EntityCollection entities, final URL resourceURL, final Query query)
            throws MalformedURLException, URISyntaxException;

    /**
     * Perform a get operation against a generic resource. This is useful when following links
     * returned by other resources, for example.
     *
     * @param sessionToken
     *            Session token.
     * @param entities
     *            Entities returned by the API in response to this request.
     * @param restURL
     *            ReST URL to get
     * @param entityClass
     *            Entity class.
     * @return ClientResponse from the ReST call.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public abstract Response getResource(final String sessionToken, List entities, final URL restURL, Class entityClass)
            throws MalformedURLException, URISyntaxException;

}
