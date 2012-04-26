package org.slc.sli.sandbox.idp.service;

import java.net.URI;
import java.util.List;

import org.slc.sli.sandbox.idp.saml.SamlResponseComposer;
import org.slc.sli.sandbox.idp.saml.SliClient;
import org.slc.sli.sandbox.idp.service.Users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    /**
     * This is the base of the issuer that gets encoded in the SAMLResponse. It will have
     * ?tenant=<tenant> appended to it. The issuer must be unique, and match the field 'body.idp.id'
     * in a realm document, otherwise the API will not be able to figure out which tenant this
     * request is for.
     */
    @Value("${sli.mock-idp.issuer-base}")
    private String issuerBase;
    
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
        
        LOG.info("Login for user: {} roles: {} inResponseTo: {} destination: {}", new Object[] { user.getUserId(), roles,
                requestInfo.getRequestId(), destination.toString() });
        
        String issuer = issuerBase + "?tenant=" + requestInfo.getTenant();
        
        String encodedResponse = samlComposer.componseResponse(destination.toString(), issuer,
                requestInfo.getRequestId(), user.getUserId(), user.getUserId(), roles);
        
        URI redirectUri = sliClient.postResponse(destination, encodedResponse);
        return redirectUri;
    }
    
    protected void setIssuerBase(String base) {
        this.issuerBase = base;
    }
}
