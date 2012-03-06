package org.slc.sli.security;

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
    
    private String apiUrl = "http://local.slidev.org:8080";
    private String authorizeUrl = apiUrl + "/api/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s";

    @Override
    public String getAccessTokenEndpoint() {
        return apiUrl + "/api/oauth/token?grant_type=authorization_code";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        System.out.println("TEMP - In getAuthURL config has it as " + config.getApiKey() + " redirect URI " + config.getCallback());
        Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback.");
        
        return String.format(authorizeUrl, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
    
    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
      return new SliTokenExtractor();
    }

}
