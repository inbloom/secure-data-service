package org.slc.sli.sandbox.idp.service;

import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder;
import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder.SamlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthRequests {
    
    @Autowired
    SamlRequestDecoder samlDecoder;
    
    public static class Request {
        private final String tenant;
        private final SamlRequest saml;
        
        private Request(String tenant, SamlRequest saml) {
            this.tenant = tenant;
            this.saml = saml;
        }
        
        public String getTenant() {
            return tenant;
        }
        
        public String getRequestId() {
            return saml.getId();
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
