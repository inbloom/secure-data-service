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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import org.slc.sli.api.client.RESTClient;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.client.security.SliApi;

/**
 *
 * Generic REST client. Provides the ability to connect to a ReSTful web service and make
 *
 * requests.
 */

public class BasicRESTClient implements RESTClient {

    private static final String SESSION_CHECK_PREFIX = "system/session/check";
    private final static Logger LOGGER = Logger.getLogger("RESTClient");
    private String apiServerUri = null;
    private Client client = null;
    private SliApi sliApi = null;
    private String sessionToken = null;
    private OAuthConfig config;
    private Token accessToken;

    /**
     *
     * Construct a new RESTClient instance.
     *
     * @param apiServerURL
     *            Fully qualified URL to the root of the API server.
     *
     * @param clientId
     *            Unique client identifier for this application.
     *
     * @param clientSecret
     *            Unique client secret value for this application.
     *
     * @param callbackURL
     *            URL used to redirect after authentication.
     */

    public BasicRESTClient(final URL apiServerURL, final String clientId, final String clientSecret,
            final URL callbackURL) {

        client = ClientFactory.newClient();

        apiServerUri = apiServerURL.toString().endsWith("/") ? apiServerURL.toString() + PathConstants.API_SERVER_PATH
                : apiServerURL.toString() + "/" + PathConstants.API_SERVER_PATH;

        sliApi = new SliApi();
        SliApi.setBaseUrl(apiServerURL);
        config = new OAuthConfig(clientId, clientSecret, callbackURL.toString(), null, null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#getLoginURL()
     */
    @Override
    public URL getLoginURL() {
        try {
            return new URL(sliApi.getAuthorizationUrl(config));
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, String.format("Failed to create login URL: %s", e.toString()));
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#connect(java.lang.String, java.lang.String)
     */
    @Override
    public Response connect(final String authorizationCode) throws OAuthException, MalformedURLException,
            URISyntaxException {
        OAuthService service = new ServiceBuilder().provider(SliApi.class).apiKey(config.getApiKey())
                .apiSecret(config.getApiSecret()).callback(config.getCallback()).build();
        Verifier verifier = new Verifier(authorizationCode);
        SliApi.TokenResponse r = ((SliApi.SLIOauth20ServiceImpl) service).getAccessToken(
                new Token(config.getApiSecret(), authorizationCode), verifier, null);

        if (r != null && r.getToken() != null) {
            accessToken = r.getToken();
            sessionToken = accessToken.getToken();
        }

        ResponseBuilder builder = Response.status(r.getOauthResponse().getCode());
        for (Map.Entry<String, String> entry : r.getOauthResponse().getHeaders().entrySet()) {
            if (entry.getKey() == null) {
                builder.header("Status", entry.getValue());
            } else {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        builder.entity(r.getOauthResponse().getBody());

        return builder.build();
    }

    @Override
    public void connectWithToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#disconnect()
     */
    @Override
    public void disconnect() {
        // TODO...
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#sessionCheck(java.lang.String)
     */
    @Override
    public String sessionCheck(final String token) throws URISyntaxException, IOException {
        LOGGER.info("Session check URL = " + SESSION_CHECK_PREFIX);
        URL url = new URL(apiServerUri + "/" + SESSION_CHECK_PREFIX);
        Response response = getRequest(url);
        String jsonText = response.readEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode obj = mapper.readTree(jsonText);
        if (obj.has("authenticated")) {
            JsonNode e = obj.get("authenticated");
            if (e.booleanValue()) {
                return sessionToken;
            }
        }
        return "";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#getRequest(java.net.URL)
     */
    @Override
    public Response getRequest(final URL url) throws MalformedURLException, URISyntaxException {
        return getRequestWithHeaders(url, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#getRequestWithHeaders(java.net.URL,
     * java.util.Map)
     */
    @Override
    public Response getRequestWithHeaders(final URL url, final Map<String, Object> headers) throws URISyntaxException {
        if (sessionToken == null) {
            LOGGER.log(Level.SEVERE, String.format("Token is null in call to RESTClient for url: %s", url.toString()));
            return null;
        }
        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(sessionToken, builder, headers);
        Invocation i = builder.buildGet();
        return i.invoke();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#getSessionToken()
     */
    @Override
    public String getSessionToken() {
        return this.sessionToken;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#postRequest(java.net.URL, java.lang.String)
     */
    @Override
    public Response postRequest(final URL url, final String json) throws URISyntaxException, MalformedURLException {
        return postRequestWithHeaders(url, json, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#postRequestWithHeaders(java.net.URL,
     * java.lang.String, java.util.Map)
     */
    @Override
    public Response postRequestWithHeaders(final URL url, final String json, final Map<String, Object> headers)
            throws URISyntaxException, MalformedURLException {
        if (sessionToken == null) {
            LOGGER.log(Level.SEVERE, String.format("Token is null in call to RESTClient for url: %s", url.toString()));
            return null;
        }

        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(sessionToken, builder, headers);
        Invocation i = builder.buildPost(javax.ws.rs.client.Entity.entity(json, MediaType.APPLICATION_JSON));
        return i.invoke();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#putRequest(java.net.URL, java.lang.String)
     */
    @Override
    public Response putRequest(final URL url, final String json) throws MalformedURLException, URISyntaxException {
        return putRequestWithHeaders(url, json, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#putRequestWithHeaders(java.net.URL,
     * java.lang.String, java.util.Map)
     */
    @Override
    public Response putRequestWithHeaders(final URL url, final String json, final Map<String, Object> headers)
            throws MalformedURLException, URISyntaxException {
        if (sessionToken == null) {
            LOGGER.log(Level.SEVERE, String.format("Token is null in call to RESTClient for url: %s", url.toString()));
            return null;
        }

        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(sessionToken, builder, headers);
        Invocation i = builder.buildPut(javax.ws.rs.client.Entity.entity(json, MediaType.APPLICATION_JSON));

        return i.invoke();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#deleteRequest(java.net.URL)
     */
    @Override
    public Response deleteRequest(final URL url) throws MalformedURLException, URISyntaxException {
        return deleteRequestWithHeaders(url, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#deleteRequestWithHeaders(java.net.URL,
     * java.util.Map)
     */
    @Override
    public Response deleteRequestWithHeaders(final URL url, final Map<String, Object> headers)
            throws MalformedURLException, URISyntaxException {
        if (sessionToken == null) {
            LOGGER.log(Level.SEVERE, String.format("Token is null in call to RESTClient for url: %s", url.toString()));
            return null;
        }
        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder = getCommonRequestBuilder(sessionToken, builder, headers);
        Invocation i = builder.buildDelete();
        return i.invoke();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#getBaseURL()
     */
    @Override
    public String getBaseURL() {
        return apiServerUri;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.api.client.impl.IRESTClient#setSessionToken(java.lang.String)
     */
    @Override
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    /**
     *
     * Get a ClientRequest.Builder with common properties already set.
     *
     * @param headers
     *
     * @return
     */
    private Invocation.Builder getCommonRequestBuilder(String sessionToken, Invocation.Builder builder, final Map<String, Object> headers) {
        Map<String, Object> useHeaders = headers; 
        if (useHeaders == null) {
            useHeaders = new HashMap<String, Object>();
        }

        useHeaders.put("Authorization", String.format("Bearer %s", sessionToken));

        for (Map.Entry<String, Object> entry : useHeaders.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        return builder;
    }
}