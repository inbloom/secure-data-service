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

package org.slc.sli.dashboard.security;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
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

import org.slc.sli.dashboard.client.APIClient;
import org.slc.sli.dashboard.client.RESTClient;
import org.slc.sli.dashboard.util.Constants;

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
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_AJAX_INDICATOR = "X-Requested-With";
    private static final String AJAX_REQUEST = "XmlHttpRequest";

    // Security Utilities
    private static final String NONSECURE_ENVIRONMENT_NAME = "local";

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
        } /* catch (Exception ex) {
            session.invalidate();
            LOG.error(LOG_MESSAGE_AUTH_EXCEPTION, new Object[] { ex.getMessage() });
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            return;
        }*/
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

                        // DE883. We need to decrypt the cookie value to authenticate the token.
                        String decryptedCookie = null;
                        try {
                            String s = URLDecoder.decode(c.getValue(), "UTF-8");
                            decryptedCookie = propDecryptor.decrypt(s);
                        } catch (GeneralSecurityException e) {
                            LOG.error(e.getMessage());
                        } catch (IOException e) {
                            LOG.error(e.getMessage());
                        } catch (NumberFormatException e) {
                            LOG.info("Failed to decrypt cookie with value: " + c.getValue());
                            return false;
                        }
                        
                        JsonObject json = restClient.sessionCheck(decryptedCookie);

                        // If user is not authenticated, expire the cookie, else set OAUTH_TOKEN to
                        // cookie value and continue
                        JsonElement authElement = json.get(Constants.ATTR_AUTHENTICATED);
                        if ((authElement != null) && (!authElement.getAsBoolean())) {
                            c.setMaxAge(0);
                            LOG.info(LOG_MESSAGE_AUTH_EXPIRING_COOKIE, new Object[] { request.getRemoteAddr() });
                        } else {
                            cookieFound = true;
                            session.setAttribute(OAUTH_TOKEN, decryptedCookie);
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
        String encryptedToken = null;
        String headerString = "";

        // DE883 Encrypt the cookie and save it in the header.
        try {
            encryptedToken = propDecryptor.encrypt(token);
            headerString = DASHBOARD_COOKIE + "=" + URLEncoder.encode(encryptedToken, "UTF-8") + ";path=/;domain="
                    + request.getServerName() + ";HttpOnly";

            if (isSecureRequest(request)) {
                headerString = headerString + (";Secure");
            }

        } catch (GeneralSecurityException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        response.setHeader("Set-Cookie", headerString);
    }

    private void initiatingAuthentication(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, OAuthService service) throws IOException {

        LOG.info(LOG_MESSAGE_AUTH_INITIATING, new Object[] { request.getRemoteAddr() });

        session.setAttribute(ENTRY_URL, request.getRequestURL());

        // The request token doesn't matter for OAuth 2.0 which is why it's null
        String authUrl = service.getAuthorizationUrl(null);
        response.sendRedirect(authUrl);
    }

    private void verifyingAuthentication(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            OAuthService service) throws IOException {

        LOG.info(LOG_MESSAGE_AUTH_VERIFYING, new Object[] { request.getRemoteAddr() });

        Verifier verifier = new Verifier(request.getParameter(OAUTH_CODE));
        Token accessToken = service.getAccessToken(null, verifier);
        session.setAttribute(OAUTH_TOKEN, accessToken.getToken());
        Object entryUrl = session.getAttribute(ENTRY_URL);
        if (entryUrl != null) {
            response.sendRedirect(session.getAttribute(ENTRY_URL).toString());
        } else {
            response.sendRedirect(request.getRequestURI());
        }
    }

    private void completeAuthentication(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            Object token, boolean cookieFound) throws ServletException, IOException {

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
            LOG.info(LOG_MESSAGE_AUTH_REDIRECTING, new Object[] { principal.getName(), request.getRemoteAddr() });
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
            if(json.get(Constants.ATTR_USER_TYPE).getAsString().equals(Constants.ROLE_TEACHER)) {
              authList.add(new GrantedAuthorityImpl(Constants.ROLE_EDUCATOR));
            }

            if(json.get(Constants.ATTR_ADMIN_USER).getAsBoolean()) {
             authList.add(new GrantedAuthorityImpl(Constants.ROLE_IT_ADMINISTRATOR));
            }

            SecurityContextHolder.getContext().setAuthentication(
                    new PreAuthenticatedAuthenticationToken(principal, token, authList));

            return principal;
        } else {
            LOG.error(LOG_MESSAGE_AUTH_EXCEPTION_INVALID_AUTHENTICATED);
        }

        return null;
    }

    /**
     * @param request
     *            - request to be determined
     * @return if the ENV is non-local ( this is due to local jetty server does not
     *         handle secure protocol ) and the protocol is HTTPS, return true.
     *         Otherwise, return false.
     */
    static boolean isSecureRequest(HttpServletRequest request) {

        String serverName = request.getServerName();
        boolean isSecureEnvironment = (!serverName.substring(0, serverName.indexOf(".")).equals(
                NONSECURE_ENVIRONMENT_NAME));

        return (request.isSecure() && isSecureEnvironment);
    }
}
