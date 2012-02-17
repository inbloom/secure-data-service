package org.slc.sli.api.security.oauth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

/**
 * Responsible for storage and management of access and refresh tokens for OAuth
 * 2.0 implementation.
 * 
 * @author shalka
 */
@Component
public class MongoTokenStore implements TokenStore {
    
    private static final String OAUTH_SESSION_COLLECTION = OAuthTokenUtil.getOAuthCollectionName();
    
    @Autowired
    private EntityRepository repo;
    
    @Autowired
    private EntityDefinitionStore store;
    
    /**
     * Finds the OAuth2Authentication object in the oauthSession collection that
     * corresponds to the specified Access Token. Assume that, if an Access
     * Token exists, and it is valid, then the user has already authenticated.
     */
    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.accessToken.value").is(token.getValue())), 0, 1);
        return OAuthTokenUtil.mapOAuth2Authentication(results);
    }
    
    /**
     * Finds the OAuth2Authentication object in the oauthSession collection that
     * corresponds to the specified Refresh Token.
     */
    @Override
    public OAuth2Authentication readAuthentication(ExpiringOAuth2RefreshToken token) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.accessToken.refreshToken.value").is(token.getValue())), 0, 1);
        return OAuthTokenUtil.mapOAuth2Authentication(results);
    }
    
    /**
     * Stores an access token and corresponding OAuth2Authentication object in
     * the oauthSession collection.
     */
    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        EntityBody clientAuthentication = OAuthTokenUtil.mapClientAuthentication(authentication
                .getClientAuthentication());
        EntityBody userAuthentication = OAuthTokenUtil.mapUserAuthentication(authentication.getUserAuthentication());
        EntityBody accessToken = OAuthTokenUtil.mapAccessToken(token);
        
        EntityBody oauth2Session = new EntityBody();
        oauth2Session.put("clientAuthn", clientAuthentication);
        oauth2Session.put("userAuthn", userAuthentication);
        oauth2Session.put("accessToken", accessToken);
        
        getService().create(oauth2Session);
    }
    
    /**
     * Determines if there is a valid Access Token in the oauthSessions
     * collection that matches the specified Access Token value.
     */
    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.accessToken.value").is(tokenValue)), 0, 1);
        if (results != null) {
            for (Entity oauth2Session : results) {
                Map<String, Object> oauthSessionBody = oauth2Session.getBody();
                
                @SuppressWarnings("unchecked")
                Map<String, Object> accessToken = (Map<String, Object>) oauthSessionBody.get("accessToken");
                
                Date accessTokenExpiration = (Date) accessToken.get("expiration");
                
                // make sure the access token hasn't expired
                if (!OAuthTokenUtil.isTokenExpired(accessTokenExpiration)) {
                    OAuth2AccessToken result = new OAuth2AccessToken(tokenValue);
                    result.setExpiration((Date) accessToken.get("expiration"));
                    result.setTokenType((String) accessToken.get("tokenType"));
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> refreshToken = (Map<String, Object>) accessToken.get("refreshToken");
                    
                    String refreshTokenValue = (String) refreshToken.get("value");
                    Date refreshTokenExpiration = (Date) refreshToken.get("expiration");
                    
                    ExpiringOAuth2RefreshToken rt = new ExpiringOAuth2RefreshToken(refreshTokenValue,
                            refreshTokenExpiration);
                    result.setRefreshToken(rt);
                    return result;
                }
            }
        }
        return null;
    }
    
    /**
     * Removes the Access Token's validation by expiring it immediately.
     */
    @Override
    public void removeAccessToken(String tokenValue) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.accessToken.value").is(tokenValue)), 0, 1);
        if (results != null) {
            for (Entity oauth2Session : results) {
                @SuppressWarnings("unchecked")
                Map<String, Object> accessToken = (Map<String, Object>) oauth2Session.getBody().get("accessToken");
                
                Date accessTokenExpiration = (Date) accessToken.get("expiration");
                
                if (!OAuthTokenUtil.isTokenExpired(accessTokenExpiration)) {
                    Map<String, Object> oauthSession = (Map<String, Object>) oauth2Session.getBody();
                    
                    accessToken.put("expiration", new Date());
                    getService().update(oauth2Session.getEntityId(), (EntityBody) oauthSession);
                }
            }
        }
    }
    
    /**
     * Performs a lookup on the OAuth2Authentication object, and stores the
     * Refresh Token into the corresponding Access Token.
     */
    @Override
    public void storeRefreshToken(ExpiringOAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        SLIPrincipal principal = (SLIPrincipal) authentication.getUserAuthentication().getPrincipal();
        if (principal != null) {
            Entity oauthSession = repo.find(OAUTH_SESSION_COLLECTION, principal.getEntity().getEntityId());
            if (oauthSession != null) {
                Map<String, Object> body = oauthSession.getBody();
                Map<String, Object> rt = new HashMap<String, Object>();
                rt.put("value", refreshToken.getValue());
                rt.put("expiration", refreshToken.getExpiration());
                @SuppressWarnings("unchecked")
                Map<String, Object> accessToken = (Map<String, Object>) body.get("accessToken");
                accessToken.put("refreshToken", rt);
                getService().update(oauthSession.getEntityId(), (EntityBody) body);
            }
        }
    }
    
    /**
     * Determines if there is a Refresh Token in the oauthSessions collection
     * that matches the specified Refresh Token value.
     */
    @SuppressWarnings("unchecked")
    @Override
    public ExpiringOAuth2RefreshToken readRefreshToken(String tokenValue) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.accessToken.refreshToken.value").is(tokenValue)), 0, 1);
        if (results != null) {
            for (Entity oauthSession : results) {
                Map<String, Object> accessToken = (Map<String, Object>) oauthSession.getBody().get("accessToken");
                Map<String, Object> refreshToken = (Map<String, Object>) accessToken.get("refreshToken");
                Date expirationDate = (Date) refreshToken.get("expiration");
                return new ExpiringOAuth2RefreshToken(tokenValue, expirationDate);
            }
        }
        return null;
    }
    
    /**
     * Removes the Refresh Token's validation by expiring it immediately.
     */
    @Override
    public void removeRefreshToken(String tokenValue) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.accessToken.refreshToken.value").is(tokenValue)), 0, 1);
        if (results != null) {
            for (Entity oauthSession : results) {
                @SuppressWarnings("unchecked")
                Map<String, Object> refreshToken = (Map<String, Object>) oauthSession.getBody().get(
                        "accessToken.refreshToken");
                
                Date refreshTokenExpiration = (Date) refreshToken.get("expiration");
                
                if (!OAuthTokenUtil.isTokenExpired(refreshTokenExpiration)) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> accessToken = (Map<String, Object>) oauthSession.getBody().get("accessToken");
                    
                    refreshToken.put("expiration", new Date());
                    accessToken.put("refreshToken", refreshToken);
                    oauthSession.getBody().put("accessToken", accessToken);
                    getService().update(oauthSession.getEntityId(), (EntityBody) oauthSession.getBody());
                }
            }
        }
    }
    
    /**
     * Removes the Access Token's validation by expiring it immediately (lookup
     * done on Refresh Token value). This does not remove the refresh token's
     * validation.
     */
    @Override
    public void removeAccessTokenUsingRefreshToken(String refreshToken) {
        Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
                new Query(Criteria.where("body.accessToken.refreshToken.value").is(refreshToken)), 0, 1);
        if (results != null) {
            for (Entity oauthSession : results) {
                @SuppressWarnings("unchecked")
                Map<String, Object> access = (Map<String, Object>) oauthSession.getBody().get("accessToken");
                
                @SuppressWarnings("unchecked")
                Map<String, Object> refresh = (Map<String, Object>) oauthSession.getBody().get(
                        "accessToken.refreshToken");
                
                Date accessTokenTokenExpiration = (Date) access.get("expiration");
                
                Date refreshTokenTokenExpiration = (Date) refresh.get("expiration");
                
                if (!OAuthTokenUtil.isTokenExpired(accessTokenTokenExpiration)
                        && !OAuthTokenUtil.isTokenExpired(refreshTokenTokenExpiration)) {
                    access.put("expiration", new Date());
                    oauthSession.getBody().put("accessToken", access);
                    getService().update(oauthSession.getEntityId(), (EntityBody) oauthSession.getBody());
                }
            }
        }
    }
    
    /**
     * Gets the EntityService associated with the OAuth 2.0 session collection.
     * 
     * @return Instance of EntityService for performing collection operations.
     */
    private EntityService getService() {
        return store.lookupByResourceName(OAUTH_SESSION_COLLECTION).getService();
    }
}
