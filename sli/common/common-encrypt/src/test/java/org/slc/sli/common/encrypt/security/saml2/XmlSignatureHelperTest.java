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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Unit tests for the XML Signature Helper class.
 *
 * @author shalka
 */
public class XmlSignatureHelperTest {

    private DefaultSAML2Validator validator = new DefaultSAML2Validator();

    private XmlSignatureHelper signatureHelper = new XmlSignatureHelper();

    private DocumentBuilder builder;

    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        builder = dbf.newDocumentBuilder();
        /*
         * There's not really a great solution to configuring this. sli-configuration props would be
         * path dependent (e.g. either sli/common/common-encrypt or sli/api/ would work, but not
         * both), or we can require them to be system properties, which makes it harder to run unit
         * tests in eclipse.
         *
         * This way unit test scope just works, and running api or ingestion uses the normal
         * sli-configuration props
         */
        validator.setTrustedCertificatesStore("./trust/trustedCertificates");
        signatureHelper.setKeyStore("../../data-access/dal/keyStore/localKeyStore.jks");
        signatureHelper.setPropertiesFile("../../data-access/dal/keyStore/localEncryption.properties");
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
        Assert.assertTrue(!validator.isDocumentTrusted(document, "CN=*.slidev.org,OU=Domain Control Validated,O=*.slidev.org"));
    }

    @Ignore
    @Test
    public void signSamlArtifactResolve() throws TransformerException, NoSuchAlgorithmException,
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
        Assert.assertTrue(validator.isDocumentTrusted(signedDom, "CN=*.slidev.org,OU=Domain Control Validated,O=*.slidev.org"));
    }
}
