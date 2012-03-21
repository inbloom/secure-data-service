package org.slc.sli.api.security.saml2;

import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import junit.framework.Assert;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for the XML Signature Helper class.
 * 
 * @author shalka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class XmlSignatureHelperTest {
    
    @Autowired
    private DefaultSAML2Validator validator;
    
    @Autowired
    private XmlSignatureHelper signatureHelper;
    
    private DocumentBuilder builder;
    
    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        builder = dbf.newDocumentBuilder();
    }
    
    private Document getDocument(final String fileName) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
        try {
            return builder.parse(is);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Test
    public void checkSamlUntrusted() throws KeyStoreException, InvalidAlgorithmParameterException,
            CertificateException, NoSuchAlgorithmException, MarshalException {
        Document document = getDocument("adfs-invalid.xml");
        Assert.assertTrue(!validator.isDocumentTrusted(document));
    }
    
    @Test
    public void signSamlArtifactResolve() throws JDOMException, TransformerException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, KeyException, MarshalException, XMLSignatureException,
            KeyStoreException, CertificateException {
        Document unsignedDocument = getDocument("artifact-resolve-unsigned.xml");
        Document signedDom = signatureHelper.signSamlAssertion(unsignedDocument);
        Assert.assertNotNull(signedDom);
        boolean foundSignedInfo = false;
        boolean foundSignatureValue = false;
        boolean foundKeyInfo = false;
        
        // traverse the saml to find the signature element and its child nodes
        NodeList list = signedDom.getChildNodes();
        if (list.item(0).getNodeName().equals("samlp:ArtifactResolve")) {
            NodeList sublist = list.item(0).getChildNodes();
            for (int i = 0; i < sublist.getLength(); i++) {
                if (sublist.item(i).getNodeName().equals("Signature")) {
                    NodeList signatureList = sublist.item(i).getChildNodes();
                    for (int j = 0; j < signatureList.getLength(); j++) {
                        if (signatureList.item(j).getNodeName().equals("SignedInfo")) {
                            foundSignedInfo = true;
                        } else if (signatureList.item(j).getNodeName().equals("SignatureValue")) {
                            foundSignatureValue = true;
                        } else if (signatureList.item(j).getNodeName().equals("KeyInfo")) {
                            foundKeyInfo = true;
                        }
                    }
                }
            }
        }
        Assert.assertTrue("Should be true if Signature contains SignedInfo tag", foundSignedInfo);
        Assert.assertTrue("Should be true if Signature contains SignatureValue tag", foundSignatureValue);
        Assert.assertTrue("Should be true if Signature contains KeyInfo tag", foundKeyInfo);
        Assert.assertTrue(validator.isDocumentTrusted(signedDom));
    }
}
