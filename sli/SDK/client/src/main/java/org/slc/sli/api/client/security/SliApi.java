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
    
    private static URL apiUrl;
    private static final String REQUEST_TOKEN_FRAGMENT = "%s/api/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s";
    private static final String AUTH_TOKEN_FRAGMENT = "%s/api/oauth/token?grant_type=authorization_code";
    
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
    
    public URL getApiUrl() {
        return apiUrl;
    }
    
    public void setBaseUrl(URL baseUrl) {
        apiUrl = baseUrl;
    }
}
