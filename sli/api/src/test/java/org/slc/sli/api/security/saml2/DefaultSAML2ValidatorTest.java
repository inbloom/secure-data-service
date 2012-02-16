package org.slc.sli.api.security.saml2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

import static junit.framework.Assert.assertTrue;

/**
 * Unit tests for basic saml validation.
 */
public class DefaultSAML2ValidatorTest {

    private DefaultSAML2Validator validator;
    private DocumentBuilder builder;

    @Before
    public void setUp() throws Exception {
        validator = new DefaultSAML2Validator();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        builder = dbf.newDocumentBuilder();

    }

    @After
    public void tearDown() throws Exception {
        validator = null;
    }

    @Test
    public void testValidatingAValidDocument() throws Exception {

        InputStream is = this.getClass().getClassLoader().getResourceAsStream("complete-valid.xml");
        assertTrue(validator.isDocumentValid(builder.parse(is)));
        assertTrue(validator.isDigestValid(builder.parse(is)));
        assertTrue(validator.isSignatureValid(builder.parse(is)));
    }

    @Test
    public void testValidatingAnInvalidDocument() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("complete-invalid.xml");
        assert (validator.isDocumentValid(builder.parse(is)));
    }
}
