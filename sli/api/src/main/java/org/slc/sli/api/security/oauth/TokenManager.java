package org.slc.sli.api.security.oauth;

import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.OAuth2ProviderTokenServices;
import org.springframework.security.oauth2.provider.token.RandomValueOAuth2ProviderTokenServices;

/**
 * Responsible for storage and management of access and refresh tokens for OAuth
 * 2.0 implementation.
 * 
 * @author shalka
 */
public class TokenManager extends RandomValueOAuth2ProviderTokenServices implements OAuth2ProviderTokenServices {
    
    @Override
    protected OAuth2Authentication<?, ?> readAuthentication(OAuth2AccessToken token) {
        return null;
    }
    
    @Override
    protected void storeAccessToken(OAuth2AccessToken token,
            @SuppressWarnings("rawtypes") OAuth2Authentication authentication) {
        
    }
    
    @Override
    protected OAuth2AccessToken readAccessToken(String tokenValue) {
        return null;
    }
    
    @Override
    protected void removeAccessToken(String tokenValue) {
        
    }
    
    @Override
    protected OAuth2Authentication<?, ?> readAuthentication(ExpiringOAuth2RefreshToken token) {
        return null;
    }
    
    @Override
    protected void storeRefreshToken(ExpiringOAuth2RefreshToken refreshToken,
            @SuppressWarnings("rawtypes") OAuth2Authentication authentication) {
        
    }
    
    @Override
    protected ExpiringOAuth2RefreshToken readRefreshToken(String tokenValue) {
        return null;
    }
    
    @Override
    protected void removeRefreshToken(String tokenValue) {
        
    }
    
    @Override
    protected void removeAccessTokenUsingRefreshToken(String refreshToken) {
        
    }
    
}
