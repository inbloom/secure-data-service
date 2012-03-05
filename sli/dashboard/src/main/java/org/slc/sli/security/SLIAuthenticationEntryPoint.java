package org.slc.sli.security;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import org.slc.sli.client.RESTClient;

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

    private String callbackUrl = "http://local.slidev.org:8000/dashboard/callback";
    
    private String clientId = "fm67sH6vZZ";
    
    private String clientSecret = "sb70uDUEYK1IkE5LB2xdBkTJRIQNhBnaOYu1ig5EZW3UwpP4";
    
    private static final String OAUTH_TOKEN = "OAuthToken";

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
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//
//        Object sessionId = request.getSession().getAttribute(SESSION_ID_KEY);
//
//        // Check if incoming request has a sessionId
//        if (sessionId == null) {
//            Cookie openAmCookie = WebUtils.getCookie(request, OPENAM_COOKIE_NAME);
//
//            // Check if cookie from idp exists
//            if (openAmCookie != null) {
//                sessionId = openAmCookie.getValue();
//
//            } else {
//                JsonObject jsonSession = this.restClient.sessionCheck(null);
//
//                //Redirect to idp, if user is not authenticated
//                if (!jsonSession.get("authenticated").getAsBoolean()) {
//                    String baseUrl = jsonSession.get("redirect_user").getAsString();
//                    String requestedURL = URLHelper.getUrl(request);
//                    LOG.debug("Using return URL of: " + requestedURL);
//                    URLBuilder url = new URLBuilder(baseUrl);
//                    url.addQueryParam("RelayState", requestedURL);
//                    response.sendRedirect(url.toString());
//                }
//            }
//        }
//
//        addAuthentication((String) sessionId);
//        response.sendRedirect(request.getRequestURI());
//    }

    private void addAuthentication(String token) {
        JsonObject json = this.restClient.sessionCheck(token);
        LOG.debug(json.toString());

        // If the user is authenticated, create an SLI principal, and authenticate
        if (json.get("authenticated").getAsBoolean()) {
//            SLIPrincipal principal = new SLIPrincipal();
//            principal.setName("Linda Kim");
//            principal.setId(token);
//            LinkedList<GrantedAuthority> authList = new LinkedList<GrantedAuthority>();
//            authList.add(new GrantedAuthorityImpl("Educator"));
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

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        System.out.println("TEMP -------- NEW COMMENCE, REQUEST URL " + request.getRequestURL());
        System.out.println("TEMP ---------- QUERY STRING " + request.getQueryString());
        OAuthService service = new ServiceBuilder().provider(SliApi.class).
                apiKey(clientId).apiSecret(clientSecret).callback(callbackUrl).
                build();

        HttpSession session = request.getSession();
        Object token = session.getAttribute(OAUTH_TOKEN);
        if (session.getAttribute(OAUTH_TOKEN) == null && request.getParameter("code") != null) {
            System.out.println("TEMP - Code is " + request.getParameter("code"));
            Verifier verifier = new Verifier(request.getParameter("code"));
            Token accessToken = service.getAccessToken(null, verifier);
            System.out.println("TEMP - The access token is " + accessToken);
            session.setAttribute(OAUTH_TOKEN, accessToken.getToken());
            System.out.println("Redirecting to " + session.getAttribute("ENTRY_URL"));
            response.sendRedirect(session.getAttribute("ENTRY_URL").toString());
        } else if (session.getAttribute(OAUTH_TOKEN) == null) {
            session.setAttribute("ENTRY_URL", request.getRequestURL());
            
            System.out.println("TEMP - Client ID " + clientId);
            System.out.println("TEMP - Client Secret " + clientSecret);
            System.out.println("TEMP - Callback URL " + callbackUrl);
            
            //The request token doesn't matter for OAuth 2.0 which is why it's null
            String authUrl = service.getAuthorizationUrl(null);
            System.out.println("TEMP - Our Authorization URL Is " + authUrl);
            response.sendRedirect(authUrl);
        } else {
            System.out.println("There's an Oauth token " + token);
//            JsonObject jsonSession = this.restClient.sessionCheck(null);
        }
        addAuthentication((String) token);
        response.sendRedirect(request.getRequestURI());

        
    }
}

