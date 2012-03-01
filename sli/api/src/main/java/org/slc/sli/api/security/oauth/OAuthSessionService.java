package org.slc.sli.api.security.oauth;

import javax.annotation.PostConstruct;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
        Iterable<Entity> results = repo.findByQuery(OAUTH_ACCESS_TOKEN_COLLECTION, new Query(Criteria.where("body.token").is(accessTokenValue)), 0, 1);
        for (Entity oauth2Session : results) {
            
            OAuth2Authentication auth = (OAuth2Authentication) OAuthTokenUtil.deserialize((byte[]) oauth2Session.getBody().get("authenticationBlob"));
            return auth;
        }
        return null;
    }
     
}
