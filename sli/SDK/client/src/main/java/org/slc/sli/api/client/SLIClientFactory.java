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

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.scribe.exceptions.OAuthException;

/**
 * Factory for creating an SLI client
 *
 * @author nbrown
 *
 */
public interface SLIClientFactory {

    /**
     * Get an SLIClient with an authorization request code
     *
     * @param authorizationCode
     *            Authorization request code returned by oauth to the callbackURL.
     * @return a connected SLIClient
     */
    SLIClient getClientWithAuthCode(String authorizationCode) throws OAuthException, MalformedURLException, URISyntaxException;

    /**
     * Get an SLIClient with a session token
     *
     * @param sessionToken
     *            SAML token
     * @return a connected SLIClient
     */
    SLIClient getClientWithSessionToken(String sessionToken) throws OAuthException, MalformedURLException, URISyntaxException;
}
