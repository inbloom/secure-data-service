package org.slc.sli.api.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientToken;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;

/**
 * Utilities for the OAuth 2.0 implementations of SliTokenService and
 * SliTokenStore.
 * 
 * @author shalka
 * 
 */
public class OAuthTokenUtil {
    
    /**
     * Name of the collection in Mongo that stores OAuth 2.0 session
     * information.
     */
    private static final String OAUTH_SESSION_COLLECTION = "oauthSession";
    
    /**
     * Get the name of the collection in Mongo that stores OAuth 2.0 session
     * information.
     * 
     * @return String representing Mongo collection name.
     */
    public static String getOAuthCollectionName() {
        return OAUTH_SESSION_COLLECTION;
    }
    
    /**
     * Maps the specified Client Authentication object into an EntityBody for
     * storage into the oauthSession collection.
     * 
     * @param accessToken
     *            Client Authentication object to be mapped.
     * @return EntityBody containing relevant values of Client Authentication.
     */
    public static EntityBody mapClientAuthentication(ClientToken clientToken) {
        EntityBody entity = new EntityBody();
        entity.put("clientId", clientToken.getClientId());
        entity.put("clientSecret", clientToken.getClientSecret());
        entity.put("clientScope", clientToken.getScope().toString());
        return entity;
    }
    
    /**
     * Maps the specified User Authentication object into an EntityBody for
     * storage into the oauthSession collection by resolving its type to
     * SLIPrincipal and calling the appropriate function.
     * 
     * @param accessToken
     *            User Authentication object to be mapped.
     * @return EntityBody containing relevant values of User Authentication.
     */
    public static EntityBody mapUserAuthentication(Authentication userAuthentication) {
        SLIPrincipal principal = (SLIPrincipal) userAuthentication.getPrincipal();
        return mapSliPrincipal(principal);
    }
    
    /**
     * Maps the specified SLI Principal object into an EntityBody for storage
     * into the oauthSession collection (stored in userAuthn field).
     * 
     * @param principal
     *            Resolved user authentication object to be mapped.
     * @return EntityBody containing relevant values for SLI Principal.
     */
    public static EntityBody mapSliPrincipal(SLIPrincipal principal) {
        EntityBody entity = new EntityBody();
        entity.put("userId", principal.getId());
        entity.put("userRealm", principal.getRealm());
        entity.put("userRoles", principal.getRoles().toString());
        entity.put("externalId", principal.getExternalId());
        entity.put("mongoEntityId", principal.getEntity().getEntityId().toString());
        return entity;
    }
    
    /**
     * Maps the specified Access Token into an EntityBody for storage into the
     * oauthSession collection.
     * 
     * @param accessToken
     *            Access Token to be mapped.
     * @return EntityBody containing relevant values of Access Token (including
     *         Refresh Token).
     */
    public static EntityBody mapAccessToken(OAuth2AccessToken accessToken) {
        EntityBody refreshToken = new EntityBody();
        refreshToken.put("value", accessToken.getRefreshToken().getValue());
        refreshToken.put("expiration", ((ExpiringOAuth2RefreshToken) accessToken.getRefreshToken()).getExpiration()
                .getTime());
        
        EntityBody entity = new EntityBody();
        entity.put("value", accessToken.getValue());
        entity.put("expiration", accessToken.getExpiration().getTime());
        entity.put("tokenType", accessToken.getTokenType());
        entity.put("refreshToken", refreshToken);
        return entity;
    }
    
    /**
     * Returns true if the current time (in ms) is greater than the specified
     * expiration date (indicating that expiration is true).
     * 
     * @param expiration
     *            Date to be checked (represented by number of milliseconds since last epoch).
     * @return 'true' if expired, 'false' if not expired.
     */
    public static boolean isTokenExpired(long expiration) {
        return System.currentTimeMillis() > expiration;
    }
}
