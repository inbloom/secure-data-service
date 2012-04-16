package org.slc.sli.api.security;

import java.net.URI;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import org.slc.sli.domain.Entity;

/**
 * Contract for handling
 * 
 * @author dkornishev
 * 
 */
public interface OauthSessionManager {
    public void createAppSession(String sessionId, String clientId, String redirectUri, String state, String tenantId, String samlId);
    public Pair<String, URI> composeRedirect(String samlId, SLIPrincipal principal);
    public String verify(String code, Pair<String, String> clientCredentials) throws BadCredentialsException;
    public OAuth2Authentication getAuthentication(String authz);
    public Entity getSession(String sessionId);
    public boolean logout(String accessToken);
}
