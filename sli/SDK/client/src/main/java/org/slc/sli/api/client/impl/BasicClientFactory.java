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
package org.slc.sli.api.client.impl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.scribe.exceptions.OAuthException;

import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.SLIClientFactory;

/**
 * Basic implmentation of an SLIClientFactory
 *
 * @author nbrown
 *
 */
public class BasicClientFactory implements SLIClientFactory {
    private final URL apiServerURL;
    private final String clientId;
    private final String clientSecret;
    private final URL callbackURL;

    public BasicClientFactory(URL apiServerURL, String clientId, String clientSecret, URL callbackURL) {
        super();
        this.apiServerURL = apiServerURL;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.callbackURL = callbackURL;
    }

    @Override
    public SLIClient getClientWithAuthCode(String authorizationCode) throws OAuthException, MalformedURLException,
            URISyntaxException {
        BasicRESTClient restClient = new BasicRESTClient(apiServerURL, clientId, clientSecret, callbackURL);
        restClient.connect(authorizationCode);
        SLIClient client = new BasicClient(restClient);
        return client;
    }

    @Override
    public SLIClient getClientWithSessionToken(String sessionToken) {
        BasicRESTClient restClient = new BasicRESTClient(apiServerURL, clientId, clientSecret, callbackURL);
        restClient.connectWithToken(sessionToken);
        SLIClient client = new BasicClient(restClient);
        return client;
    }

}
