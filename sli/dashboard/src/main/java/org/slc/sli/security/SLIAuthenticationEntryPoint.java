package org.slc.sli.security;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slc.sli.client.RESTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Spring interceptor for calls that don't have a session
 * This implementation simply redirects to the login URL
 * 
 * @author dkornishev
 * 
 */
@Component
public class SLIAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private static final Logger LOG = LoggerFactory.getLogger(SLIAuthenticationEntryPoint.class);
    
    @Value("${oauth.redirect}")
    private String callbackUrl;
    
    @Value("${oauth.client.id}")
    private String clientId;
    
    @Value("${oauth.client.secret}")
    private String clientSecret;
    
    @Value("${api.server.url}")
    private String apiUrl;
    
    private static final String OAUTH_TOKEN = "OAUTH_TOKEN";
    private static final String ENTRY_URL = "ENTRY_URL";
    
    private RESTClient restClient;
    
    public RESTClient getRestClient() {
        return restClient;
    }
    
    public void setRestClient(RESTClient restClient) {
        this.restClient = restClient;
    }
    
    private void addAuthentication(String token) {
        JsonObject json = restClient.sessionCheck(token);
        LOG.debug(json.toString());
        
        // If the user is authenticated, create an SLI principal, and authenticate
        if (json.get("authenticated").getAsBoolean()) {
            SLIPrincipal principal = new SLIPrincipal();
            JsonElement nameElement = json.get("full_name");
            String username = nameElement.getAsString();
            if (username != null && username.contains("@")) {
                username = username.substring(0, username.indexOf("@"));
                if (username.contains(".")) {
                    String first = username.substring(0, username.indexOf('.'));
                    String second = username.substring(username.indexOf('.') + 1);
                    username = first.substring(0, 1).toUpperCase() + (first.length() > 1 ? first.substring(1) : "")
                            + (second.substring(0, 1).toUpperCase() + (second.length() > 1 ? second.substring(1) : ""));
                }
            }
            principal.setName(username);
            principal.setId(token);
            JsonArray grantedAuthorities = json.getAsJsonArray("sliRoles");
            Iterator<JsonElement> authIterator = grantedAuthorities.iterator();
            LinkedList<GrantedAuthority> authList = new LinkedList<GrantedAuthority>();
            
            // Add authorities to user principal
            while (authIterator.hasNext()) {
                JsonElement nextElement = authIterator.next();
                authList.add(new GrantedAuthorityImpl(nextElement.getAsString()));
            }
            
            SecurityContextHolder.getContext().setAuthentication(
                    new PreAuthenticatedAuthenticationToken(principal, token, authList));
        }
    }
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        SliApi.setBaseUrl(apiUrl);
        LOG.debug("Client ID is {}, clientSecret is {}, callbackUrl is {}", new Object[] { clientId, clientSecret,
                callbackUrl });
        OAuthService service = new ServiceBuilder().provider(SliApi.class).apiKey(clientId).apiSecret(clientSecret)
                .callback(callbackUrl).build();
        
        HttpSession session = request.getSession();
        Object token = session.getAttribute(OAUTH_TOKEN);
        LOG.debug(
                "Oauth token in session - {} and access code - {} and request URL is {}",
                new Object[] { session.getAttribute(OAUTH_TOKEN), request.getParameter("code"), request.getRequestURL() });
        if (session.getAttribute(OAUTH_TOKEN) == null && request.getParameter("code") != null) {
            Verifier verifier = new Verifier(request.getParameter("code"));
            Token accessToken = null;
            try {
                accessToken = service.getAccessToken(null, verifier);
            } catch (OAuthException ex) {
                //This will happen if the request to getAccessToken results in an OAuth error, such as
                //if the user isn't authorized to use the client.
                
                //TODO: provide an improved error page
                response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
                return;
            } catch (Exception ex) {
                // This will happen if the request to getAccessToken results in an OAuth error, such
                // as
                // if the user isn't authorized to use the client.
                
                // TODO: provide an improved error page
                session.setAttribute(OAUTH_TOKEN, "");
                response.sendRedirect("/dashboard/exception");
                return;
            }
            session.setAttribute(OAUTH_TOKEN, accessToken.getToken());
            Object entryUrl = session.getAttribute(ENTRY_URL);
            if (entryUrl != null) {
                response.sendRedirect(session.getAttribute(ENTRY_URL).toString());
            } else {
                response.sendRedirect(request.getRequestURI());
            }
        } else if (session.getAttribute(OAUTH_TOKEN) == null) {
            session.setAttribute(ENTRY_URL, request.getRequestURL());
            
            // The request token doesn't matter for OAuth 2.0 which is why it's null
            String authUrl = service.getAuthorizationUrl(null);
            response.sendRedirect(authUrl);
        } else {
            LOG.debug("Using access token {}", token);
            addAuthentication((String) token);
            response.sendRedirect(request.getRequestURI());
        }
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public String getCallbackUrl() {
        return callbackUrl;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
}
