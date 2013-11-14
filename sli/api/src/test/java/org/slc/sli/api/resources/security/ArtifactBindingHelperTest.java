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
package org.slc.sli.api.resources.security;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opensaml.saml2.core.ArtifactResolve;
import org.opensaml.ws.soap.soap11.Envelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

/**
 * @author: npandey
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ArtifactBindingHelperTest {

    @Autowired
    ArtifactBindingHelper artifactBindingHelper;

    @Value("${sli.security.sp.issuerName}")
    private String issuerName;

    @Value("${sli.security.idp.url}")
    private String idpUrl;

    @Value("${sli.api.digital.signature.alias}")
    private String keystoreAlias;

    @Value("#{encryptor.decrypt('${sli.encryption.ldapKeyAlias}', '${sli.encryption.ldapKeyPass}', '${sli.api.keystore.entry.password}')}")
    String keyStorEntryPassword;

    @Autowired
    KeyStoreAccessor apiKeyStoreAccessor;

    KeyStore.PrivateKeyEntry pkEntry;

    @Before
    public void setUp() throws CertificateException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, IOException {
       pkEntry = apiKeyStoreAccessor.getPrivateKeyEntry(keystoreAlias, keyStorEntryPassword);
    }

    @Test
    public void generateArtifactRequestTest() {
        ArtifactResolve ar = artifactBindingHelper.generateArtifactResolveRequest("test1234", pkEntry);
        Assert.assertEquals("test1234", ar.getArtifact().getArtifact());
        Assert.assertTrue(ar.isSigned());
        Assert.assertNotNull(ar.getSignature().getKeyInfo().getX509Datas());
        Assert.assertEquals(ar.getDestination(), idpUrl);
        Assert.assertEquals(issuerName, ar.getIssuer().getValue());
    }

    @Test
    public void generateSOAPEnvelopeTest() {
        ArtifactResolve artifactRequest = Mockito.mock(ArtifactResolve.class);
        Envelope env = artifactBindingHelper.generateSOAPEnvelope(artifactRequest);
        Assert.assertEquals(artifactRequest, env.getBody().getUnknownXMLObjects().get(0));
        Assert.assertEquals(Envelope.DEFAULT_ELEMENT_NAME, env.getElementQName());
    }
}
