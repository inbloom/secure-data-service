package org.slc.sli.api.security.saml2;

import org.w3c.dom.Document;

/**
 * Simple interface for SAML2 validation and signing.
 */
public interface SAML2Validator {

    public boolean isDocumentValid(Document samlDocument);

    public boolean isSignatureValid(Document samlDocument);

    public boolean isDigestValid(Document samlDocument);

    public Document signDocumentWithSAMLSigner(Document samlDocument, SAML2Signer signer);
}
