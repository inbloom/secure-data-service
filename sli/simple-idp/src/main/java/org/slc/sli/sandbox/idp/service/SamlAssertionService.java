package org.slc.sli.sandbox.idp.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.sandbox.idp.saml.SamlResponseComposer;

/**
 * Handles building SAML Response
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@Component
public class SamlAssertionService {

    private static final Logger LOG = LoggerFactory.getLogger(SamlAssertionService.class);

    @Autowired
    SamlResponseComposer samlComposer;

    /**
     * This is the base of the issuer that gets encoded in the SAMLResponse. It will have
     * ?tenant=<tenant> appended to it. The issuer must be unique, and match the field 'body.idp.id'
     * in a realm document, otherwise the API will not be able to figure out which tenant this
     * request is for.
     */
    @Value("${sli.simple-idp.issuer-base}")
    private String issuerBase;

    /**
     * Creates the identity assertion SAML Response to send to the API
     *
     * @param userId
     *            userId to send back in saml assertion
     * @param roles
     *            selected roles
     * @param attributes
     *            map of user attributes to send back in saml assertion
     * @param requestInfo
     *            information previously obtained from an incoming SAMLRequest
     * @return redirect URI sent back by the api in response to the SAMLResponse
     */
    public SamlAssertion buildAssertion(String userId, List<String> roles, Map<String, String> attributes,
            AuthRequestService.Request requestInfo) {
        String destination = requestInfo.getDestination();

        LOG.info("Building SAML assertion for user: {} roles: {} attributes: {} inResponseTo: {} destination: {}",
                new Object[] { userId, roles, attributes, requestInfo.getRequestId(), destination });

        String issuer = issuerBase;
        if (requestInfo.getRealm() != null && requestInfo.getRealm().length() > 0) {
            issuer += "?realm=" + requestInfo.getRealm();
        }

        String encodedResponse = samlComposer.componseResponse(destination, issuer, requestInfo.getRequestId(), userId,
                attributes, roles);

        return new SamlAssertion(destination, encodedResponse);
    }

    protected void setIssuerBase(String base) {
        this.issuerBase = base;
    }

    /**
     * Holds saml response info
     */
    public static class SamlAssertion {
        private final String redirectUri;
        private final String samlResponse;

        public SamlAssertion(String redirectUri, String samlResponse) {
            this.redirectUri = redirectUri;
            this.samlResponse = samlResponse;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public String getSamlResponse() {
            return samlResponse;
        }

    }
}
