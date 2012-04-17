package org.slc.sli.api.client.security;

import java.net.URL;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * @author jnanney
 *
 */
public class SliApi extends DefaultApi20 {

    // TODO - this assumes we're sharing this across all sessions. Is this assumption valid?
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
     * @param baseUrl the base URL for the API ReST server.
     */
    public static void setBaseUrl(final URL baseUrl) {
        SliApi.apiUrl = baseUrl;
    }
}
