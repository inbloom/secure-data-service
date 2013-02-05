/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A basic implementation of a SAML2 validator. This is based on OpenAM
 * as it was configured on 2/16/2012.
 */
@Component
public class DefaultSAML2Validator implements SAML2Validator {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSAML2Validator.class);

    @Value("${sli.trust.certificates}")
    private String trustedCertificatesStore;

    private KeyStore cacerts;

    private DOMValidateContext valContext;

    private static final String REGEX = "^https?://@domain(:\\d+)?.*";
    /**
     * Pulls the <Signature> tag from the SAML assertion document.
     *
     * @param samlDocument
     *            Document containing SAML assertion.
     * @return Node representing the Signature block from the SAML assertion.
     */
    private Node getSignatureElement(Document samlDocument) {
        return samlDocument.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature").item(0);
    }

    /**
     * Creates a DOM validator for the SAML assertion document.
     *
     * @param samlDocument
     *            Document containing SAML assertion.
     */
    private void createContext(Document samlDocument) {
        valContext = new DOMValidateContext(new KeyValueKeySelector(), getSignatureElement(samlDocument));
    }

    /**
     * Unmarshals the XML signature from the SAML assertion document.
     *
     * @param samlDocument
     *            Document containing SAML assertion.
     * @return XML Signature element.
     * @throws MarshalException
     *             thrown if an issue occurs during unmarshalling process.
     */
    private XMLSignature getSignature(Document samlDocument) throws MarshalException {
        createContext(samlDocument);
        XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
        return factory.unmarshalXMLSignature(valContext);
    }

    @Override
    public boolean isSignatureTrusted(XMLSignature signature, String issuer) throws KeyStoreException,
            InvalidAlgorithmParameterException, CertificateException, NoSuchAlgorithmException {
        boolean trusted = false;
        X509Certificate certificate = null;

        @SuppressWarnings("unchecked")
        List<XMLStructure> keyInfoContext = signature.getKeyInfo().getContent();

        for (XMLStructure xmlStructure : keyInfoContext) {
            if (xmlStructure instanceof X509Data) {
                X509Data xd = (X509Data) xmlStructure;
                @SuppressWarnings("unchecked")
                Iterator<Object> data = xd.getContent().iterator();
                while (data.hasNext()) {
                    Object nextElement = data.next();
                    if (nextElement instanceof X509Certificate) {
                        certificate = (X509Certificate) nextElement;
                        break;
                    }
                }
            }
        }

        if (certificate != null) {
            if (issuerMatchesSubject(certificate, issuer)) {
                if (cacerts == null) {
                    cacerts = loadCaCerts();
                }
                PKIXParameters params = new PKIXParameters(cacerts);
                params.setRevocationEnabled(false);

                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                CertPath certPath = certFactory.generateCertPath(Arrays.asList(certificate));
                CertPathValidator certPathValidator = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
                try {
                    certPathValidator.validate(certPath, params);
                    trusted = true;
                    LOG.debug("X509 Certificate is trusted.");
                } catch (CertPathValidatorException e) {
                    LOG.error("X509 Certificate is not trusted.");
                }
            }
            else {
            	LOG.error("Domain on the certificate does not match the issuer");
            }
        } else {
            LOG.error("X509 Certificate is null --> no trust can be established.");
        }
        return trusted;
    }

    /**
     * Loads the trusted Certificate Authority store.
     *
     * @return KeyStore containing trusted certificate authorities.
     */
    private KeyStore loadCaCerts() {
        FileInputStream fis = null;
        try {
            cacerts = KeyStore.getInstance("JKS");
            cacerts.load(new FileInputStream(new File(trustedCertificatesStore)), null);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Requested cryptographic algorithm is invalid or unavailable in the current environment", e);
        } catch (CertificateException e) {
            LOG.error("There is an issue with the X509 Certificate", e);
        } catch (FileNotFoundException e) {
            LOG.error("Trusted Certificate Authority store was not found", e);
        } catch (IOException e) {
            LOG.error("There was an issue opening the trusted Certificate Authority store", e);
        } catch (KeyStoreException e) {
            LOG.error("There is an issue with the trusted Certificate Authority store", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOG.error("couldn't close stream", e);
                }
            }
        }
        return cacerts;
    }

    /**
     * Checks that the issuer of the SAML assertion matches the subject of the X.509 certificate.
     *
     * @param certificate
     *            X509 Certificate.
     * @param issuer
     *            Source where SAML assertion originated.
     * @return True if issuer matches subject, false otherwise.
     */
    private boolean issuerMatchesSubject(X509Certificate certificate, String issuer) {
        String subject = getSubjectFromCertificate(certificate);
        boolean verdict=false;
		if (subject != null) {
			String regexp = REGEX.replace("@domain", subject.replace(".","\\.").replace("*", "[\\w-]+"));
			verdict = issuer.matches(regexp);
		}
		
		LOG.info("[{}] Assertion issuer: {}. Certificate domain: {}.", new Object[] { verdict ? "MATCHED" : "MISMATCH", issuer, subject });
		
		return verdict;
    }

    private String getSubjectFromCertificate(X509Certificate certificate) {
        String[] pieces = certificate.getSubjectX500Principal().getName().split(",");
        for (String piece : pieces) {
            if (piece.startsWith("CN=")) {
                String[] names = piece.split("=");
                if (names.length == 2) {
                    return names[1];
                }
            }
        }
        return null;
    }

    @Override
    public boolean isDocumentTrustedAndValid(Document samlDocument, String issuer) throws KeyStoreException,
            InvalidAlgorithmParameterException, CertificateException, NoSuchAlgorithmException, MarshalException {
        return isDocumentTrusted(samlDocument, issuer) && isDocumentValid(samlDocument);
    }

    @Override
    public boolean isDocumentTrusted(Document samlDocument, String issuer) throws KeyStoreException,
            InvalidAlgorithmParameterException, CertificateException, NoSuchAlgorithmException, MarshalException {
        return isSignatureTrusted(getSignature(samlDocument), issuer);
    }

    @Override
    public boolean isDocumentValid(Document samlDocument) {
        try {
            return getSignature(samlDocument).validate(valContext);
        } catch (MarshalException e) {
            LOG.warn("Couldn't validate Document", e);
        } catch (XMLSignatureException e) {
            LOG.warn("Couldn't extract XML Signature from Document", e);
        }
        return false;
    }

    @Override
    public boolean isSignatureValid(Document samlDocument) {
        try {
            return getSignature(samlDocument).getSignatureValue().validate(valContext);
        } catch (MarshalException e) {
            LOG.warn("Couldn't validate signature", e);
        } catch (XMLSignatureException e) {
            LOG.warn("Couldn't validate signature", e);
        }
        return false;
    }

    @Override
    public boolean isDigestValid(Document samlDocument) {
        boolean valid = false;
        try {
            @SuppressWarnings("unchecked")
            Iterator<Reference> iterator = getSignature(samlDocument).getSignedInfo().getReferences().iterator();
            while (iterator.hasNext()) {
                Reference ref = iterator.next();
                valid = ref.validate(valContext);
            }
        } catch (XMLSignatureException e) {
            LOG.warn("Couldn't validate digest", e);
        } catch (MarshalException e) {
            LOG.warn("Couldn't validate digest", e);
        }
        return valid;
    }

    /**
     * Suggest deleting this --> functionality exists within XMLSignatureHelper class.
     */
    @Override
    public Document signDocumentWithSAMLSigner(Document samlDocument, SAML2Signer signer) {
        return null;
    }

    private static class KeyValueKeySelector extends KeySelector {

        @Override
        public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method,
                XMLCryptoContext context) throws KeySelectorException {

            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            SignatureMethod sm = (SignatureMethod) method;
            @SuppressWarnings("unchecked")
            List<XMLStructure> list = keyInfo.getContent();

            for (XMLStructure xmlStructure : list) {
                if (xmlStructure instanceof KeyValue) {
                    PublicKey pk = null;
                    try {
                        pk = ((KeyValue) xmlStructure).getPublicKey();
                    } catch (KeyException ke) {
                        throw new KeySelectorException(ke);
                    }
                    // make sure algorithm is compatible with method
                    if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                        return new SimpleKeySelectorResult(pk);
                    }
                }
                if (xmlStructure instanceof X509Data) {
                    X509Data xd = (X509Data) xmlStructure;
                    @SuppressWarnings("unchecked")
                    Iterator<Object> data = xd.getContent().iterator();
                    for (; data.hasNext();) {
                        Object o = data.next();
                        if (o instanceof X509Certificate) {
                            X509Certificate cert = (X509Certificate) o;
                            return new SimpleKeySelectorResult(cert.getPublicKey());
                        }
                    }
                }
            }
            throw new KeySelectorException("No KeyValue element found!");
        }

        static boolean algEquals(String algURI, String algName) {
            return algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)
                    || algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1);
        }

        public static class SimpleKeySelectorResult implements KeySelectorResult {
            private Key k;

            public SimpleKeySelectorResult(PublicKey k) {
                this.k = k;
            }

            @Override
            public Key getKey() {
                return k;
            }
        }
    }

    protected void setTrustedCertificatesStore(String certStore) {
        trustedCertificatesStore = certStore;
        loadCaCerts();
    }
}
