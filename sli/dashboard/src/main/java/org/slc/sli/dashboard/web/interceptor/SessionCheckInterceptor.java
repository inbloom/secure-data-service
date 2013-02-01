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


package org.slc.sli.dashboard.web.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import org.slc.sli.dashboard.client.RESTClient;
import org.slc.sli.dashboard.security.SLIAuthenticationEntryPoint;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.SecurityUtil;
import org.slc.sli.dashboard.web.controller.ErrorController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Intercepts all incoming requests and ensures user is authenticated against api
 * @author svankina
 * @author rbloh
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
     * Prehandle performs a session check on all incoming requests to ensure a user with an active spring security session,
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
            
            // Only redirect if not error page
            if (!(request.getServletPath().equalsIgnoreCase(ErrorController.EXCEPTION_URL) || request.getServletPath()
                    .equalsIgnoreCase(ErrorController.TEST_EXCEPTION_URL))) {
                response.sendRedirect(request.getRequestURI());
                return false;
            }
        }
        
        return true;
    }
    
}
