package org.slc.sli.api.security.oauth;

import java.util.Map;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

/**
 * Responsible for storage and management of access and refresh tokens for OAuth
 * 2.0 implementation.
 * 
 * @author shalka
 */
@Component
public class MongoTokenStore implements TokenStore {

    @Autowired
    private Repository<Entity> repo;

    @Autowired
    private EntityDefinitionStore store;
    
    @Autowired
    private OAuthTokenUtil util;
    
    private static final String OAUTH_ACCESS_TOKEN_COLLECTION = OAuthTokenUtil.getOAuthAccessTokenCollectionName();

    /**
     * Finds the OAuth2Authentication object in the oauthSession collection that
     * corresponds to the specified Access Token. Assume that, if an Access
     * Token exists, and it is valid, then the user has already authenticated.
     */
    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("token=" + token.getValue()));
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        Iterable<Entity> results = repo.findAll(OAUTH_ACCESS_TOKEN_COLLECTION, neutralQuery);
       
        for (Entity oauth2Session : results) {
            return util.createOAuth2Authentication((Map) oauth2Session.getBody().get("authentication"));
        }
        return null;
    }

    /**
     * Finds the OAuth2Authentication object in the oauthSession collection that
     * corresponds to the specified Refresh Token.
     */
    @Override
    public OAuth2Authentication readAuthentication(ExpiringOAuth2RefreshToken token) {
        
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("refreshToken=" + token.getValue()));
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        
        Iterable<Entity> results = repo.findAll(OAUTH_ACCESS_TOKEN_COLLECTION, neutralQuery);
       
        for (Entity oauth2Session : results) {
            return util.createOAuth2Authentication((Map) oauth2Session.getBody().get("authentication"));
        }
        return null;
    }

    /**
     * Stores an access token and corresponding OAuth2Authentication object in
     * the oauthSession collection.
     */
    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String tokenValue = token.getValue();
        
        final EntityBody body = new EntityBody();
        body.put("token", tokenValue);
        body.put("accessToken", util.serializeAccessToken(token));
        body.put("authentication", util.serializeOauth2Auth(authentication));
        final EntityService service = getAccessTokenService();
        SecurityUtil.sudoRun(new SecurityTask<Boolean>() {

            @Override
            public Boolean execute() {
                service.create(body);
                return Boolean.TRUE;
            }

        });
    }

    /**
     * Determines if there is a valid Access Token in the oauthSessions
     * collection that matches the specified Access Token value.
     */
    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("token=" + tokenValue));
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        Iterable<Entity> results = repo.findAll(OAUTH_ACCESS_TOKEN_COLLECTION, neutralQuery);
       
       
        for (Entity oauth2Session : results) {
            return util.deserializeAccessToken((Map) oauth2Session.getBody().get("accessToken"));
        }
        return null;
    }

    /**
     * Removes the Access Token's validation by expiring it immediately.
     */
    @Override
    public void removeAccessToken(String tokenValue) {

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("token=" + tokenValue));
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        Iterable<Entity> results = repo.findAll(OAUTH_ACCESS_TOKEN_COLLECTION, neutralQuery);
       
        for (Entity oauth2Session : results) {
            getAccessTokenService().delete(oauth2Session.getEntityId());
        }
    }

    /**
     * Performs a lookup on the OAuth2Authentication object, and stores the
     * Refresh Token into the corresponding Access Token.
     */
    @Override
    public void storeRefreshToken(ExpiringOAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        throw new RuntimeException("Refresh tokens not supported");
    }

    /**
     * Determines if there is a Refresh Token in the oauthSessions collection
     * that matches the specified Refresh Token value.
     */
    @Override
    public ExpiringOAuth2RefreshToken readRefreshToken(String tokenValue) {

        throw new RuntimeException("Refresh tokens not supported");
        
    }

    /**
     * Removes the Refresh Token's validation by expiring it immediately.
     */
    @Override
    public void removeRefreshToken(String tokenValue) {

        throw new RuntimeException("Refresh tokens not supported");
    }

    /**
     * Removes the Access Token's validation by expiring it immediately (lookup
     * done on Refresh Token value). This does not remove the refresh token's
     * validation.
     */
    @Override
    public void removeAccessTokenUsingRefreshToken(String refreshToken) {

        throw new RuntimeException("Refresh tokens not supported");
    }

    /**
     * Gets the EntityService associated with the OAuth 2.0 session collection.
     * 
     * @return Instance of EntityService for performing collection operations.
     */
    public EntityService getAccessTokenService() {
        EntityDefinition defn = store.lookupByResourceName(OAUTH_ACCESS_TOKEN_COLLECTION);
        return defn.getService();
    }

}
