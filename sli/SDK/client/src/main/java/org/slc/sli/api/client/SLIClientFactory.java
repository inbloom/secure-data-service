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
