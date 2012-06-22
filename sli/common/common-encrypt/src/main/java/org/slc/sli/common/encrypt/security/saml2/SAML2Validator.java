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
