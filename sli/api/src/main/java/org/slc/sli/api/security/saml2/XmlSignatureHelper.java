package org.slc.sli.api.security.saml2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.security.AccessControlException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.crypto.SecretKey;
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
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
    private static final String SLI_KEYSTORE_PASS = "sli.encryption.keyStorePass";
    private static final String SLI_WILDCARD_CERTIFICATE = "sli.wildcard.x509certificate.alias";
    private static final String SLI_DAL_PASS = "sli.encryption.dalKeyPass";
    private static final String SLI_DAL_KEY_ALIAS = "sli.encryption.dalKeyAlias";
    
    @Value("${sli.encryption.keyStore}")
    private String keyStore;
    
    @Value("${sli.encryption.properties}")
    private String propertiesFile;
    
    public org.w3c.dom.Document signSamlAssertion(org.jdom.Document document) {
        if (document != null) {
            org.w3c.dom.Element signedElement = signSamlAssertion(document, getSecretKeyFromKeystore(),
                    getX509CertificateFromKeystore().getPublicKey());
            org.jdom.Element signedJdomElement = convertToJdom(signedElement);
            return convertDocumentToDocumentDom(document.setRootElement((org.jdom.Element) signedJdomElement.detach()));
        }
        return null;
    }
    
    private org.w3c.dom.Element signSamlAssertion(org.jdom.Document document, Key privateKey, PublicKey publicKey) {
        try {
            XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
            List<Transform> envelopedTransform = Collections.singletonList(signatureFactory.newTransform(Transform.ENVELOPED,
                    (TransformParameterSpec) null));
            Reference ref = signatureFactory.newReference("",
                    signatureFactory.newDigestMethod(DigestMethod.SHA1, null), envelopedTransform, null, null);
            
            // Create the SignatureMethod based on the type of key
            SignatureMethod signatureMethod = null;
            if (publicKey instanceof DSAPublicKey) {
                signatureMethod = signatureFactory.newSignatureMethod(SignatureMethod.DSA_SHA1, null);
            } else if (publicKey instanceof RSAPublicKey) {
                signatureMethod = signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
            }
            
            CanonicalizationMethod canonicalizationMethod = signatureFactory.newCanonicalizationMethod(
                    CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null);
            
            SignedInfo signedInfo = signatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod,
                    Collections.singletonList(ref));
            
            KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
            KeyValue keyValuePair = keyInfoFactory.newKeyValue(publicKey);
            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(keyValuePair));
            
            // Convert the JDOM document to w3c (Java XML signature API requires w3c representation)
            org.w3c.dom.Element w3cElement = convertDocumentToElementDom(document);
            DOMSignContext dsc = new DOMSignContext(privateKey, w3cElement);
            org.w3c.dom.Node xmlSigInsertionPoint = getXmlSignatureInsertionLocation(w3cElement);
            dsc.setNextSibling(xmlSigInsertionPoint);
            
            XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
            signature.sign(dsc);
            
            return w3cElement;
            
        } catch (InvalidAlgorithmParameterException e) {
            LOG.error("invalid algorithm parameter: ", e.getStackTrace());
        } catch (NoSuchAlgorithmException e) {
            LOG.error("specified algorithm does not exist: ", e.getStackTrace());
        } catch (AccessControlException e) {
            LOG.error("requested access to system resource is denied: ", e.getStackTrace());
        } catch (XMLSignatureException e) {
            LOG.error("failed to sign xml: ", e.getStackTrace());
        } catch (KeyException e) {
            LOG.error("issue with secret key: ", e.getStackTrace());
        } catch (MarshalException e) {
            LOG.error("exception occurred during marshaling of xml: ", e.getStackTrace());
        }
        return null;
    }
    
    /**
     * Non-static method to return X509 certificate from the keystore.
     * 
     * @return Certificate containing X509 data.
     */
    public Certificate getX509CertificateFromKeystore() {
        String keyStorePass = null;
        String keyAlias = null;
        String keyPass = null;
        
        FileInputStream propStream = null;
        try {
            propStream = new FileInputStream(new File(propertiesFile));
            Properties props = new Properties();
            props.load(propStream);
            keyStorePass = props.getProperty(SLI_KEYSTORE_PASS);
            keyAlias = props.getProperty(SLI_WILDCARD_CERTIFICATE);
            keyPass = props.getProperty(SLI_DAL_PASS);
        } catch (FileNotFoundException e) {
            LOG.error("properties file not found: {}", e.getStackTrace());
        } catch (IOException e) {
            LOG.error("error loading properties file: {}", e.getStackTrace());
        } finally {
            if (propStream != null) {
                try {
                    propStream.close();
                } catch (IOException e) {
                    LOG.error("error closing properties file: {}", e.getStackTrace());
                }
            }
        }
        
        if (keyStorePass == null) {
            throw new RuntimeException("No key store password found in properties file.");
        }
        if (keyAlias == null) {
            throw new RuntimeException("No key alias found in properties file");
        }
        if (keyPass == null) {
            throw new RuntimeException("No key password found in properties file");
        }
        
        KeyStore ks = loadKeyStore(keyStorePass);
        Certificate cert = null;
        try {
            if (ks.containsAlias(keyAlias)) {
                cert = ks.getCertificate(keyAlias);
            }
        } catch (KeyStoreException e) {
            LOG.error("key store error: {}", e.getStackTrace());
        }
        return cert;
    }
    
    /**
     * Non-static method to return the private key from the keystore.
     * 
     * @return SecretKey representing private key.
     */
    public Key getSecretKeyFromKeystore() {
        String keyStorePass = null;
        String keyAlias = null;
        String keyPass = null;
        
        FileInputStream propStream = null;
        try {
            propStream = new FileInputStream(new File(propertiesFile));
            Properties props = new Properties();
            props.load(propStream);
            keyStorePass = props.getProperty(SLI_KEYSTORE_PASS);
            keyAlias = props.getProperty(SLI_DAL_KEY_ALIAS);
            keyPass = props.getProperty(SLI_DAL_PASS);
        } catch (FileNotFoundException e) {
            LOG.error("properties file not found: {}", e.getStackTrace());
        } catch (IOException e) {
            LOG.error("error loading properties file: {}", e.getStackTrace());
        } finally {
            if (propStream != null) {
                try {
                    propStream.close();
                } catch (IOException e) {
                    LOG.error("error closing properties file: {}", e.getStackTrace());
                }
            }
        }
        
        if (keyStorePass == null) {
            throw new RuntimeException("No key store password found in properties file.");
        }
        if (keyAlias == null) {
            throw new RuntimeException("No key alias found in properties file");
        }
        if (keyPass == null) {
            throw new RuntimeException("No key password found in properties file");
        }
        
        KeyStore ks = loadKeyStore(keyStorePass);
        Key key = null;
        try {
            key = ks.getKey(keyAlias, keyPass.toCharArray());
        } catch (KeyStoreException e) {
            LOG.error("key store error: {}", e.getStackTrace());
        } catch (UnrecoverableKeyException e) {
            LOG.error("key could not be recovered: {}", e.getStackTrace());
        } catch (NoSuchAlgorithmException e) {
            LOG.error("algorithm used generate key in keystore not found: {}", e.getStackTrace());
        }
        if (key != null) {
            if (!(key instanceof SecretKey)) {
                throw new RuntimeException("Expected key of type SecretKey, got " + key.getClass());
            }
            return (SecretKey) key;
        }
        return null;
    }
    
    /**
     * Loads the keystore for retrieving the secret key and X509 certificate.
     * 
     * @param keyStorePass
     *            Password to open keystore.
     * @return KeyStore containing certificate and private key.
     */
    protected KeyStore loadKeyStore(String keyStorePass) {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JCEKS");
        } catch (KeyStoreException e) {
            LOG.error("could not find keystore of matching storetype: {}", e.getStackTrace());
        }
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(keyStore));
            ks.load(fis, keyStorePass.toCharArray());
        } catch (FileNotFoundException e) {
            LOG.error("keystore file not found: {}", e.getStackTrace());
        } catch (IOException e) {
            LOG.error("error loading keystore: {}", e.getStackTrace());
        } catch (NoSuchAlgorithmException e) {
            LOG.error("algorithm used to check integrity of keystore not found: {}", e.getStackTrace());
        } catch (CertificateException e) {
            LOG.error("certificate could not be loaded from keystore: {}", e.getStackTrace());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOG.error("error closing keystore: {}", e.getStackTrace());
                }
            }
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
    public org.w3c.dom.Document convertDocumentToDocumentDom(org.jdom.Document doc) {
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
    public org.w3c.dom.Element convertElementToElementDom(org.jdom.Element element) {
        return convertDocumentToDocumentDom(element.getDocument()).getDocumentElement();
    }
    
    /**
     * Converts a jdom element to a w3c representation of the element.
     * 
     * @param element
     *            jdom element to be converted.
     * @return w3c representation of jdom element.
     */
    public org.w3c.dom.Element convertDocumentToElementDom(org.jdom.Document document) {
        return convertDocumentToDocumentDom(document).getDocumentElement();
    }
    
    /**
     * Stolen shamelessly from (with minor changes):
     * http://google-apps-sso-sample.googlecode.com/svn/trunk/java/samlsource/src/util/Util.java.
     * 
     * Converts an element in w3c representation to jdom representation.
     * 
     * @param element
     *            Element to be converted.
     * @return jdom representation of w3c element.
     */
    private org.jdom.Element convertToJdom(org.w3c.dom.Element element) {
        DOMBuilder builder = new DOMBuilder();
        org.jdom.Element jdomElement = builder.build(element);
        return jdomElement;
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
    private org.w3c.dom.Node getXmlSignatureInsertionLocation(org.w3c.dom.Element element) {
        org.w3c.dom.Node insertLocation = null;
        org.w3c.dom.NodeList nodeList = element.getElementsByTagNameNS(SAML_PROTOCOL_NS_URI_V20, "Extensions");
        if (nodeList.getLength() != 0) {
            insertLocation = nodeList.item(nodeList.getLength() - 1);
        } else {
            nodeList = element.getElementsByTagNameNS(SAML_PROTOCOL_NS_URI_V20, "Status");
            insertLocation = nodeList.item(nodeList.getLength() - 1);
        }
        return insertLocation;
    }
    
}
