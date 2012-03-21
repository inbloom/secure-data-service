package org.slc.sli.api.security.oauth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RandomValueTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.enums.Right;

/**
 * Handles Authorization and Resource Server token services by extending
 * RandomValueTokenServices.
 * 
 * @author shalka
 * 
 */
@Component
public class OAuthSessionService extends RandomValueTokenServices {

    private static final String OAUTH_ACCESS_TOKEN_COLLECTION = OAuthTokenUtil.getOAuthAccessTokenCollectionName();
    private static final int REFRESH_TOKEN_VALIDITY_SECONDS = OAuthTokenUtil.getRefreshTokenValidity(); 
    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = OAuthTokenUtil.getAccessTokenValidity();
    
    @Autowired
    private Repository<Entity> repo;
    
    @Autowired
    private TokenStore mongoTokenStore;
    
    @Autowired
    private OAuthTokenUtil util;
    
    @PostConstruct
    public void init() {
        setRefreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
        setAccessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
        setSupportRefreshToken(true);
        setTokenStore(mongoTokenStore);
    }
    
    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue)
            throws AuthenticationException {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria("token", "=", accessTokenValue));
        
        
        Iterable<Entity> results = repo.findAll(OAUTH_ACCESS_TOKEN_COLLECTION, neutralQuery);
        for (Entity oauth2Session : results) {
            Map data = (Map) oauth2Session.getBody().get("authentication");
            return util.createOAuth2Authentication(data);
        }
        
        String time = "" + System.currentTimeMillis();
        return new OAuth2Authentication(new ClientToken("UNKNOWM", "UNKNOWN", new HashSet<String>()), new AnonymousAuthenticationToken(time, time, Arrays.<GrantedAuthority>asList(Right.ANONYMOUS_ACCESS)));
    }
     
}
