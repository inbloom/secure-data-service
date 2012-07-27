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
