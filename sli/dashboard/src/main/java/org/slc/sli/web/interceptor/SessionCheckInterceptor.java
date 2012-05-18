package org.slc.sli.web.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slc.sli.client.RESTClient;
import org.slc.sli.security.SLIAuthenticationEntryPoint;
import org.slc.sli.util.Constants;
import org.slc.sli.util.SecurityUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.gson.JsonObject;

/**
 * Intercepts all incoming requests and ensures user is authenticated against api
 * @author svankina
 *
 */
public class SessionCheckInterceptor extends HandlerInterceptorAdapter {

    private RESTClient restClient;
    
    public RESTClient getRestClient() {
        return restClient;
    }

    public void setRestClient(RESTClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Prehandle performs a session check on all incoming requests to ensure a user with an active spring securoty session,
     *  is still authenticated against the api.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = SecurityUtil.getToken();

        JsonObject json = restClient.sessionCheck(token);
        
        // If the user is not authenticated, expire the cookie and set oauth_token to null
        if (!json.get(Constants.ATTR_AUTHENTICATED).getAsBoolean()) {
            SecurityContextHolder.getContext().setAuthentication(null);
            HttpSession session = request.getSession();
            session.setAttribute(SLIAuthenticationEntryPoint.OAUTH_TOKEN, null);
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals(SLIAuthenticationEntryPoint.DASHBOARD_COOKIE)) {
                    c.setMaxAge(0);
                }
            }
            response.sendRedirect(request.getRequestURI());
            return false;
        }
        
        return true;
    }
    
}
