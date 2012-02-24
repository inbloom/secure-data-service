package org.slc.sli.api.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.code.UnconfirmedAuthorizationCodeClientToken;

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
    private static final String OAUTH_ACCESS_TOKEN_COLLECTION = "oauth_access_token";
    private static final String OAUTH_REFRESH_TOKEN_COLLECTION = "oauth_refresh_token";
    /**
     * Lifetime (duration of validity) of an Access Token in seconds.
     */
    private static final int ACCESS_TOKEN_VALIDITY = 1800;
    
    /**
     * Lifetime (duration of validity) of a Refresh Token in seconds.
     */
    private static final int REFRESH_TOKEN_VALIDITY = 3600;
    
    /**
     * Lifetime (duration of validity) of an Authorization Code in seconds.
     */
    private static final int AUTHORIZATION_CODE_VALIDITY = 300;
    
    /**
     * Get the name of the collection in Mongo that stores OAuth 2.0 session
     * information.
     * 
     * @return String representing Mongo collection name.
     */
    public static String getOAuthAccessTokenCollectionName() {
        return OAUTH_ACCESS_TOKEN_COLLECTION;
    }
    
    public static String getOAuthRefreshTokenCollectionName() {
        return OAUTH_REFRESH_TOKEN_COLLECTION;
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
        return mapSliPrincipal(userAuthentication.getPrincipal());
    }
    
    /**
     * Maps the specified SLI Principal object into an EntityBody for storage
     * into the oauthSession collection (stored in userAuthn field).
     * 
     * @param principal
     *            Resolved user authentication object to be mapped.
     * @return EntityBody containing relevant values for SLI Principal.
     */
    public static EntityBody mapSliPrincipal(Object user) {
        EntityBody entity = new EntityBody();
        if (user instanceof User) {
            User principal = (User) user;
            entity.put("externalId", principal.getUsername());
            //entity.put("", principal.getAuthorities());
        } else {
            SLIPrincipal principal = (SLIPrincipal) user;
            entity.put("userId", principal.getId());
            entity.put("userRealm", principal.getRealm());
            entity.put("userRoles", principal.getRoles().toString());
            entity.put("externalId", principal.getExternalId());
            entity.put("mongoEntityId", principal.getEntity().getEntityId().toString());
        }
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
    
    /**
     * Maps an client into an EntityBody from the client authentication within the
     * UnconfirmedAuthorizationCodeAuthenticationTokenHolder object.
     * 
     * @param clientAuthentication
     *            Object from the UnconfirmedAuthorizationCodeAuthenticationTokenHolder being
     *            mapped.
     * @return EntityBody containing the relevant data.
     */
    public static EntityBody mapUnconfirmedClientAuthentication(
            UnconfirmedAuthorizationCodeClientToken clientAuthentication) {
        EntityBody clientBody = new EntityBody();
        clientBody.put("clientId", clientAuthentication.getClientId());
        clientBody.put("clientSecret", clientAuthentication.getClientSecret());
        clientBody.put("clientScope", clientAuthentication.getScope().toString());
        // clientAuthentication.getState();
        clientBody.put("redirectUri", clientAuthentication.getRequestedRedirect());
        return clientBody;
    }
    
    /**
     * Maps an input authorization code into the EntityBody of a verification code object.
     * 
     * @param code
     *            String representing the authorization code.
     * @return EntityBody containing the relevant data.
     */
    public static EntityBody mapAuthorizationCode(String code, String redirectUri, String userName) {
        EntityBody authorizationCode = new EntityBody();
        long expiration = AUTHORIZATION_CODE_VALIDITY * 1000L;
        authorizationCode.put("value", code);
        authorizationCode.put("expiration", new Date().getTime() + expiration);
        authorizationCode.put("redirectUri", redirectUri);
        authorizationCode.put("userName", userName);
        return authorizationCode;
    }
    
    /**
     * Returns the validity of a refresh token in seconds.
     * 
     * @return Integer representing the number of seconds that the refresh token is valid for.
     */
    public static int getRefreshTokenValidity() {
        return REFRESH_TOKEN_VALIDITY;
    }
    
    /**
     * Returns the validity of an access token in seconds.
     * 
     * @return Integer representing the number of seconds that the access token is valid for.
     */
    public static int getAccessTokenValidity() {
        return ACCESS_TOKEN_VALIDITY;
    }
    
    public static byte[] serialize(Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return baos.toByteArray();
    }
    
    public static Object deserialize(byte[] b) {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois;
        Object toReturn = null;
        try {
            ois = new ObjectInputStream(bais);
            toReturn = ois.readObject();
            ois.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return toReturn;
    }
    
}
