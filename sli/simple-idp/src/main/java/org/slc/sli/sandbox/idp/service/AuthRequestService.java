package org.slc.sli.sandbox.idp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder;
import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder.SamlRequest;

/**
 * Service that decodes SAMLRequests
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@Component
public class AuthRequestService {

    @Autowired
    SamlRequestDecoder samlDecoder;

    /**
     * Holds information from the initial request that's eventually needed to complete login.
     */
    public static class Request {
        private String realm;
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

        public void setRealm(String realm) {
            this.realm = realm;
        }

        public boolean isForceAuthn() {
            return saml.isForceAuthn();
        }
    }

    public Request processRequest(String encodedSamlRequest, String realm) {
        if (encodedSamlRequest == null) {
            return null;
        }
        SamlRequest request = samlDecoder.decode(encodedSamlRequest);
        return new Request(realm, request);
    }
}
