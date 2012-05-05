package org.slc.sli.sandbox.idp.service;

import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder;
import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder.SamlRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger LOG = LoggerFactory.getLogger(AuthRequests.class);
    
    @Autowired
    SamlRequestDecoder samlDecoder;
    
    /**
     * Holds information from the initial request that's eventually needed to complete login.
     */
    public static class Request {
        private final String realm;
        private final SamlRequest saml;
        
        Request(String realm, SamlRequest saml) {
            this.realm = realm;
            this.saml = saml;
        }
        
        public String getRealm() {
            return realm;
        }
        
        public String getRequestId() {
            return saml.getId();
        }
        
        public String getDestination() {
            return saml.getSpDestination();
        }
    }
    
    public Request processRequest(String encodedSamlRequest, String realm) {
        if (encodedSamlRequest == null) {
            return null;
        }
        SamlRequest request = samlDecoder.decode(encodedSamlRequest);
        String destination = request.getIdpDestination();
        int index = destination.indexOf("realm=");
        String destinationRealm = destination.substring(index + 6);
        if (!realm.equals(destinationRealm)) {
            LOG.error("Destination realm <" + destinationRealm + "> does not match URL parameter <" + realm
                    + "> with destination attribute: " + destination);
            throw new IllegalArgumentException("Destination realm does not match URL");
        }
        return new Request(realm, request);
    }
}
