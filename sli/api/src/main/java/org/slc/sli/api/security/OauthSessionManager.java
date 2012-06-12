package org.slc.sli.api.security;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.security.oauth.OAuthAccessException;
import org.slc.sli.domain.Entity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Contract for handling
 *
 * @author dkornishev
 *
 */
public interface OauthSessionManager {
    public void createAppSession(String sessionId, String clientId, String redirectUri, String state, String tenantId, String samlId);
    public Entity getSessionForSamlId(String samlId);
    public Map<String, Object> getAppSession(String samlId, Entity session);
    public void updateSession(Entity session);
    public String verify(String code, Pair<String, String> clientCredentials) throws OAuthAccessException;
    public OAuth2Authentication getAuthentication(String authz);
    public Entity getSession(String sessionId);
    public boolean logout(String accessToken);
}
