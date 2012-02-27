package org.slc.sli.api.security.oauth;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private EntityRepository repo;

    @Autowired
    private EntityDefinitionStore store;

    
    private static final String OAUTH_ACCESS_TOKEN_COLLECTION = OAuthTokenUtil.getOAuthAccessTokenCollectionName();

    private static final String OAUTH_REFRESH_TOKEN_COLLECTION = OAuthTokenUtil.getOAuthRefreshTokenCollectionName();
    /**
     * Finds the OAuth2Authentication object in the oauthSession collection that
     * corresponds to the specified Access Token. Assume that, if an Access
     * Token exists, and it is valid, then the user has already authenticated.
     */
    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_ACCESS_TOKEN_COLLECTION,
                new Query(Criteria.where("body.token").is(token.getValue())), 0, 1);
       
        for (Entity oauth2Session : results) {
            return (OAuth2Authentication) OAuthTokenUtil.deserialize((byte[]) oauth2Session.getBody().get("authenticationBlob"));
        }
        return null;
    }

    /**
     * Finds the OAuth2Authentication object in the oauthSession collection that
     * corresponds to the specified Refresh Token.
     */
    @Override
    public OAuth2Authentication readAuthentication(ExpiringOAuth2RefreshToken token) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_ACCESS_TOKEN_COLLECTION,
                new Query(Criteria.where("body.refreshToken").is(token.getValue())), 0, 1);
       
        for (Entity oauth2Session : results) {
            return (OAuth2Authentication) OAuthTokenUtil.deserialize((byte[]) oauth2Session.getBody().get("authenticationBlob"));
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
        body.put("refreshToken", token.getRefreshToken().getValue());
        body.put("tokenBlob", OAuthTokenUtil.serialize(token));
        body.put("authenticationBlob", OAuthTokenUtil.serialize(authentication));
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
        Iterable<Entity> results = repo.findByQuery(OAUTH_ACCESS_TOKEN_COLLECTION,
                new Query(Criteria.where("body.token").is(tokenValue)), 0, 1);
       
        for (Entity oauth2Session : results) {
            return (OAuth2AccessToken) OAuthTokenUtil.deserialize((byte[]) oauth2Session.getBody().get("tokenBlob"));
        }
        return null;
    }

    /**
     * Removes the Access Token's validation by expiring it immediately.
     */
    @Override
    public void removeAccessToken(String tokenValue) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_ACCESS_TOKEN_COLLECTION,
                new Query(Criteria.where("body.token").is(tokenValue)), 0, 1);
       
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
        
        String token = refreshToken.getValue();
        final EntityBody body = new EntityBody();
        body.put("token", token);
        body.put("authenticationBlob", OAuthTokenUtil.serialize(authentication));
        body.put("refreshTokenBlob", OAuthTokenUtil.serialize(refreshToken));
        SecurityUtil.sudoRun(new SecurityTask<Boolean>() {
            @Override
            public Boolean execute() {
                getRefreshTokenService().create(body);
                return true;
            }
        });
    }

    /**
     * Determines if there is a Refresh Token in the oauthSessions collection
     * that matches the specified Refresh Token value.
     */
    @Override
    public ExpiringOAuth2RefreshToken readRefreshToken(String tokenValue) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_REFRESH_TOKEN_COLLECTION,
                new Query(Criteria.where("body.token").is(tokenValue)), 0, 1);
       
        for (Entity oauth2Session : results) {
            return (ExpiringOAuth2RefreshToken) OAuthTokenUtil.deserialize((byte[]) oauth2Session.getBody().get("refreshTokenBlob"));
        }
        return null;
    }

    /**
     * Removes the Refresh Token's validation by expiring it immediately.
     */
    @Override
    public void removeRefreshToken(String tokenValue) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_REFRESH_TOKEN_COLLECTION,
                new Query(Criteria.where("body.token").is(tokenValue)), 0, 1);
       
        for (Entity oauth2Session : results) {
            getRefreshTokenService().delete(oauth2Session.getEntityId());
        }
    }

    /**
     * Removes the Access Token's validation by expiring it immediately (lookup
     * done on Refresh Token value). This does not remove the refresh token's
     * validation.
     */
    @Override
    public void removeAccessTokenUsingRefreshToken(String refreshToken) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_ACCESS_TOKEN_COLLECTION,
                new Query(Criteria.where("body.refreshToken").is(refreshToken)), 0, 1);
       
        for (Entity oauth2Session : results) {
            getAccessTokenService().delete(oauth2Session.getEntityId());
        }
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
    
    public EntityService getRefreshTokenService() {
        EntityDefinition defn = store.lookupByResourceName(OAUTH_REFRESH_TOKEN_COLLECTION);
        return defn.getService();
    }
}
