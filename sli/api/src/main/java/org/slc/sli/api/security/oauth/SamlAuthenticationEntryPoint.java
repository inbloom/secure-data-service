package org.slc.sli.api.security.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    SliClientDetailService clientService = null;
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        String clientId = request.getParameter("client_id");
        String relay = request.getParameter("RelayState");
        if (relay == null) {
            relay = lookupRedirect(clientId);
        }
        
        String url = "/api/disco/list?clientId="
                + clientId + "&RelayState=" + relay;
        
        response.sendRedirect(url);
        return;
    }

    private String lookupRedirect(String clientId) {
       return clientService.loadClientByClientId(clientId).getWebServerRedirectUri();
    }
}