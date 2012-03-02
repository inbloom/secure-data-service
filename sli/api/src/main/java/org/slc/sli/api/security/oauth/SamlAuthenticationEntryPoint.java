package org.slc.sli.api.security.oauth;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 
 * @author pwolf
 *
 * Handles redirecting an unauthenticated user to disco
 */
@Component
public class SamlAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        String clientId = URLEncoder.encode(request.getParameter("client_id"), "utf-8");
        //String relay = request.getParameter("RelayState");
        
        String url = "/api/disco/list?clientId=" + clientId;
        
        if (request.getParameter("RealmName") != null) {
            url += "&RealmName=" + URLEncoder.encode(request.getParameter("RealmName"), "utf-8");
        }
        
        response.sendRedirect(url);
        return;
    }
}