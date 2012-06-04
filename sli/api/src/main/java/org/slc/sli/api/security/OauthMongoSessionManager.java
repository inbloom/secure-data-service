package org.slc.sli.api.security;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.exceptions.OAuthException;
import org.slc.sli.api.security.oauth.ApplicationAuthorizationValidator;
import org.slc.sli.api.security.oauth.OAuthAccessException;
import org.slc.sli.api.security.oauth.OAuthAccessException.OAuthError;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
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
 *
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
    public void createAppSession(String sessionId, String clientId, String redirectUri, String state, String tenantId, String samlId) {
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("client_id", "=", clientId));
        Entity app = repo.findOne(APPLICATION_COLLECTION, nq);
        
        if (app == null) {
            RuntimeException x = new InvalidClientException(String.format("No app with id %s registered", clientId));
            error(x.getMessage(), x);
            throw x;
        }
        Boolean isInstalled = (Boolean)app.getBody().get("installed");
        
        if (!isInstalled && redirectUri != null && !redirectUri.startsWith((String) app.getBody().get("redirect_uri"))) {
            RuntimeException x = new RedirectMismatchException("Invalid redirect_uri specified " + redirectUri);
            error(x.getMessage() + " expected " + app.getBody().get("redirect_uri"), x);
            throw x;
        }

        Entity sessionEntity = sessionId == null ? null : repo.findById(SESSION_COLLECTION, sessionId);

        if (sessionEntity == null) {
            sessionEntity = repo.create(SESSION_COLLECTION, new HashMap<String, Object>());
            sessionEntity.getBody().put("expiration", System.currentTimeMillis() + this.sessionLength);
            sessionEntity.getBody().put("hardLogout", System.currentTimeMillis() + this.hardLogout);
            sessionEntity.getBody().put("tenantId", tenantId);
            sessionEntity.getBody().put("appSession", new ArrayList<Map<String, Object>>());
        }
        
        List<Map<String, Object>> appSessions = (List<Map<String, Object>>) sessionEntity.getBody().get("appSession");
        appSessions.add(newAppSession(clientId, redirectUri, state, samlId, isInstalled));

        repo.update(SESSION_COLLECTION, sessionEntity);
    }

    @Override
    public Entity getSessionForSamlId(String samlId){
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
    public Map<String, Object> getAppSession(String samlId, Entity session){
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
    public void updateSession(Entity session){
        repo.update(SESSION_COLLECTION, session);
    }

    /**
     * Verifies and makes active an app session. Provides the token for the app.
     * @throws OAuthAccessException
     * @throws OAuthException
     */
    @Override
    @SuppressWarnings("unchecked")
    public String verify(String code, Pair<String, String> clientCredentials) throws OAuthAccessException {
        NeutralQuery nq = new NeutralQuery();
        nq.addCriteria(new NeutralCriteria("appSession.clientId", "=", clientCredentials.getLeft()));
        nq.addCriteria(new NeutralCriteria("appSession.verified", "=", "false"));
        nq.addCriteria(new NeutralCriteria("appSession.code.value", "=", code));
        nq.addCriteria(new NeutralCriteria("appSession.code.expiration", ">", System.currentTimeMillis()));

        Entity session = repo.findOne(SESSION_COLLECTION, nq);

        if (session == null) {
            RuntimeException x = new IllegalArgumentException(String.format("No session with code/client %s/%s", code, clientCredentials.getLeft()));
            error("Attempted to access invalid session", x);
            throw x;
        }

        // Locate the application and compare the client secret
        nq = new NeutralQuery();
        nq.addCriteria(new NeutralCriteria("client_id", "=", clientCredentials.getLeft()));
        nq.addCriteria(new NeutralCriteria("client_secret", "=", clientCredentials.getRight()));

        Entity app = repo.findOne(APPLICATION_COLLECTION, nq);

        if (app == null) {
            OAuthAccessException x = new OAuthAccessException(OAuthError.UNAUTHORIZED_CLIENT, "No application matching credentials found.");
            error("App credentials mismatch", x);
            throw x;
        }

        //Make sure the user's district has authorized the use of this application
        SLIPrincipal principal = jsoner.convertValue(session.getBody().get("principal"), SLIPrincipal.class);
        principal.setEntity(locator.locate((String) principal.getTenantId(), principal.getExternalId()).getEntity());
        List<String> authorizedAppIds = appValidator.getAuthorizedApps(principal);

        //If the list of authorized apps is null, we weren't able to figure out the user's LEA.
        //TODO: deny access if no context information is available--to fix in oauth hardening
        if (authorizedAppIds != null && !authorizedAppIds.contains(app.getEntityId())) {
            throw new OAuthAccessException(OAuthError.UNAUTHORIZED_CLIENT,
                    "User " + principal.getExternalId() + " is not authorized to use " + app.getBody().get("name"),
                    (String) session.getBody().get("state"));
        }

        List<Map<String, Object>> appSessions = (List<Map<String, Object>>) session.getBody().get("appSession");

        String token = "";
        for (Map<String, Object> appSession : appSessions) {
            Map<String, Object> codeBlock = (Map<String, Object>) appSession.get("code");

            if (codeBlock.get("value").equals(code)) {
                token = (String) appSession.get("token");
                appSession.put("verified", "true");
                repo.update(SESSION_COLLECTION, session);
                break;
            }
        }

        return token;
    }

    /**
     * Loads session referenced by the headers
     *
     * @param headers
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public OAuth2Authentication getAuthentication(String authz) {
        OAuth2Authentication auth = createAnonymousAuth();

        if (authz != null && !authz.equals("")) {
            try {
                Matcher user = USER_AUTH.matcher(authz);
                if (user.find()) {
                    String accessToken = user.group(1);

                    Entity sessionEntity = findEntityForAccessToken(accessToken);
                    if (sessionEntity != null) {
                        List<Map<String, Object>> sessions = (List<Map<String, Object>>) sessionEntity.getBody().get("appSession");
                        for (Map<String, Object> session : sessions) {
                            if (session.get("token").equals(accessToken)) {

                                ClientToken token = new ClientToken((String) session.get("clientId"), null /* secret not needed */, null /* Scope is unused */);
                                // Spring doesn't provide a setter for the approved field (used by isAuthorized), so we set it the hard way
                                Field approved = ClientToken.class.getDeclaredField("approved");
                                approved.setAccessible(true);
                                approved.set(token, true);

                                SLIPrincipal principal = jsoner.convertValue(sessionEntity.getBody().get("principal"), SLIPrincipal.class);
                                principal.setEntity(locator.locate((String) principal.getTenantId(), principal.getExternalId()).getEntity());
                                Collection<GrantedAuthority> authorities = resolveAuthorities(principal.getRealm(), principal.getRoles());
                                PreAuthenticatedAuthenticationToken userToken = new PreAuthenticatedAuthenticationToken(principal, accessToken, authorities);
                                userToken.setAuthenticated(true);
                                auth = new OAuth2Authentication(token, userToken);

                                // Extend the session
                                long expire = (Long) sessionEntity.getBody().get("expiration");
                                sessionEntity.getBody().put("expiration", expire + this.sessionLength);
                                repo.update(SESSION_COLLECTION, sessionEntity);

                                break;
                            }
                        }
                    }
                } else {
                    info("User is anonymous");
                }
            } catch (Exception e) {
                error("Error processing authentication.  Anonymous context will be returned.", e);
            }
        }
        return auth;
    }

    /**
     * Determines if the specified mongo id maps to a valid OAuth access token.
     *
     * @param mongoId id of the oauth session in mongo.
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

    private Collection<GrantedAuthority> resolveAuthorities(final String realm, final List<String> roleNames) {
        Collection<GrantedAuthority> userAuthorities = SecurityUtil.sudoRun(new SecurityTask<Collection<GrantedAuthority>>() {
            @Override
            public Collection<GrantedAuthority> execute() {
                return resolver.resolveRoles(realm, roleNames);
            }
        });
        return userAuthorities;
    }

    private OAuth2Authentication createAnonymousAuth() {
        String time = Long.toString(System.currentTimeMillis());
        SLIPrincipal anon = new SLIPrincipal(time);
        anon.setEntity(new MongoEntity("user", "-133", new HashMap<String, Object>(), new HashMap<String, Object>()));
        return new OAuth2Authentication(new ClientToken("UNKNOWN", "UNKNOWN", new HashSet<String>()), new AnonymousAuthenticationToken(time, anon , Arrays.<GrantedAuthority>asList(Right.ANONYMOUS_ACCESS)));
    }

    private Entity findEntityForAccessToken(String token) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("appSession.token", "=", token));
        neutralQuery.addCriteria(new NeutralCriteria("expiration", ">", System.currentTimeMillis()));
        neutralQuery.addCriteria(new NeutralCriteria("hardLogout", ">", System.currentTimeMillis()));
        return repo.findOne(SESSION_COLLECTION, neutralQuery);
    }

    private Map<String, Object> newAppSession(String clientId, String redirectUri, String state, String samlId, Boolean isInstalled) {
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

}
