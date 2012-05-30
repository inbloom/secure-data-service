package org.slc.sli.common.encrypt.security.saml2;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.xml.crypto.MarshalException;

import org.w3c.dom.Document;

/**
 * Simple interface for SAML2 validation and signing.
 */
public interface SAML2Validator {

    public boolean isDocumentTrustedAndValid(Document samlDocument) throws KeyStoreException,
            InvalidAlgorithmParameterException, CertificateException, NoSuchAlgorithmException, MarshalException;
    
    public boolean isDocumentValid(Document samlDocument);

    public boolean isSignatureValid(Document samlDocument);

    public boolean isDigestValid(Document samlDocument);

    public Document signDocumentWithSAMLSigner(Document samlDocument, SAML2Signer signer);
}
