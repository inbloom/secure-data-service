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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Class used for signing XML documents.
 * 
 * @author shalka
 */
@Component
public class XmlSignatureHelper {
    
    private static final Logger LOG = LoggerFactory.getLogger(XmlSignatureHelper.class);
    
    private static final String SAML_PROTOCOL_NS_URI_V20 = "urn:oasis:names:tc:SAML:2.0:protocol";
    private static final String SAML_ASSERTION_NS_URI_V20 = "urn:oasis:names:tc:SAML:2.0:assertion";
    private static final String SLI_KEYSTORE_PASS = "sli.encryption.keyStorePass";
    private static final String SLI_WILDCARD_CERTIFICATE = "sli.wildcard.x509certificate.alias";
    
    @Value("${sli.encryption.keyStore}")
    private String keyStore;
    
    @Value("${sli.conf}")
    private String propertiesFile;
    
    /**
     * Signs and returns the w3c representation of the document containing the SAML assertion.
     * 
     * @param document
     *            w3c document to be signed.
     * @return w3c representation of the signed document.
     * @throws TransformerException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws KeyException
     * @throws MarshalException
     * @throws XMLSignatureException
     */
    public Document signSamlAssertion(Document document) throws TransformerException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, KeyException, MarshalException, XMLSignatureException {
        if (document != null) {
            PrivateKeyEntry entry = getPrivateKeyEntryFromKeystore();
            PrivateKey privateKey = entry.getPrivateKey();
            X509Certificate certificate = (X509Certificate) entry.getCertificate();
            Element signedElement = signSamlAssertion(document, privateKey, certificate);
            return signedElement.getOwnerDocument();
        }
        return null;
    }
    
    /**
     * Signs the SAML assertion using the specified public and private keys.
     * 
     * @param document
     *            SAML assertion be signed.
     * @param privateKey
     *            Private key used to sign SAML assertion.
     * @param publicKey
     *            Public key used to sign SAML asserion.
     * @return w3c element representation of specified document.
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws KeyException
     * @throws MarshalException
     * @throws XMLSignatureException
     */
    private Element signSamlAssertion(Document document, PrivateKey privateKey, X509Certificate certificate)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyException, MarshalException,
            XMLSignatureException {
        XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
        List<Transform> envelopedTransform = Collections.singletonList(signatureFactory.newTransform(
                Transform.ENVELOPED, (TransformParameterSpec) null));
        Reference ref = signatureFactory.newReference("", signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
                envelopedTransform, null, null);
        
        SignatureMethod signatureMethod = null;
        if (certificate.getPublicKey() instanceof DSAPublicKey) {
            signatureMethod = signatureFactory.newSignatureMethod(SignatureMethod.DSA_SHA1, null);
        } else if (certificate.getPublicKey() instanceof RSAPublicKey) {
            signatureMethod = signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
        }
        
        CanonicalizationMethod canonicalizationMethod = signatureFactory.newCanonicalizationMethod(
                CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null);
        
        SignedInfo signedInfo = signatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod,
                Collections.singletonList(ref));
        
        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
        X509Data data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(data));
        
        Element w3cElement = document.getDocumentElement();
        Node xmlSigInsertionPoint = getXmlSignatureInsertionLocation(w3cElement);
        DOMSignContext dsc = new DOMSignContext(privateKey, w3cElement, xmlSigInsertionPoint);
        
        XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
        signature.sign(dsc);
        return w3cElement;
    }
    
    /**
     * Non-static method to return private key entry from the keystore.
     * 
     * @return PrivateKeyEntry Entry containing public-private key combination.
     */
    private PrivateKeyEntry getPrivateKeyEntryFromKeystore() {
        String keyStorePass = null;
        String keyAlias = null;
        FileInputStream propStream = null;
        try {
            propStream = new FileInputStream(new File(propertiesFile));
            Properties props = new Properties();
            props.load(propStream);
            keyStorePass = props.getProperty(SLI_KEYSTORE_PASS);
            keyAlias = props.getProperty(SLI_WILDCARD_CERTIFICATE);
        } catch (FileNotFoundException e) {
            LOG.error("properties file not found: {}", e);
        } catch (IOException e) {
            LOG.error("error loading properties file: {}", e);
        } finally {
            if (propStream != null) {
                try {
                    propStream.close();
                } catch (IOException e) {
                    LOG.error("error closing properties file: {}", e);
                }
            }
        }
        
        if (keyStorePass == null) {
            throw new RuntimeException("No key store password found in properties file.");
        }
        if (keyAlias == null) {
            throw new RuntimeException("No key alias found in properties file");
        }
        
        PrivateKeyEntry privateKeyEntry = null;
        KeyStore ks = loadKeyStore(keyStorePass);
        try {
            if (ks.containsAlias(keyAlias)) {
                privateKeyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(keyAlias, new KeyStore.PasswordProtection(
                        keyStorePass.toCharArray()));
            }
        } catch (KeyStoreException e) {
            LOG.warn("key store error: {}", e);
        } catch (UnrecoverableEntryException e) {
            LOG.warn("could not recover entry: {}", e);
        } catch (NoSuchAlgorithmException e) {
            LOG.warn("no such algorithm: {}", e);
        }
        return privateKeyEntry;
    }
    
    /**
     * Loads the keystore for retrieving the secret key and X509 certificate.
     * 
     * @param keyStorePass
     *            Password to open keystore.
     * @return KeyStore containing certificate and private key.
     */
    private KeyStore loadKeyStore(String keyStorePass) {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JCEKS");
            
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(new File(keyStore));
                ks.load(fis, keyStorePass.toCharArray());
            } catch (FileNotFoundException e) {
                LOG.error("keystore file not found: {}", e);
            } catch (IOException e) {
                LOG.error("error loading keystore: {}", e);
            } catch (NoSuchAlgorithmException e) {
                LOG.error("algorithm used to check integrity of keystore not found: {}", e);
            } catch (CertificateException e) {
                LOG.error("certificate could not be loaded from keystore: {}", e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        LOG.error("error closing keystore: {}", e);
                    }
                }
            }
        } catch (KeyStoreException e) {
            LOG.error("could not find keystore of matching storetype", e);
        }
        return ks;
    }
    
    /**
     * Stolen shamelessly from (with minor changes):
     * http://google-apps-sso-sample.googlecode.com/svn/trunk/java/samlsource/src/util/Util.java.
     * 
     * Converts a JDOM Document to a W3 DOM document.
     * 
     * @param doc
     *            JDOM Document
     * @return W3 DOM Document if converted successfully, null otherwise
     */
    public Document convertDocumentToDocumentDom(org.jdom.Document doc) {
        try {
            XMLOutputter xmlOutputter = new XMLOutputter();
            StringWriter elemStrWriter = new StringWriter();
            xmlOutputter.output(doc, elemStrWriter);
            byte[] xmlBytes = elemStrWriter.toString().getBytes();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xmlBytes));
        } catch (IOException e) {
            LOG.error("Error converting JDOM document to W3 DOM document: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            LOG.error("Error converting JDOM document to W3 DOM document: " + e.getMessage());
        } catch (SAXException e) {
            LOG.error("Error converting JDOM document to W3 DOM document: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Stolen shamelessly from (with minor changes):
     * http://google-apps-sso-sample.googlecode.com/svn/trunk/java/samlsource/src/util/Util.java.
     * 
     * Converts a jdom element to a w3c representation of the element.
     * 
     * @param element
     *            jdom element to be converted.
     * @return w3c representation of jdom element.
     */
    public Element convertElementToElementDom(org.jdom.Element element) {
        return convertDocumentToDocumentDom(element.getDocument()).getDocumentElement();
    }
    
    /**
     * Converts a jdom element to a w3c representation of the element.
     * 
     * @param element
     *            jdom element to be converted.
     * @return w3c representation of jdom element.
     */
    public Element convertDocumentToElementDom(org.jdom.Document document) {
        return convertDocumentToDocumentDom(document).getDocumentElement();
    }
    
    /**
     * Stolen shamelessly from (with minor changes):
     * http://google-apps-sso-sample.googlecode.com/svn/trunk/java/samlsource/src/util/
     * XmlDigitalSigner.java.
     * 
     * Determines location to insert the XML 'Signature' element into the SAML response.
     * 
     * @param element
     *            w3c representation of element to be signed
     * @return node that should immediately follow the xml signature
     */
    private Node getXmlSignatureInsertionLocation(Element element) {
        org.w3c.dom.Node insertLocation = null;
        org.w3c.dom.NodeList nodeList = element.getElementsByTagNameNS(SAML_PROTOCOL_NS_URI_V20, "Artifact");
        if (nodeList.getLength() != 0) {
            insertLocation = nodeList.item(nodeList.getLength() - 1);
            return insertLocation;
        }
        
        nodeList = element.getElementsByTagNameNS(SAML_PROTOCOL_NS_URI_V20, "Status");
        if (nodeList.getLength() != 0) {
            insertLocation = nodeList.item(nodeList.getLength() - 1);
            return insertLocation;
        }
        
        nodeList = element.getElementsByTagNameNS(SAML_ASSERTION_NS_URI_V20, "Subject");
        if (nodeList.getLength() != 0) {
            insertLocation = nodeList.item(nodeList.getLength() - 1);
            return insertLocation;
        }
        
        nodeList = element.getElementsByTagNameNS(SAML_ASSERTION_NS_URI_V20, "NameID");
        if (nodeList.getLength() != 0) {
            insertLocation = nodeList.item(nodeList.getLength() - 1);
            return insertLocation;
        }
        
        return null;
    }
    
    protected void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }
    
    protected void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }
    
}
