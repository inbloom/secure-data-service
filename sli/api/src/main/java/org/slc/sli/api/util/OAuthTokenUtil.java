package org.slc.sli.api.util;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

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
        return SerializationUtils.serialize((Serializable) o);
    }
    
    public static Object deserialize(byte[] b) {
        return SerializationUtils.deserialize(b);
    }
    
}
