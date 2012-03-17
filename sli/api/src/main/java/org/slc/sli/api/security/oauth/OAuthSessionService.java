package org.slc.sli.api.security.oauth;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RandomValueTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

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
    private EntityRepository repo;
    
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
        return null;
    }
     
}
