package org.slc.sli.security;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slc.sli.client.APIClient;
import org.slc.sli.client.RESTClient;
import org.slc.sli.util.Constants;
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

/**
 * Spring interceptor for calls that don't have a session
 * This implementation simply redirects to the login URL
 * 
 * @author dkornishev
 * @author rbloh
 * 
 */
@Component
public class SLIAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private static final Logger LOG = LoggerFactory.getLogger(SLIAuthenticationEntryPoint.class);
    
    public static final String OAUTH_TOKEN = "OAUTH_TOKEN";
    public static final String DASHBOARD_COOKIE = "SLI_DASHBOARD_COOKIE";
    
    private static final String OAUTH_CODE = "code";
    private static final String ENTRY_URL = "ENTRY_URL";
    private static final String STATE_PARAMETER = "state";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_AJAX_INDICATOR = "X-Requested-With";
    private static final String AJAX_REQUEST = "XmlHttpRequest";
    
    private static final String LOG_MESSAGE_AUTH_INITIATING = "Authentication: initiating {}";
    private static final String LOG_MESSAGE_AUTH_VERIFYING = "Authentication: verifying {}";
    private static final String LOG_MESSAGE_AUTH_COMPLETED = "Authentication: complete [{}] {}";
    private static final String LOG_MESSAGE_AUTH_REDIRECTING = "Authentication: redirecting [{}] {}";
    private static final String LOG_MESSAGE_AUTH_USING_COOKIE = "Authentication: using cookie {}";
    private static final String LOG_MESSAGE_AUTH_EXPIRING_COOKIE = "Authentication: expiring cookie {}";
    private static final String LOG_MESSAGE_AUTH_EXCEPTION = "Authentication Exception: {}";
    private static final String LOG_MESSAGE_AUTH_EXCEPTION_INVALID_AUTHENTICATED = "Authentication Exception: invalid authenticated indicator";
    private static final String LOG_MESSAGE_AUTH_EXCEPTION_INVALID_NAME = "Authentication Exception: invalid name indicator";
    private static final String LOG_MESSAGE_AUTH_EXCEPTION_INVALID_ROLES = "Authentication Exception: invalid roles indicator";
    
    @Value("${oauth.redirect}")
    private String callbackUrl;
    
    @Value("${api.server.url}")
    private String apiUrl;
    
    // TODO: Remove and use SDK APIClient for session checks
    private RESTClient restClient;
    
    private APIClient apiClient;
    
    private PropertiesDecryptor propDecryptor;
    
    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
    
    public String getCallbackUrl() {
        return callbackUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public RESTClient getRestClient() {
        return restClient;
    }
    
    public void setRestClient(RESTClient restClient) {
        this.restClient = restClient;
    }
    
    public APIClient getApiClient() {
        return apiClient;
    }
    
    public void setApiClient(APIClient apiClient) {
        this.apiClient = apiClient;
    }
    
    public PropertiesDecryptor getPropDecryptor() {
        return propDecryptor;
    }
    
    public void setPropDecryptor(PropertiesDecryptor propDecryptor) {
        this.propDecryptor = propDecryptor;
    }
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        
        try {
            
            SliApi.setBaseUrl(apiUrl);
            
            // Setup OAuth service
            OAuthService service = new ServiceBuilder().provider(SliApi.class)
                    .apiKey(propDecryptor.getDecryptedClientId()).apiSecret(propDecryptor.getDecryptedClientSecret())
                    .callback(callbackUrl).build();
            
            // Check cookies for token, if found insert into session
            boolean cookieFound = checkCookiesForToken(request, session);
            
            Object token = session.getAttribute(OAUTH_TOKEN);
            
            if (token == null && request.getParameter(OAUTH_CODE) == null) {
                // Initiate authentication
                initiatingAuthentication(request, response, session, service);
            } else if (token == null && request.getParameter(OAUTH_CODE) != null) {
                // Verify authentication
                verifyingAuthentication(request, response, session, service);
            } else {
                // Complete authentication
                completeAuthentication(request, response, session, token, cookieFound);
            }
        } catch (OAuthException ex) {
            session.invalidate();
            LOG.error(LOG_MESSAGE_AUTH_EXCEPTION, new Object[] { ex.getMessage() });
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
            return;
        } catch (Exception ex) {
            session.invalidate();
            LOG.error(LOG_MESSAGE_AUTH_EXCEPTION, new Object[] { ex.getMessage() });
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            return;
        }
    }
    
    private boolean isAjaxRequest(HttpServletRequest request) {        
        boolean isAjaxRequest = false;        
        String ajaxHeader = request.getHeader(HEADER_AJAX_INDICATOR);
        if ((ajaxHeader != null) && (ajaxHeader.equalsIgnoreCase(AJAX_REQUEST))) {
            isAjaxRequest = true;
        }
        return isAjaxRequest;
    }
    
    private boolean checkCookiesForToken(HttpServletRequest request, HttpSession session) {
        boolean cookieFound = false;
        
        // If there is no oauth credential, and the user has a dashboard cookie, add cookie value as
        // oauth session attribute.
        if (session.getAttribute(OAUTH_TOKEN) == null) {
            Cookie[] cookies = request.getCookies();
            
            if (cookies != null) {
                
                // Loop through cookies to find dashboard cookie
                for (Cookie c : cookies) {
                    if (c.getName().equals(DASHBOARD_COOKIE)) {
                        
                        JsonObject json = restClient.sessionCheck(c.getValue());
                        
                        // If user is not authenticated, expire the cookie, else set OAUTH_TOKEN to
                        // cookie value and continue
                        JsonElement authElement = json.get(Constants.ATTR_AUTHENTICATED);
                        if ((authElement != null) && (!authElement.getAsBoolean())) {
                            c.setMaxAge(0);
                            LOG.info(LOG_MESSAGE_AUTH_EXPIRING_COOKIE, new Object[] { request.getRemoteAddr() });
                        } else {
                            cookieFound = true;
                            session.setAttribute(OAUTH_TOKEN, c.getValue());
                            LOG.info(LOG_MESSAGE_AUTH_USING_COOKIE, new Object[] { request.getRemoteAddr() });
                        }
                        
                    }
                }
            }
        }
        
        return cookieFound;
    }
    
    private void saveCookieWithToken(HttpServletRequest request, HttpServletResponse response, String token) {
        
        // DE476 Using custom header, since servlet api version 2.5 does not support
        // httpOnly
        // TODO: Remove custom header and use cookie when servlet-api is upgraded to 3.0
        // response.setHeader("Set-Cookie", DASHBOARD_COOKIE + "=" + (String) token +
        // ";path=/;domain=" + domain of the request + ";Secure;HttpOnly");
        response.setHeader("Set-Cookie", DASHBOARD_COOKIE + "=" + (String) token + ";path=/;domain="
                + request.getServerName() + ";HttpOnly");
        
    }
    
    private void initiatingAuthentication(HttpServletRequest request, HttpServletResponse response, HttpSession session, OAuthService service) throws IOException {
        
        LOG.info(LOG_MESSAGE_AUTH_INITIATING, new Object[] { request.getRemoteAddr() });
        
        // The request token doesn't matter for OAuth 2.0 which is why it's null
        String authUrl = service.getAuthorizationUrl(null);
        
      //State is keyword used by idp to forward parameters
      //Adding requestUrl as state, to allow idp to send it back in the redirect
        authUrl += "&" + STATE_PARAMETER + "=" + request.getRequestURL().toString();
        response.sendRedirect(authUrl);        
    }
    
    private void verifyingAuthentication(HttpServletRequest request, HttpServletResponse response, HttpSession session, OAuthService service) throws IOException {
        
        LOG.info(LOG_MESSAGE_AUTH_VERIFYING, new Object[] { request.getRemoteAddr() });
        
        Verifier verifier = new Verifier(request.getParameter(OAUTH_CODE));
        Token accessToken = service.getAccessToken(null, verifier);
        
        //setting cookie, instead of saving oauth_token in session
        saveCookieWithToken(request, response, accessToken.getToken());
        
        //State is keyword used by idp to forward parameters
        //Retrieving entryUrl from state parameter added in initiatingAuthentication
        String entryUrl = request.getParameter(STATE_PARAMETER);

        if (entryUrl != null) {
            response.sendRedirect(entryUrl);
        } else {
            response.sendRedirect(request.getRequestURI());
        }        
    }
    
    private void completeAuthentication(HttpServletRequest request, HttpServletResponse response, HttpSession session, Object token, boolean cookieFound) throws ServletException, IOException {
        
        // Complete Spring security integration
        SLIPrincipal principal = completeSpringAuthentication((String) token);
        
        LOG.info(LOG_MESSAGE_AUTH_COMPLETED, new Object[] { principal.getName(), request.getRemoteAddr() });
        
        // Save the cookie to support sessions across multiple dashboard servers
        saveCookieWithToken(request, response, (String) token);
        
        // AJAX calls OR cookie sessions should not redirect
        if (isAjaxRequest(request) || cookieFound) {            
            RequestDispatcher dispatcher = request.getRequestDispatcher(request.getServletPath());
            dispatcher.forward(request, response);
        } else {            
            LOG.info(LOG_MESSAGE_AUTH_REDIRECTING,
                    new Object[] { principal.getName(), request.getRemoteAddr() });            
            response.sendRedirect(request.getRequestURI());
        }        
    }
    
    private SLIPrincipal completeSpringAuthentication(String token) {
        
        // Get authentication information
        JsonObject json = restClient.sessionCheck(token);
        
        LOG.debug(json.toString());
        
        // If the user is authenticated, create an SLI principal, and authenticate
        JsonElement authElement = json.get(Constants.ATTR_AUTHENTICATED);
        if ((authElement != null) && (authElement.getAsBoolean())) {
            
            // Setup principal
            SLIPrincipal principal = new SLIPrincipal();
            principal.setId(token);
            
            // Extract user name from authentication payload
            String username = "";
            JsonElement nameElement = json.get(Constants.ATTR_AUTH_FULL_NAME);
            if (nameElement != null) {
                username = nameElement.getAsString();
                if (username != null && username.contains("@")) {
                    username = username.substring(0, username.indexOf("@"));
                    if (username.contains(".")) {
                        String first = username.substring(0, username.indexOf('.'));
                        String second = username.substring(username.indexOf('.') + 1);
                        username = first.substring(0, 1).toUpperCase()
                                + (first.length() > 1 ? first.substring(1) : "")
                                + (second.substring(0, 1).toUpperCase() + (second.length() > 1 ? second.substring(1)
                                        : ""));
                    }
                }
            } else {
                LOG.error(LOG_MESSAGE_AUTH_EXCEPTION_INVALID_NAME);
            }
            
            // Set principal name
            principal.setName(username);
            
            // Extract user roles from authentication payload
            LinkedList<GrantedAuthority> authList = new LinkedList<GrantedAuthority>();
            JsonArray grantedAuthorities = json.getAsJsonArray(Constants.ATTR_AUTH_ROLES);
            if (grantedAuthorities != null) {
                
                // Add authorities to user principal
                Iterator<JsonElement> authIterator = grantedAuthorities.iterator();
                while (authIterator.hasNext()) {
                    JsonElement nextElement = authIterator.next();
                    authList.add(new GrantedAuthorityImpl(nextElement.getAsString()));
                }
            } else {
                LOG.error(LOG_MESSAGE_AUTH_EXCEPTION_INVALID_ROLES);
            }
            
            SecurityContextHolder.getContext().setAuthentication(
                    new PreAuthenticatedAuthenticationToken(principal, token, authList));
            
            return principal;
        } else {
            LOG.error(LOG_MESSAGE_AUTH_EXCEPTION_INVALID_AUTHENTICATED);
        }
        
        return null;
    }
    
}
