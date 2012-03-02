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
 */
@Component
public class SamlAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        String clientId = URLEncoder.encode(request.getParameter("client_id"), "utf-8");
        String relay = request.getParameter("redirect_uri");
        
        String url = "/api/disco/list?clientId=" + clientId + "&redirect_uri=" + relay;
        
        response.sendRedirect(url);
        return;
    }
}