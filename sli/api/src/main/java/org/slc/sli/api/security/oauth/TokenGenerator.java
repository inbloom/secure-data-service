package org.slc.sli.api.security.oauth;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

/**
 * 
 * @author pwolf
 *
 */
public class TokenGenerator {

    private static final char[] DEFAULT_CODEC = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            .toCharArray();
    
    private static Random random = new SecureRandom();

    /**
     * Generates a random string containing characters a-z, A-Z, 0-9
     * 
     * @param length length of string
     * @return a securely random string
     */
    public static String generateToken(int length) {
        byte[] verifierBytes = new byte[length];
        random.nextBytes(verifierBytes);
        return getAuthorizationCodeString(verifierBytes);
    }
    
    /**
     * Grabbed this out of {@link RandomValueAuthorizationCodeServices}
     * 
     * @param verifierBytes The bytes.
     * @return The string.
     */
    protected static String getAuthorizationCodeString(byte[] verifierBytes) {
        char[] chars = new char[verifierBytes.length];
        for (int i = 0; i < verifierBytes.length; i++) {
            chars[i] = DEFAULT_CODEC[((verifierBytes[i] & 0xFF) % DEFAULT_CODEC.length)];
        }
        return new String(chars);
    }


}
