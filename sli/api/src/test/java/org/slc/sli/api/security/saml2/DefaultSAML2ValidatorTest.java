package org.slc.sli.api.security.saml2;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;


/**
 * Unit tests for basic saml validation.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
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
    public void testValidatingAnInvalidDocument() throws Exception {
        Document doc = getDocument("complete-invalid.xml");
        Assert.isTrue(!validator.isDocumentValid(doc));
    }
}
