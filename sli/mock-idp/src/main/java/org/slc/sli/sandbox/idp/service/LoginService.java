package org.slc.sli.sandbox.idp.service;

import java.net.URI;
import java.util.List;

import org.slc.sli.sandbox.idp.saml.SamlResponseComposer;
import org.slc.sli.sandbox.idp.saml.SliClient;
import org.slc.sli.sandbox.idp.service.Users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles building
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Component
public class LoginService {
    
    private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);
    
    @Autowired
    SamlResponseComposer samlComposer;
    
    @Autowired
    SliClient sliClient;
    
    private static String issuer = "http://local.slidev.org:8082/mock-idp";
    
    /**
     * Submits identity assertion to the API based upon the user's choices.
     * 
     * @param user
     *            selected user
     * @param roles
     *            selected roles
     * @param requestInfo
     *            information previously obtained from an incoming SAMLRequest
     * @return redirect URI sent back by the api in response to the SAMLResponse
     */
    public URI login(User user, List<String> roles, AuthRequests.Request requestInfo) {
        URI destination = sliClient.findDestination();
        
        LOG.info("Login for user: {} roles: {} inResponseTo: {} destination: {}", new Object[] { user.getId(), roles,
                requestInfo.getRequestId(), destination.toString() });
        
        String encodedResponse = samlComposer.componseResponse(destination.toString(), issuer,
                requestInfo.getRequestId(), user.getId(), user.getUserName(), roles);
        
        URI redirectUri = sliClient.postResponse(destination, encodedResponse);
        return redirectUri;
    }
}
