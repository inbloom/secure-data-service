package org.slc.sli.web.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slc.sli.client.RESTClient;
import org.slc.sli.security.SLIAuthenticationEntryPoint;
import org.slc.sli.util.SecurityUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.gson.JsonObject;

/**
 * 
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = SecurityUtil.getToken();

        JsonObject json = restClient.sessionCheck(token);
        if (!json.get("authenticated").getAsBoolean()) {
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
