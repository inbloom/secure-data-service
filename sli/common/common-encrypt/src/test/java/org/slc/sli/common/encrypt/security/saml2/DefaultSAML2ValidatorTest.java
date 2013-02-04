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

import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
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
		Assert.assertTrue(validator.isSignatureValid(doc));
	}

	@Test
	public void testIsSignatureValidWithInvalid() throws Exception {
		Document doc = getDocument("complete-invalid.xml");
		Assert.assertTrue(!validator.isSignatureValid(doc));
	}

	@Test
	public void testValidatingAValidDocument() throws Exception {
		Document doc = getDocument("complete-valid2.xml");
		Assert.assertTrue(validator.isDocumentValid(doc));
	}

	@Test
	public void testValidatingAnInvalidDocument() throws Exception {
		Document doc = getDocument("complete-invalid.xml");
		Assert.assertTrue(!validator.isDocumentValid(doc));
	}

	@Test
	public void testIsDigestValidWithValid() throws Exception {
		Document doc = getDocument("complete-valid.xml");
		Assert.assertTrue(validator.isDigestValid(doc));
	}

	@Test
	public void testIsDigestValidWithValid2() throws Exception {
		Document doc = getDocument("complete-valid2.xml");
		Assert.assertTrue(validator.isDigestValid(doc));
	}

	@Test
	public void testIsDigestInvalidWithInvalid() throws Exception {
		Document doc = getDocument("complete-invalid.xml");
		Assert.assertTrue(!validator.isDigestValid(doc));
	}

	@Test
	public void testIsUntrustedAssertionTrusted() throws Exception {
		Document doc = getDocument("adfs-invalid.xml");
		Assert.assertTrue(!validator.isDocumentTrusted(doc, "CN=*.slidev.org,OU=Domain Control Validated,O=*.slidev.org"));
	}

	@Test
	public void testIssuerMatches() throws Exception {
		Assert.assertTrue(isIssuerValid("CN=sidp.sandbox-staging.slcedu.org", "https://sidp.sandbox-staging.slcedu.org"));
		Assert.assertTrue(isIssuerValid("CN=sidp.sandbox-staging.slcedu.org", "https://sidp.sandbox-staging.slcedu.org/"));
		Assert.assertTrue(isIssuerValid("CN=sidp.sandbox-staging.slcedu.org", "https://sidp.sandbox-staging.slcedu.org/arggg"));
		Assert.assertTrue(isIssuerValid("CN=sidp.sandbox-staging.slcedu.org", "https://sidp.sandbox-staging.slcedu.org/two/levels"));
		Assert.assertTrue(isIssuerValid("CN=sidp.sandbox-staging.slcedu.org", "https://sidp.sandbox-staging.slcedu.org:8080/two/levels"));
	}

	@Test
	public void testIssuerMatchesWildcards() throws Exception {
		Assert.assertTrue(isIssuerValid("CN=*.sandbox-staging.slcedu.org", "https://sidp.sandbox-staging.slcedu.org"));
		Assert.assertTrue(isIssuerValid("CN=*.sandbox-staging.slcedu.org", "https://sidp.sandbox-staging.slcedu.org/"));
		Assert.assertTrue(isIssuerValid("CN=*.sandbox-staging.slcedu.org", "https://sidp.sandbox-staging.slcedu.org/arggg"));
		Assert.assertTrue(isIssuerValid("CN=*.sandbox-staging.slcedu.org", "https://sidp.sandbox-staging.slcedu.org/two/levels"));
		Assert.assertTrue(isIssuerValid("CN=*.sandbox-staging.slcedu.org", "https://sidp.sandbox-staging.slcedu.org:8080/two/levels"));
	}

	@Test
	public void testIssuerMatchesWildcardsTwoLevels() throws Exception {
		Assert.assertTrue(isIssuerValid("CN=*.*.slcedu.org", "https://sidp.sandbox-staging.slcedu.org"));
		Assert.assertTrue(isIssuerValid("CN=*.*.slcedu.org", "https://sidp.sandbox-staging.slcedu.org/"));
		Assert.assertTrue(isIssuerValid("CN=*.*.slcedu.org", "https://sidp.sandbox-staging.slcedu.org/arggg"));
		Assert.assertTrue(isIssuerValid("CN=*.*.slcedu.org", "https://sidp.sandbox-staging.slcedu.org/two/levels"));
		Assert.assertTrue(isIssuerValid("CN=*.*.slcedu.org", "https://sidp.sandbox-staging.slcedu.org:8080/two/levels"));
		Assert.assertTrue("Major Weirdness", isIssuerValid("CN=*.*.slcedu.org", "https://si_d-p.s-_and-b_ox-staging.slcedu.org:8080/two/levels"));
	}

	@Test
	public void testIssuerDoesNotMatch() throws Exception {
		Assert.assertFalse("Garbage matched", isIssuerValid("CN=*.sandbox-staging.slcedu.org", "https://sidp.-stang.slcedu.org"));
		Assert.assertFalse("Domain as path matched", isIssuerValid("CN=*.sandbox-staging.slcedu.org", "https://malicious.com/sidp.sandbox-staging.slcedu.org"));
		Assert.assertFalse("Domain as query param matched", isIssuerValid("CN=*.sandbox-staging.slcedu.org", "https://malicious.com?impersonate=sidp.sandbox-staging.slcedu.org"));
	}

	/**
	 * Yes I know it is a hack to use reflection, but it is a better hack than making
	 * the method public
	 * @param cn
	 * @param issuer
	 * @return
	 * @throws Exception
	 */
	private boolean isIssuerValid(String cn, String issuer) throws Exception {
		X509Certificate cert = Mockito.mock(X509Certificate.class);
		X500Principal pr = new X500Principal(cn);
		Mockito.when(cert.getSubjectX500Principal()).thenReturn(pr);
		Method m = this.validator.getClass().getDeclaredMethod("issuerMatchesSubject", X509Certificate.class, String.class);
		m.setAccessible(true);
		boolean verdict = (Boolean) m.invoke(this.validator, new Object[] { cert, issuer });
		return verdict;
	}

}
