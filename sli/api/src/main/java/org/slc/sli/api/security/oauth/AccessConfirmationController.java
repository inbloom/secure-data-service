package org.slc.sli.api.security.oauth;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.UnconfirmedAuthorizationCodeAuthenticationTokenHolder;
import org.springframework.security.oauth2.provider.code.UnconfirmedAuthorizationCodeClientToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;

@Controller
@RequestMapping("/oauth/confirm_access")
public class AccessConfirmationController {
    
    private static final Logger LOG = LoggerFactory.getLogger(AccessConfirmationController.class);
    
    @Autowired
    private ClientDetailsService clientDetailsService;
    
    @Autowired
    private AuthorizationCodeServices mongoAuthorizationCodeServices;
    
    @RequestMapping(method = RequestMethod.GET)
    public String getAccessConfirmation(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws Exception {
        final String clientId = request.getParameter("client_id");
        ClientDetails client = SecurityUtil.sudoRun(new SecurityTask<ClientDetails>() {
            
            @Override
            public ClientDetails execute() {
                return clientDetailsService.loadClientByClientId(clientId);
            }
            
        });
        
        // String clientState = request.getParameter("state");
        LOG.debug("loaded app with id: {}", client.getClientId());
        UnconfirmedAuthorizationCodeClientToken token = new UnconfirmedAuthorizationCodeClientToken(
                client.getClientId(), client.getClientSecret(), new HashSet<String>(), "",
                client.getWebServerRedirectUri());
        UnconfirmedAuthorizationCodeAuthenticationTokenHolder combinedAuth = new UnconfirmedAuthorizationCodeAuthenticationTokenHolder(
                token, authentication);
        String code = mongoAuthorizationCodeServices.createAuthorizationCode(combinedAuth);
        
        return "redirect:" + client.getWebServerRedirectUri() + "?code=" + code;
    }
}
