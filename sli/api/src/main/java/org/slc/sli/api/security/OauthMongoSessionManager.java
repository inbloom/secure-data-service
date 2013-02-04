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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.oauth.ApplicationAuthorizationValidator;
import org.slc.sli.api.security.oauth.OAuthAccessException;
import org.slc.sli.api.security.oauth.OAuthAccessException.OAuthError;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Manages SLI User/app sessions
 * Provides functionality to update existing session based on Oauth life-cycle stages
 *
 * @author dkornishev
 */
@Component
public class OauthMongoSessionManager implements OauthSessionManager {

    private static final Pattern USER_AUTH = Pattern.compile("Bearer (.+)", Pattern.CASE_INSENSITIVE);

    private static final String APPLICATION_COLLECTION = "application";
    private static final String SESSION_COLLECTION = "userSession";

    @Value("${sli.session.length}")
    private int sessionLength;

    @Value("${sli.session.hardLogout}")
    private int hardLogout;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private RolesToRightsResolver resolver;

    @Autowired
    private UserLocator locator;

    private ObjectMapper jsoner = new ObjectMapper();

    @Autowired
    private ApplicationAuthorizationValidator appValidator;

    /**
     * Creates a new app session
     * Creates user session if needed
     */
    @Override
    @SuppressWarnings("unchecked")
    public void createAppSession(String sessionId, String clientId, String redirectUri, String state, String tenantId,
            String realmId, String samlId, boolean sessionExpired) {
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("client_id", "=", clientId));
        Entity app = repo.findOne(APPLICATION_COLLECTION, nq);

        if (app == null) {
            RuntimeException x = new InvalidClientException(String.format("No app with id %s registered", clientId));
            error(x.getMessage(), x);
            throw x;
        }
        Boolean isInstalled = (Boolean) app.getBody().get("installed");

        String appRedirectUri = (String) app.getBody().get("redirect_uri");
        if (!isInstalled && (appRedirectUri == null || appRedirectUri.trim().length() == 0)) {
        	RuntimeException x = new RedirectMismatchException("No redirect_uri specified on non-installed app");
        	error(x.getMessage());
        	throw x;
        }
        if (!isInstalled && redirectUri != null && !redirectUri.startsWith((String) app.getBody().get("redirect_uri"))) {
            RuntimeException x = new RedirectMismatchException("Invalid redirect_uri specified " + redirectUri);
            error(x.getMessage() + " expected " + app.getBody().get("redirect_uri"), x);
            throw x;
        }

        Entity sessionEntity = sessionId == null ? null : repo.findById(SESSION_COLLECTION, sessionId);

        if (sessionEntity == null || sessionExpired) {
            sessionEntity = repo.create(SESSION_COLLECTION, new HashMap<String, Object>());
            sessionEntity.getBody().put("expiration", System.currentTimeMillis() + this.sessionLength);
            sessionEntity.getBody().put("hardLogout", System.currentTimeMillis() + this.hardLogout);
            sessionEntity.getBody().put("requestedRealmId", realmId);
            sessionEntity.getBody().put("appSession", new ArrayList<Map<String, Object>>());
        }

        List<Map<String, Object>> appSessions = (List<Map<String, Object>>) sessionEntity.getBody().get("appSession");
        appSessions.add(newAppSession(clientId, redirectUri, state, samlId, isInstalled));

        repo.update(SESSION_COLLECTION, sessionEntity);
    }

    @Override
    public Entity getSessionForSamlId(String samlId) {
        NeutralQuery nq = new NeutralQuery();
        nq.addCriteria(new NeutralCriteria("appSession.samlId", "=", samlId));

        Entity session = repo.findOne(SESSION_COLLECTION, nq);

        if (session == null) {
            RuntimeException x = new IllegalStateException(String.format("No session with samlId %s", samlId));
            error("Attempted to access invalid session", x);
            throw x;
        }
        return session;
    }

    @Override
    public Map<String, Object> getAppSession(String samlId, Entity session) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> appSessions = (List<Map<String, Object>>) session.getBody().get("appSession");

        for (Map<String, Object> appSession : appSessions) {
            if (appSession.get("samlId").equals(samlId)) {
                return appSession;
            }
        }
        RuntimeException x = new IllegalStateException(String.format("No session with samlId %s", samlId));
        error("Attempted to access invalid session", x);
        throw x;
    }

    @Override
    public void updateSession(Entity session) {
        repo.update(SESSION_COLLECTION, session);
    }

    /**
     * Verifies and makes active an app session. Provides the token for the app.
     *
     * @throws OAuthAccessException
     * @throws OAuthException
     */
    @Override
    @SuppressWarnings("unchecked")
    public String verify(String code, Pair<String, String> clientCredentials) throws OAuthAccessException {
        NeutralQuery nq = new NeutralQuery();
        nq.addCriteria(new NeutralCriteria("appSession.code.value", "=", code));

        Entity session = repo.findOne(SESSION_COLLECTION, nq);

        if (session == null) {
            error("Session with code %s does not exist.", code);
            throw new OAuthAccessException(OAuthError.INVALID_GRANT, String.format(
                    "Session with code %s does not exist.", code));
        }

        // Find the nested app session data with the given code
        List<Map<String, Object>> appSessions = (List<Map<String, Object>>) session.getBody().get("appSession");
        Map<String, Object> curAppSession = null;
        for (Map<String, Object> appSession : appSessions) {
            Map<String, Object> codeBlock = (Map<String, Object>) appSession.get("code");

            if (codeBlock.get("value").equals(code)) {
                curAppSession = appSession;
                break;
            }
        }

        if (curAppSession == null) {
            error("OAuth session not found with code %s.", code);
            throw new OAuthAccessException(OAuthError.INVALID_GRANT, String.format(
                    "OAuth session not found with code %s.", code));
        }

        // verify other attributes of the appSession
        String clientId = (String) curAppSession.get("clientId");
        if (!clientCredentials.getLeft().equals(clientId)) {
            error("Client %s is invalid for app session %s.", clientCredentials.getLeft(), code);
            throw new OAuthAccessException(OAuthError.INVALID_CLIENT, String.format(
                    "Client %s is invalid for app session %s.", clientCredentials.getLeft(), code));
        }

        String verified = (String) curAppSession.get("verified");
        if (Boolean.valueOf(verified)) {
            error("App session %s has already been verified.", code);
            throw new OAuthAccessException(OAuthError.INVALID_GRANT, String.format(
                    "App session %s has already been verified.", code));
        }

        Long expiration = (Long) ((Map<String, Object>) curAppSession.get("code")).get("expiration");
        if (expiration < System.currentTimeMillis()) {
            error("App session %s has expired.", code);
            throw new OAuthAccessException(OAuthError.INVALID_GRANT, String.format("App session %s has expired.", code));
        }

        // Locate the application and compare the client secret
        nq = new NeutralQuery();
        nq.addCriteria(new NeutralCriteria("client_id", "=", clientCredentials.getLeft()));
        nq.addCriteria(new NeutralCriteria("client_secret", "=", clientCredentials.getRight()));

        Entity app = repo.findOne(APPLICATION_COLLECTION, nq);

        if (app == null) {
            OAuthAccessException x = new OAuthAccessException(OAuthError.UNAUTHORIZED_CLIENT,
                    "No application matching credentials found.");
            error("App credentials mismatch", x);
            throw x;
        }

        // Make sure the user's district has authorized the use of this application
        SLIPrincipal principal = jsoner.convertValue(session.getBody().get("principal"), SLIPrincipal.class);
        if (principal.getUserType() == null) {
            principal.setUserType(EntityNames.STAFF);
        }
        principal.setEntity(locator.locate(principal.getTenantId(), principal.getExternalId(), principal.getUserType()).getEntity());
        TenantContext.setTenantId(principal.getTenantId());

        List<String> authorizedAppIds = appValidator.getAuthorizedApps(principal);

        if (!authorizedAppIds.contains(app.getEntityId())) {
            String message = "User " + principal.getExternalId() + " is not authorized to use "
                    + app.getBody().get("name");
            error(message);
            throw new OAuthAccessException(OAuthError.UNAUTHORIZED_CLIENT, message, (String) session.getBody().get(
                    "state"));
        }

        String token = "";
        token = (String) curAppSession.get("token");
        curAppSession.put("verified", "true");
        repo.update(SESSION_COLLECTION, session);
        return token;
    }

    /**
     * Loads session referenced by the headers
     *
     * If there's a problem with the token, we embed a relevant {@link OAuth2Exception} in the auth's details field
     *
     * Per Oauth spec:
     *
     * If the protected resource request included an access token and failed
     * authentication, the resource server SHOULD include the "error"
     * attribute to provide the client with the reason why the access
     * request was declined.
     *
     * In addition, the resource server MAY include the
     * "error_description" attribute to provide developers a human-readable
     * explanation that is not meant to be displayed to end-users.
     *
     * If the request lacks any authentication information (e.g., the client
     * was unaware that authentication is necessary or attempted using an
     * unsupported authentication method), the resource server SHOULD NOT
     * include an error code or other error information.
     *
     * @param headers
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public OAuth2Authentication getAuthentication(String authz) {
        OAuth2Authentication auth = createAnonymousAuth();
        String accessToken = getTokenFromAuthHeader(authz);
        if (accessToken != null) {
            Entity sessionEntity = findEntityForAccessToken(accessToken);
            if (sessionEntity != null) {
                List<Map<String, Object>> sessions = (List<Map<String, Object>>) sessionEntity.getBody().get(
                        "appSession");
                for (Map<String, Object> session : sessions) {
                    if (session.get("token").equals(accessToken)) {

                        // Log that the long lived session is being used
                        Date createdOn;
                        if (sessionEntity.getMetaData().get("created").getClass() == String.class) {
                            String date = (String) sessionEntity.getMetaData().get("created");

                            if (date.contains("T")) {
                                date = date.substring(0, date.indexOf("T"));
                            }
                            createdOn = DateTimeUtil.parseDateTime(date).toDate();

                        } else {
                            createdOn = (Date) sessionEntity.getMetaData().get("created");
                        }
                        Long hl = (Long) sessionEntity.getBody().get("hardLogout");

                        if (isLongLived(hl - createdOn.getTime())) {
                        	String displayToken = accessToken.substring(0, 6) + "......" + accessToken.substring(accessToken.length()-4, accessToken.length());
                            info("Using long-lived session {} belonging to app {}", displayToken,
                                    session.get("clientId"));
                        }
                        // ****

                        ClientToken token = new ClientToken((String) session.get("clientId"), null, null);

                        try {
                            // Spring doesn't provide a setter for the approved field (used by
                            // isAuthorized), so we set it the hard way
                            Field approved = ClientToken.class.getDeclaredField("approved");
                            approved.setAccessible(true);
                            approved.set(token, true);
                        } catch (Exception e) {
                            error("Error processing authentication.  Anonymous context will be returned.", e);
                        }

                        SLIPrincipal principal = jsoner.convertValue(sessionEntity.getBody().get("principal"),
                                SLIPrincipal.class);
                        TenantContext.setTenantId(principal.getTenantId());
                        // add logic here that checks principal.getUserType()
                        // -> if nil, set to staff
                        principal.setEntity(locator.locate(principal.getTenantId(), principal.getExternalId(),principal.getUserType())
                                .getEntity());
                        principal.setSessionId(sessionEntity.getEntityId());
                        Collection<GrantedAuthority> selfAuthorities = resolveAuthorities(principal.getTenantId(),
                                principal.getRealm(), principal.getRoles(), principal.isAdminRealmAuthenticated(), true);
                        principal.setSelfRights(selfAuthorities);
                        debug("Granted self rights - {}", selfAuthorities);
                        
                        Collection<GrantedAuthority> authorities = resolveAuthorities(principal.getTenantId(),
                                principal.getRealm(), principal.getRoles(), principal.isAdminRealmAuthenticated(), false);
                        debug("Granted regular rights - {}", authorities);
                        if (!principal.isAdminRealmAuthenticated()) {
                            principal.setAuthorizingEdOrgs(appValidator.getAuthorizingEdOrgsForApp(token.getClientId()));
                        }
                        PreAuthenticatedAuthenticationToken userToken = new PreAuthenticatedAuthenticationToken(
                                principal, accessToken, authorities);
                        userToken.setAuthenticated(true);
                        auth = new OAuth2Authentication(token, userToken);

                        // Extend the session
                        long previousExpire = (Long) sessionEntity.getBody().get("expiration");
                        // only update the expire time if it is within the next 5 minutes
                        // this explicitly does not update the expire time for long-lived
                        // session tokens
                        // they will last until their end, plus a 5 minutes session buffer
                        if (previousExpire < (System.currentTimeMillis() + 300000)) {
                            sessionEntity.getBody().put("expiration",
                                    System.currentTimeMillis() + this.sessionLength);
                            repo.update(SESSION_COLLECTION, sessionEntity);
                        }
                        // Purge expired sessions
                        purgeExpiredSessions();

                        break;
                    }
                }
            } else {
                //details is a convenient place to store an error message
                auth.setDetails(new OAuthAccessException(OAuthError.INVALID_TOKEN, "The access token does not exist or is expired."));
            }
        } else {
            //details is a convenient place to store an error message
            if (authz == null) {
                auth.setDetails(null);  //oauth spec says not to give detailed error information if token isn't included
            } else {
                auth.setDetails(new OAuthAccessException(OAuthError.INVALID_TOKEN, "Authorization header must be of the form 'Bearer <token>'"));
            }
        }
        return auth;
    }

    private String getTokenFromAuthHeader(String authz) {
        if (authz == null || authz.isEmpty()) {
            return null;
        }

        Matcher user = USER_AUTH.matcher(authz);
        if (user.find()) {
            String accessToken = user.group(1);
            return accessToken;
        } else {
            return null;
        }
    }

    /**
     * Determines if the specified mongo id maps to a valid OAuth access token.
     *
     * @param mongoId
     *            id of the oauth session in mongo.
     * @return id of realm (valid session) or null (not a valid session).
     */
    @Override
    public Entity getSession(String sessionId) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("_id", "=", sessionId));

        Entity session = repo.findOne(SESSION_COLLECTION, neutralQuery);

        return session;
    }

    @Override
    public boolean logout(String accessToken) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("appSession.token", "=", accessToken));

        Entity session = repo.findOne(SESSION_COLLECTION, neutralQuery);

        boolean deleted = false;
        if (session != null) {
            deleted = repo.delete(SESSION_COLLECTION, session.getEntityId());
        }

        return deleted;
    }

    private Collection<GrantedAuthority> resolveAuthorities(String tenantId, final String realm,
            final List<String> roleNames, boolean isAdmin, boolean isSelf) {
        return resolver.resolveRoles(tenantId, realm, roleNames, isAdmin, isSelf);
    }

    private OAuth2Authentication createAnonymousAuth() {
        String time = Long.toString(System.currentTimeMillis());
        SLIPrincipal anon = new SLIPrincipal(time);
        anon.setEntity(new MongoEntity("user", "-133", new HashMap<String, Object>(), new HashMap<String, Object>()));
        return new OAuth2Authentication(new ClientToken("UNKNOWN", "UNKNOWN", new HashSet<String>()),
                new AnonymousAuthenticationToken(time, anon, Arrays.<GrantedAuthority> asList(Right.ANONYMOUS_ACCESS)));
    }

    private Entity findEntityForAccessToken(String token) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("appSession.token", "=", token));
        neutralQuery.addCriteria(new NeutralCriteria("expiration", ">", System.currentTimeMillis()));
        neutralQuery.addCriteria(new NeutralCriteria("hardLogout", ">", System.currentTimeMillis()));
        return repo.findOne(SESSION_COLLECTION, neutralQuery);
    }

    private Map<String, Object> newAppSession(String clientId, String redirectUri, String state, String samlId,
            Boolean isInstalled) {
        Map<String, Object> app = new HashMap<String, Object>();
        app.put("clientId", clientId);
        app.put("redirectUri", redirectUri);
        app.put("state", state);
        app.put("samlId", samlId);
        app.put("verified", "false");
        app.put("installed", isInstalled);
        Map<String, Object> code = new HashMap<String, Object>();
        code.put("value", "c-" + UUID.randomUUID().toString());
        code.put("expiration", System.currentTimeMillis() + this.sessionLength);

        app.put("code", code);
        app.put("token", "t-" + UUID.randomUUID().toString());
        return app;
    }

    /**
     * Purges all expired oauth access tokens from the user session collection.
     */
    public boolean purgeExpiredSessions() {
        boolean success = true;

        NeutralQuery hardLogoutQuery = new NeutralQuery(0);
        hardLogoutQuery.addCriteria(new NeutralCriteria("hardLogout", NeutralCriteria.CRITERIA_LT, System
                .currentTimeMillis()));

        NeutralQuery query = new NeutralQuery();
        NeutralQuery expireQuery = new NeutralQuery(new NeutralCriteria("expiration", NeutralCriteria.CRITERIA_LT,
                System.currentTimeMillis()));
        query.addOrQuery(hardLogoutQuery);
        query.addOrQuery(expireQuery);

        for (Entity entity : repo.findAll(SESSION_COLLECTION, query)) {
            if (!repo.delete(SESSION_COLLECTION, entity.getEntityId())) {
                error("Failed to delete entity with id: {}", new Object[] { entity.getEntityId() });
                success = false;
            }
        }
        return success;
    }

    /**
     * Sets the entity repository.
     *
     * @param repository
     *            New Entity Repository to be used.
     */
    public void setEntityRepository(Repository<Entity> repository) {
        this.repo = repository;
    }

    /**
     * Compares the provided number of milliseconds converted to minutes
     * against the configuration property
     *
     * @param actual
     * @return
     */
    private boolean isLongLived(long actual) {
        long minutes = actual / 60000;
        long configMinutes = this.hardLogout / 60000;

        return minutes > configMinutes;
    }
}
