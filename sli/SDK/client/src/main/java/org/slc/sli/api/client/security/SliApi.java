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


package org.slc.sli.api.client.security;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

import java.net.URL;

/**
 * @author jnanney
 *
 */
public class SliApi extends DefaultApi20 {

    private static URL apiUrl;
    private static final String REQUEST_TOKEN_FRAGMENT = "%sapi/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s";
    private static final String AUTH_TOKEN_FRAGMENT = "%sapi/oauth/token?grant_type=authorization_code";

    @Override
    public String getAccessTokenEndpoint() {
        return String.format(AUTH_TOKEN_FRAGMENT, apiUrl.toString());
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback.");

        return String.format(REQUEST_TOKEN_FRAGMENT, apiUrl.toString(), config.getApiKey(),
                OAuthEncoder.encode(config.getCallback()));
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new SliTokenExtractor();
    }

    /**
     * @param baseUrl
     *            the base URL for the API ReST server.
     */
    public static void setBaseUrl(final URL baseUrl) {
        SliApi.apiUrl = baseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OAuthService createService(OAuthConfig config) {
        return new SLIOauth20ServiceImpl(this, config);
    }

    /**
     * POJO for Token and Response
     */
    public static class TokenResponse {
        private Token token;
        private Response oauthResponse;
        public Token getToken() {
            return token;
        }
        public void setToken(Token token) {
            this.token = token;
        }
        public Response getOauthResponse() {
            return oauthResponse;
        }
        public void setOauthResponse(Response oauthResponse) {
            this.oauthResponse = oauthResponse;
        }

    }

    /**
     * SLI Implementation of the OAuth 2.0 Service
     */
    public class SLIOauth20ServiceImpl extends OAuth20ServiceImpl {

        private final DefaultApi20 myApi;
        private final OAuthConfig myConfig;

        public SLIOauth20ServiceImpl(DefaultApi20 api, OAuthConfig config) {
            super(api, config);
            myApi = api;
            myConfig = config;
        }

        public TokenResponse getAccessToken(Token requestToken, Verifier verifier, Token t) {
            TokenResponse tokenResponse = new TokenResponse();

            OAuthRequest request = new OAuthRequest(myApi.getAccessTokenVerb(), myApi.getAccessTokenEndpoint());
            request.addQuerystringParameter(OAuthConstants.CLIENT_ID, myConfig.getApiKey());
            request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, myConfig.getApiSecret());
            request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
            request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, myConfig.getCallback());
            if (myConfig.hasScope()) {
                request.addQuerystringParameter(OAuthConstants.SCOPE, myConfig.getScope());
            }

            Response response = request.send();

            tokenResponse.oauthResponse = response;
            tokenResponse.token = myApi.getAccessTokenExtractor().extract(response.getBody());
            return tokenResponse;
        }
    }
}
