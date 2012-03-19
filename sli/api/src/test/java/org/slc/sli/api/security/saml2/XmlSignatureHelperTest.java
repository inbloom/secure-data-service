package org.slc.sli.api.security.saml2;

import java.security.Key;
import java.security.cert.Certificate;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

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
    private XmlSignatureHelper signatureHelper;
    
    @Test
    public void getX509CertificateTest() {
        Certificate cert = signatureHelper.getX509CertificateFromKeystore();
        Assert.assertNotNull(cert);
    }
    
    @Test
    public void getSecretKeyTest() {
        Key secretKey = signatureHelper.getSecretKeyFromKeystore();
        Assert.assertNotNull(secretKey);
    }
}
