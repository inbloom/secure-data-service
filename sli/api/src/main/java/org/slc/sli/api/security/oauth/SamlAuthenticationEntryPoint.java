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
        
        String url = "/api/disco/list";
        String clientId = request.getParameter("client_id");
        String realmName = request.getParameter("RealmName");
        
        if (clientId != null) {
            url += "?clientId=" + URLEncoder.encode(clientId, "utf-8");
        }
       
        if (realmName != null) {
            if (clientId != null) {
                url += "&";
            } else {
                url += "?";
            }
            url += "RealmName=" + URLEncoder.encode(realmName, "utf-8");
        }
        
        response.sendRedirect(url);
        return;
    }
}