/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.security;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import org.slc.sli.api.security.oauth.OAuthAccessException;
import org.slc.sli.domain.Entity;

/**
 * Contract for handling
 *
 * @author dkornishev
 *
 */
public interface OauthSessionManager {
    public void createAppSession(String sessionId, String clientId, String redirectUri, String state, String tenantId, String realmId, String samlId, boolean sessionExpired);
    public Entity getSessionForSamlId(String samlId);
    public Map<String, Object> getAppSession(String samlId, Entity session);
    public void updateSession(Entity session);
    public String verify(String code, Pair<String, String> clientCredentials) throws OAuthAccessException;
    public OAuth2Authentication getAuthentication(String authz);
    public Entity getSession(String sessionId);
    public boolean logout(String accessToken);
}
