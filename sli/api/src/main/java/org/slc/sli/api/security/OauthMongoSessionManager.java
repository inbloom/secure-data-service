package org.slc.sli.api.security;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
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
    private static final String SESSION_COLLECTION = "userSession";

    @Value("${sli.session.length}")
    private int sessionLength;

    @Value("${sli.session.hardLogout}")
    private int hardLogout;

    @Autowired
    private Repository<Entity> repo;

    /**
     * Creates a new app session
     * Creates user session if needed
     */
    @Override
    @SuppressWarnings("unchecked")
    public void createAppSession(String sessionId, String clientId, String redirectUri, String state, String samlId) {
        // TODO check for error conditions
        // clientId exists (app)
        // redirectUri matches (app)

        Entity sessionEntity = sessionId == null ? null : repo.findById(SESSION_COLLECTION, sessionId);

        if (sessionEntity == null) {
            sessionEntity = repo.create(SESSION_COLLECTION, new HashMap<String, Object>());
            sessionEntity.getBody().put("expiration", System.currentTimeMillis() + this.sessionLength);
            sessionEntity.getBody().put("hardLogout", System.currentTimeMillis() + this.hardLogout);
            sessionEntity.getBody().put("appSession", new ArrayList<Map<String, Object>>());
        }

        List<Map<String, Object>> appSessions = (List<Map<String, Object>>) sessionEntity.getBody().get("appSession");
        appSessions.add(newAppSession(clientId, redirectUri, state, samlId));

        repo.update(SESSION_COLLECTION, sessionEntity);
    }

    /**
     * Provides the URI to which the user should be redirected
     * Upon receipt of successful SAML message
     */
    @Override
    @SuppressWarnings("unchecked")
    public URI composeRedirect(String samlId) {
        NeutralQuery nq = new NeutralQuery();
        nq.addCriteria(new NeutralCriteria("appSession.samlId", "=", samlId));

        Entity session = repo.findOne(SESSION_COLLECTION, nq);

        if (session == null) {
            RuntimeException x = new IllegalStateException(String.format("No session with samlId", samlId));
            error("Attempted to access invalid session", x);
            throw x;
        }

        List<Map<String, Object>> appSessions = (List<Map<String, Object>>) session.getBody().get("appSession");

        URI redirect = null;
        for (Map<String, Object> appSession : appSessions) {
            if (appSession.get("samlId").equals(samlId)) {
                UriBuilder builder = UriBuilder.fromUri((String) appSession.get("redirectUri"));
                Map<String, Object> code = (Map<String, Object>) appSession.get("code");
                builder.queryParam("code", (String) code.get("value"));

                if (appSession.get("state") != null) {
                    builder.queryParam("state", appSession.get("state"));
                }

                redirect = builder.build();
                break;
            }
        }

        return redirect;
    }

    /**
     * Verifies and makes active an app session. Provides the token for the app.
     */
    @Override
    @SuppressWarnings("unchecked")
    public String verify(String code, Pair<String, String> clientCredentials) {
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

        Entity app = repo.findOne("application", nq);

        if (app == null) {
            RuntimeException x = new BadCredentialsException("No application matching credentials found.");
            error("App credentials mismatch", x);
            throw x;
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
                    List<Map<String, Object>> sessions = (List<Map<String, Object>>) sessionEntity.getBody().get("appSession");
                    for (Map<String, Object> session : sessions) {
                        if (session.get("token").equals(accessToken)) {
                            ClientToken token = new ClientToken((String) session.get("clientId"), null /* secret not needed */, null /* Scope is unused */);
                            PreAuthenticatedAuthenticationToken userToken = new PreAuthenticatedAuthenticationToken(new SLIPrincipal() /* TODO wire in principal */, 
                                    null /*Credentials*/, 
                                    new ArrayList<GrantedAuthority>() /*TODO wire in authorities*/);
                            userToken.setAuthenticated(true);
                            auth = new OAuth2Authentication(token, userToken);
                            break;
                        }
                    }
                } else {
                    info("User is anonymous");
                }
            } catch (Exception e) {
                warn("Error processing authentication.  Anonymous context will be returned...\n {}", e);
            }
        }
        return auth;
    }

    private OAuth2Authentication createAnonymousAuth() {
        String time = Long.toString(System.currentTimeMillis());
        return new OAuth2Authentication(new ClientToken("UNKNOWN", "UNKNOWN", new HashSet<String>()), new AnonymousAuthenticationToken(time, time, Arrays.<GrantedAuthority>asList(Right.ANONYMOUS_ACCESS)));
    }

    private Entity findEntityForAccessToken(String token) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("appSession.token", "=", token));
        return repo.findOne(SESSION_COLLECTION, neutralQuery);
    }

    private Map<String, Object> newAppSession(String clientId, String redirectUri, String state, String samlId) {
        Map<String, Object> app = new HashMap<String, Object>();
        app.put("clientId", clientId);
        app.put("redirectUri", redirectUri);
        app.put("state", state);
        app.put("samlId", samlId);
        app.put("verified", "false");

        Map<String, Object> code = new HashMap<String, Object>();
        code.put("value", "c-" + UUID.randomUUID().toString());
        code.put("expiration", System.currentTimeMillis() + this.sessionLength);

        app.put("code", code);
        app.put("token", "t-" + UUID.randomUUID().toString());
        return app;
    }

}
