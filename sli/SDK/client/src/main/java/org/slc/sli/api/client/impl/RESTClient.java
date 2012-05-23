package org.slc.sli.api.client.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import org.slc.sli.api.client.security.SliApi;
import org.slc.sli.common.constants.v1.PathConstants;
/**
 * Generic REST client. Provides the ability to connect to a ReSTful web service and make
 * requests.
 */
public class RESTClient {

    private static final String SESSION_CHECK_PREFIX = "api/rest/v1/system/session/check";

    private static Logger logger = Logger.getLogger("RESTClient");
    private String apiServerUri = null;
    private Client client = null;
    private SliApi sliApi = null;
    private String sessionToken = null;
    private OAuthConfig config;

    private Token accessToken;

    /**
     * Construct a new RESTClient instance.
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
    public RESTClient(final URL apiServerURL, final String clientId, final String clientSecret, final URL callbackURL) {
        client = ClientFactory.newClient();
        apiServerUri = apiServerURL.toString().endsWith("/") ? apiServerURL.toString() + PathConstants.API_SERVER_PATH
                : apiServerURL.toString() + "/" + PathConstants.API_SERVER_PATH;

        sliApi = new SliApi();
        SliApi.setBaseUrl(apiServerURL);

        config = new OAuthConfig(clientId, clientSecret, callbackURL.toString(), null, null, null);
    }

    /**
     * Get the URL used to authenticate with the IDP.
     *
     * @return URL
     */
    public URL getLoginURL() {
        try {
            return new URL(sliApi.getAuthorizationUrl(config));
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, String.format("Failed to create login URL: %s", e.toString()));
        }
        return null;
    }

    /**
     * Connect to the IDP and redirect to the callback URL.
     *
     * @param authorizationCode
     *            Authorization code returned by oauth.
     * @return String authorization token from the OAuth service.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public String connect(final String authorizationCode) throws MalformedURLException, URISyntaxException {

        logger.log(
                Level.INFO,
                String.format("Client ID is %s clientSecret is %s callbackURL is %s", config.getApiKey(),
                        config.getApiSecret(), config.getCallback()));

        OAuthService service = new ServiceBuilder().provider(SliApi.class).apiKey(config.getApiKey())
                .apiSecret(config.getApiSecret()).callback(config.getCallback()).build();

        logger.log(Level.INFO, String.format("Oauth request token %s", authorizationCode));

        Verifier verifier = new Verifier(authorizationCode);
        accessToken = service.getAccessToken(null, verifier);
        sessionToken = accessToken.getToken();
        return accessToken.getRawResponse();
    }

    /**
     * Disconnect from the IDP.
     */
    public void disconnect() {
        // TODO...
    }

    /**
     * Call the session/check API. If the SAML token is invalid or null, this will redirect
     * to the realm selector page.
     *
     * @param token
     *            SAML token or null.
     * @param redirectUrl
     *            The redirect URL after a successful authentication - set by the Security API.
     * @return String containing the authentication token.
     * @throws URISyntaxException
     * @throws IOException
     * @throws JsonProcessingException
     */
    public String sessionCheck(final String token) throws URISyntaxException, JsonProcessingException, IOException {
        logger.info("Session check URL = " + SESSION_CHECK_PREFIX);

        URL url = new URL(apiServerUri + "/" + SESSION_CHECK_PREFIX);

        Response response = getRequest(url);

        String jsonText = response.readEntity(String.class);
        logger.info("jsonText = " + jsonText);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode obj = mapper.readTree(jsonText);

        if (obj.has("authenticated")) {
            JsonNode e = obj.get("authenticated");
            if (e.getBooleanValue()) {
                e = obj.get("sessionId");
                sessionToken = e.getValueAsText();
            }
        }

        return sessionToken;
    }

    /**
     * Make a synchronous GET request to a REST service.
     *
     * @param url
     *            full URL to the request.
     * @return ClientResponse containing the status code and return values.
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public Response getRequest(final URL url) throws MalformedURLException, URISyntaxException {

        return getRequestWithHeaders(url, null);
    }

    /**
     * Make a synchronous GET request to a REST service. The request includes additional header
     * information.
     *
     * @param url
     *
     * @param URL
     *            Fully qualified URL to the ReSTful resource.
     * @param headers
     *            key / value pairs of the headers to attach to the request.
     * @return ClientResponse containing the status code and return value(s).
     * @throws URISyntaxException
     */
    public Response getRequestWithHeaders(final URL url, final Map<String, Object> headers) throws URISyntaxException {

        if (sessionToken == null) {
            logger.log(Level.SEVERE, String.format("Token is null in call to RESTClient for url: %s", url.toString()));
            return null;
        }

        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(builder, headers);

        Invocation i = builder.buildGet();
        return i.invoke();
    }

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
    public Response postRequest(final URL url, final String json) throws URISyntaxException, MalformedURLException {

        return postRequestWithHeaders(url, json, null);
    }

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
    public Response postRequestWithHeaders(final URL url, final String json, final Map<String, Object> headers)
            throws URISyntaxException, MalformedURLException {

        if (sessionToken == null) {
            logger.log(Level.SEVERE, String.format("Token is null in call to RESTClient for url: %s", url.toString()));
            return null;
        }

        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(builder, headers);

        Invocation i = builder.buildPost(javax.ws.rs.client.Entity.entity(json, MediaType.APPLICATION_JSON));
        return i.invoke();
    }

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
    public Response putRequest(final URL url, final String json) throws MalformedURLException, URISyntaxException {
        return putRequestWithHeaders(url, json, null);
    }

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
    public Response putRequestWithHeaders(final URL url, final String json, final Map<String, Object> headers)
            throws MalformedURLException, URISyntaxException {

        if (sessionToken == null) {
            logger.log(Level.SEVERE, String.format("Token is null in call to RESTClient for url: %s", url.toString()));
            return null;
        }

        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(builder, headers);

        Invocation i = builder.buildPut(javax.ws.rs.client.Entity.entity(json, MediaType.APPLICATION_JSON));
        return i.invoke();
    }

    /**
     * Synchronously delete an existing entity using the REST service.
     *
     * @param url
     *            Fully qualified URL to the ReSTful resource.
     * @return ClientResponse containing the status code and return value(s).
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public Response deleteRequest(final URL url) throws MalformedURLException, URISyntaxException {
        return deleteRequestWithHeaders(url, null);
    }

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
    public Response deleteRequestWithHeaders(final URL url, final Map<String, Object> headers)
            throws MalformedURLException, URISyntaxException {

        if (sessionToken == null) {
            logger.log(Level.SEVERE, String.format("Token is null in call to RESTClient for url: %s", url.toString()));
            return null;
        }

        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(builder, headers);

        Invocation i = builder.buildDelete();
        return i.invoke();
    }

    /**
     * Get the base URL for all SLI API ReSTful service calls.
     *
     * @return Server URL string.
     */
    public String getBaseURL() {
        return apiServerUri;
    }

    /**
     * Set the sessionToken for all SLI API ReSTful service calls.
     *
     * @param sessionToken
     */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    /**
     * Get a ClientRequest.Builder with common properties already set.
     *
     * @param headers
     * @return
     */
    private Invocation.Builder getCommonRequestBuilder(Invocation.Builder builder, Map<String, Object> headers) {

        if (headers == null) {
            headers = new HashMap<String, Object>();
        }

        headers.put("Authorization", String.format("Bearer %s", sessionToken));

        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        return builder;
    }
}
