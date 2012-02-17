package org.slc.sli.api.util;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.domain.Entity;

/**
 * Utilities for the OAuth 2.0 implementations of SliTokenService and
 * SliTokenStore.
 * 
 * @author shalka
 * 
 */
public class OAuthTokenUtil {
    
    @Autowired
    private static RolesToRightsResolver rolesToRightsResolver;
    
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
        refreshToken.put("expiration", ((ExpiringOAuth2RefreshToken) accessToken.getRefreshToken()).getExpiration());
        
        EntityBody entity = new EntityBody();
        entity.put("value", accessToken.getValue());
        entity.put("expiration", accessToken.getExpiration());
        entity.put("tokenType", accessToken.getTokenType());
        entity.put("refreshToken", refreshToken);
        return entity;
    }
    
    /**
     * 
     * @param queryResult
     * @return
     */
    public static OAuth2Authentication mapOAuth2Authentication(Iterable<Entity> queryResult) {
        if (queryResult != null) {
            for (Entity oauth2Session : queryResult) {
                Map<String, Object> body = oauth2Session.getBody();
                Date accessTokenExpiration = (Date) body.get("accessToken.expiration");
                
                // make sure the access token hasn't expired
                if (!OAuthTokenUtil.isTokenExpired(accessTokenExpiration)) {
                    String clientId = (String) body.get("clientAuthn.clientId");
                    String clientSecret = (String) body.get("clientAuthn.clientSecret");
                    Set<String> clientScope = new HashSet<String>();
                    clientScope.add((String) body.get("clientAuthn.clientScope"));
                    
                    String userId = (String) body.get("userAuthn.userId");
                    String userRealm = (String) body.get("userAuthn.userRealm");
                    @SuppressWarnings("unchecked")
                    List<String> userRoles = (List<String>) body.get("userAuthn.userRoles");
                    String userExtId = (String) body.get("userAuthn.externalId");
                    Entity mongoEntityId = (Entity) body.get("userAuthn.mongoEntityId");
                    
                    SLIPrincipal principal = new SLIPrincipal();
                    principal.setId(userId);
                    principal.setRealm(userRealm);
                    principal.setRoles(userRoles);
                    principal.setExternalId(userExtId);
                    principal.setEntity(mongoEntityId);
                    
                    String samlMessageId = (String) body.get("samlMessageId");
                    
                    Set<GrantedAuthority> userAuthorities = rolesToRightsResolver.resolveRoles(userRealm, userRoles);
                    
                    ClientToken clientAuthentication = new ClientToken(clientId, clientSecret, clientScope);
                    Authentication userAuthentication = new PreAuthenticatedAuthenticationToken(principal,
                            samlMessageId, userAuthorities);
                    
                    return new OAuth2Authentication(clientAuthentication, userAuthentication);
                } else {
                    throw new InvalidTokenException("token is expired");
                }
            }
        }
        throw new InvalidTokenException("no matching token in database");
    }
    
    /**
     * Returns true if the current time (in ms) is greater than the specified
     * expiration date (indicating that expiration is true).
     * 
     * @param expiration
     *            Date to be checked.
     * @return 'true' if expired, 'false' if not expired.
     */
    public static boolean isTokenExpired(Date expiration) {
        return System.currentTimeMillis() > expiration.getTime();
    }
}
