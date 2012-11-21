/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.sandbox.idp.service;

import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder;
import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder.SamlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
