package org.slc.sli.security;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.util.WebUtils;

import org.slc.sli.util.URLBuilder;
import org.slc.sli.util.URLHelper;

/**
 * Spring interceptor for calls that don't have a session
 * This implementation simply redirects to the login URL
 * 
 * @author dkornishev
 *
 */
public class SLIAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private static final Logger LOG = LoggerFactory.getLogger(SLIAuthenticationEntryPoint.class);
    
    private static final String SESSION_ID_KEY = "sliSessionId";
    private static final String OPENAM_COOKIE_NAME = "iPlanetDirectoryPro";
    
    
    private RESTClient restClient;
    
    public RESTClient getRestClient() {
        return restClient;
    }

    public void setRestClient(RESTClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Redirects user to login URL
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        Object sessionId = request.getSession().getAttribute(SESSION_ID_KEY);
        
        // Check if incoming request has a sessionId
        if (sessionId == null) {
            Cookie openAmCookie = WebUtils.getCookie(request, OPENAM_COOKIE_NAME);
            
            // Check if cookie from idp exists
            if (openAmCookie != null) {
                sessionId = openAmCookie.getValue();

            } else {
                JsonObject jsonSession = this.restClient.sessionCheck(null);

                //Redirect to idp, if user is not authenticated
                if (!jsonSession.get("authenticated").getAsBoolean()) {
                    String baseUrl = jsonSession.get("redirect_user").getAsString();
                    String requestedURL = URLHelper.getUrl(request);
                    LOG.debug("Using return URL of: " + requestedURL);
                    URLBuilder url = new URLBuilder(baseUrl);
                    url.addQueryParam("RelayState", requestedURL);
                    response.sendRedirect(url.toString());
                }
            }
        }

        addAuthentication((String) sessionId);
        response.sendRedirect(request.getRequestURI());
    }
    
    private void addAuthentication(String token) {
        JsonObject json = this.restClient.sessionCheck(token);
        LOG.debug(json.toString());
        
        // If the user is authenticated, create an SLI principal, and authenticate
        if (json.get("authenticated").getAsBoolean()) {
            SLIPrincipal principal = new SLIPrincipal();
            JsonElement nameElement = json.get("full_name");
            principal.setName(nameElement.getAsString());
            principal.setId(token);
            JsonArray grantedAuthorities = json.getAsJsonArray("granted_authorities");
            Iterator<JsonElement> authIterator = grantedAuthorities.iterator();
            LinkedList<GrantedAuthority> authList = new LinkedList<GrantedAuthority>();
            
            // Add authorities to user principal
            while (authIterator.hasNext()) {
                JsonElement nextElement = authIterator.next();
                authList.add(new GrantedAuthorityImpl(nextElement.getAsString()));
            }
    
            SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(principal, token, authList));
        }
    }
    
}

