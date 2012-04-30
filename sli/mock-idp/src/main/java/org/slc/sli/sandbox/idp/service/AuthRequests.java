package org.slc.sli.sandbox.idp.service;

import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder;
import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder.SamlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Service that decodes SAMLRequests prior to login form being displayed.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Component
public class AuthRequests {
    
    @Autowired
    SamlRequestDecoder samlDecoder;
    
    /**
     * Holds information from the initial request that's eventually needed to complete login.
     */
    public static class Request {
        private final String tenant;
        private final SamlRequest saml;
        
        Request(String tenant, SamlRequest saml) {
            this.tenant = tenant;
            this.saml = saml;
        }
        
        public String getTenant() {
            return tenant;
        }
        
        public String getRequestId() {
            return saml.getId();
        }
        public String getDestination() {
            return saml.getDestination();
        }
    }
    
    public Request processRequest(String encodedSamlRequest, String tenantName) {
        if (encodedSamlRequest == null) {
            return null;
        }
        SamlRequest request = samlDecoder.decode(encodedSamlRequest);
        return new Request(tenantName, request);
    }
}
