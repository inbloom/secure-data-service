package org.slc.sli.common.encrypt.security.saml2;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.common.encrypt.security.saml2.DefaultSAML2Validator;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

/**
 * Unit tests for basic saml validation.
 */

public class DefaultSAML2ValidatorTest {

    private DefaultSAML2Validator validator = new DefaultSAML2Validator();

    private DocumentBuilder builder;

    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        builder = dbf.newDocumentBuilder();
        validator.setTrustedCertificatesStore("./trust/trustedCertificates");
    }

    @After
    public void tearDown() throws Exception {
        validator = null;
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
    public void testIsSignatureValidWithValid() throws Exception {
        Document doc = getDocument("complete-valid2.xml");
        Assert.isTrue(validator.isSignatureValid(doc));
    }

    @Test
    public void testIsSignatureValidWithInvalid() throws Exception {
        Document doc = getDocument("complete-invalid.xml");
        Assert.isTrue(!validator.isSignatureValid(doc));
    }

    @Test
    public void testValidatingAValidDocument() throws Exception {
        Document doc = getDocument("complete-valid2.xml");
        Assert.isTrue(validator.isDocumentValid(doc));
    }

    @Test
    public void testValidatingAnInvalidDocument() throws Exception {
        Document doc = getDocument("complete-invalid.xml");
        Assert.isTrue(!validator.isDocumentValid(doc));
    }

    @Test
    public void testIsDigestValidWithValid() throws Exception {
        Document doc = getDocument("complete-valid.xml");
        Assert.isTrue(validator.isDigestValid(doc));
    }

    @Test
    public void testIsDigestValidWithValid2() throws Exception {
        Document doc = getDocument("complete-valid2.xml");
        Assert.isTrue(validator.isDigestValid(doc));
    }

    @Test
    public void testIsDigestInvalidWithInvalid() throws Exception {
        Document doc = getDocument("complete-invalid.xml");
        Assert.isTrue(!validator.isDigestValid(doc));
    }

    @Test
    public void testIsUntrustedAssertionTrusted() throws Exception {
        Document doc = getDocument("adfs-invalid.xml");
        Assert.isTrue(!validator.isDocumentTrusted(doc));
    }

}
