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
package org.slc.sli.api.security.saml;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensaml.Configuration;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.signature.Signature;
import org.slc.sli.api.resources.security.KeyStoreAccessor;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.resolver.RealmHelper;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ablum
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SamlHelperTest {
    @InjectMocks
    @Autowired
    SamlHelper samlHelper;

    @Mock
    RealmHelper realmHelper;

    @Autowired
    private KeyStoreAccessor apiKeyStoreAccessor;

    private Entity realm = Mockito.mock(Entity.class);

    private static final String REALM_ID = "REALM_ID";
    private static final String ARTIFACT = "AAQAAjh3bwgbBZ+LiIx3/RVwDGy0aRUu+xxuNtTZVbFofgZZVCKJQwQNQ7Q=";
    private static final String ARTIFACT_RESOLUTION_ENDPOINT = "https://example/artifactResolution";

    private static final String VALID_SOURCEID = "38776f081b059f8b888c77fd15700c6cb469152e";
    private static final String INCORRECT_SOURCEID = "59886f081b059f8b888c77fd15700c6cb469152e";
    private static final String NOT_HEX_SOURCEID = "49887f091b059f8b888c77fd15700c6vf469152f";

    @Value("${sli.api.encryption.certificate.keyAlias}")
    String encryptionCertKeyStoreAlias;

    @Value("#{encryptor.decrypt('${sli.encryption.ldapKeyAlias}', '${sli.encryption.ldapKeyPass}', '${sli.api.encryption.certificate.keyPass}')}")
    String encryptionCertKeyStoreEntryPassword;

    KeyStore.PrivateKeyEntry encryptPKEntry;

    @SuppressWarnings("unchecked")
    @Before
    public void init() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(realmHelper.findRealmById(Mockito.anyString())).thenReturn(realm);
        Mockito.when(realm.getEntityId()).thenReturn(REALM_ID);

        try {
            encryptPKEntry = apiKeyStoreAccessor.getPrivateKeyEntry(encryptionCertKeyStoreAlias, encryptionCertKeyStoreEntryPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testGetArtifactUrl() {
        setRealm(VALID_SOURCEID);

        String result = samlHelper.getArtifactUrl(REALM_ID, ARTIFACT);
        Assert.assertEquals(ARTIFACT_RESOLUTION_ENDPOINT, result);

    }

    @Test(expected = APIAccessDeniedException.class)
    public void testGetArtifactUrlIncorrectSourceId() {
        setRealm(INCORRECT_SOURCEID);

        String result = samlHelper.getArtifactUrl(REALM_ID, ARTIFACT);
        Assert.assertEquals(ARTIFACT_RESOLUTION_ENDPOINT, result);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArtifactUrlInvalidSourceIdFormat() {
        setRealm(NOT_HEX_SOURCEID);

        String result = samlHelper.getArtifactUrl(REALM_ID, ARTIFACT);
        Assert.assertEquals(ARTIFACT_RESOLUTION_ENDPOINT, result);

    }

    @Test
    public void testInlineDecryption() {
        Resource inlineAssertionResource = new ClassPathResource("saml/inlineEncryptedAssertion.xml");
        EncryptedAssertion encAssertion = createAssertion(inlineAssertionResource);

        Assertion assertion = samlHelper.decryptAssertion(encAssertion, encryptPKEntry);
        verifyAssertion(assertion);
    }

    private void verifyAssertion(Assertion assertion) {
        Assert.assertNotNull(assertion);
        Assert.assertNotNull(assertion.getSubject());
        Assert.assertEquals("http://local.slidev.org:8080/api/rest/saml/sso/artifact", assertion.getSubject().getSubjectConfirmations().get(0).getSubjectConfirmationData().getRecipient());
        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getSignature());

        LinkedMultiValueMap<String, String> attributes = samlHelper.extractAttributesFromResponse(assertion);
        Assert.assertEquals(3, attributes.size());
    }

    @Test
    public void testPeerDecryption() {
        Resource peerAssertionResource = new ClassPathResource("saml/peerEncryptedAssertion.xml");
        EncryptedAssertion encAssertion = createAssertion(peerAssertionResource);

        Assertion assertion = samlHelper.decryptAssertion(encAssertion, encryptPKEntry);
        verifyAssertion(assertion);
    }

    @Test
    public void testIsAssertionEncrypted() {
        Response samlResponse = Mockito.mock(Response.class);
        Mockito.when(samlResponse.getEncryptedAssertions()).thenReturn(null);

        boolean result = samlHelper.isAssertionEncrypted(samlResponse);
        Assert.assertFalse(result);

        Mockito.when(samlResponse.getEncryptedAssertions()).thenReturn(new ArrayList<EncryptedAssertion>());
        result = samlHelper.isAssertionEncrypted(samlResponse);
        Assert.assertFalse(result);

        EncryptedAssertion encryptedAssertion = Mockito.mock(EncryptedAssertion.class);
        List<EncryptedAssertion> assertionList = new ArrayList<EncryptedAssertion>();
        assertionList.add(encryptedAssertion);

        Mockito.when(samlResponse.getEncryptedAssertions()).thenReturn(assertionList);
        result = samlHelper.isAssertionEncrypted(samlResponse);
        Assert.assertTrue(result);
    }

    @Test (expected = APIAccessDeniedException.class)
    public void testValidSignature() {
        Response samlResponse = Mockito.mock(Response.class);
        Assertion assertion = Mockito.mock(Assertion.class);
        Signature responseSignature = Mockito.mock(Signature.class);
        Signature assertionSignature = Mockito.mock(Signature.class);
        Element element = Mockito.mock(Element.class);

        Issuer issuer = Mockito.mock(Issuer.class);
        String issuerValue = "http://testidp.com";
        Mockito.when(issuer.getValue()).thenReturn(issuerValue);

        Mockito.when(samlResponse.getIssuer()).thenReturn(issuer);
        Mockito.when(samlResponse.getSignature()).thenReturn(responseSignature);
        Mockito.when(samlResponse.getDOM()).thenReturn(element);
        Mockito.when(assertion.getSignature()).thenReturn(null);
        Mockito.when(assertion.getDOM()).thenReturn(element);

        SamlHelper spyObject = Mockito.spy(samlHelper);

        Mockito.doNothing().when(spyObject).validateFormatAndCertificate(Mockito.any(Signature.class), Mockito.any(Element.class), Mockito.anyString());

        spyObject.validateSignature(samlResponse, assertion);

        Mockito.verify(spyObject, Mockito.times(1)).validateFormatAndCertificate(responseSignature, element, issuerValue);
        Mockito.verify(spyObject, Mockito.times(0)).validateFormatAndCertificate(assertionSignature, element, issuerValue);

        Mockito.when(assertion.getSignature()).thenReturn(assertionSignature);

        spyObject = Mockito.spy(samlHelper);
        Mockito.doNothing().when(spyObject).validateFormatAndCertificate(Mockito.any(Signature.class), Mockito.any(Element.class), Mockito.anyString());
        spyObject.validateSignature(samlResponse, assertion);

        Mockito.verify(spyObject, Mockito.times(1)).validateFormatAndCertificate(responseSignature, element, issuerValue);
        Mockito.verify(spyObject, Mockito.times(1)).validateFormatAndCertificate(assertionSignature, element, issuerValue);

        Mockito.when(samlResponse.getSignature()).thenReturn(null);

        spyObject = Mockito.spy(samlHelper);
        Mockito.doNothing().when(spyObject).validateFormatAndCertificate(Mockito.any(Signature.class), Mockito.any(Element.class), Mockito.anyString());
        spyObject.validateSignature(samlResponse, assertion);

        Mockito.verify(spyObject, Mockito.times(0)).validateFormatAndCertificate(responseSignature, element, issuerValue);
        Mockito.verify(spyObject, Mockito.times(1)).validateFormatAndCertificate(assertionSignature, element, issuerValue);


        Mockito.when(samlResponse.getSignature()).thenReturn(null);
        Mockito.when(assertion.getSignature()).thenReturn(null);

        spyObject = Mockito.spy(samlHelper);
        Mockito.doNothing().when(spyObject).validateFormatAndCertificate(Mockito.any(Signature.class), Mockito.any(Element.class), Mockito.anyString());
        spyObject.validateSignature(samlResponse, assertion);

        Mockito.verify(spyObject, Mockito.times(0)).validateFormatAndCertificate(responseSignature, element, issuerValue);
        Mockito.verify(spyObject, Mockito.times(0)).validateFormatAndCertificate(assertionSignature, element, issuerValue);
    }

    @Test (expected = APIAccessDeniedException.class)
    public void testNonSamlXml() {
        String postData = "<test><child>text</child></test>";
        Document doc = samlHelper.parseToDoc(postData);

        Response samlResponse = samlHelper.convertToSAMLResponse(doc.getDocumentElement());
        Assert.assertNull(samlResponse);
    }

    @Test (expected = APIAccessDeniedException.class)
    public void testNonSamlResponseXml() {
        String postData = "<saml2:AttributeService xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:metadata\"></saml2:AttributeService>";
        Document doc = samlHelper.parseToDoc(postData);

        Response samlResponse = samlHelper.convertToSAMLResponse(doc.getDocumentElement());

        Assert.assertNull(samlResponse);
    }

    private void setRealm(String sourceId) {
        Map<String, Object> realmBody = new HashMap<String, Object>();
        realmBody.put("edOrg", "My School");
        Map<String, List<Map<String, String>>> saml = new HashMap<String, List<Map<String, String>>>();
        saml.put("field", new ArrayList<Map<String, String>>());
        realmBody.put("saml", saml);
        realmBody.put("tenantId", "My Tenant");
        realmBody.put("admin", true);
        realmBody.put("developer", Boolean.FALSE);
        Map<String, Object> idp = new HashMap<String, Object>();
        idp.put("artifactResolutionEndpoint", ARTIFACT_RESOLUTION_ENDPOINT);
        idp.put("sourceId", sourceId);
        realmBody.put("idp", idp);
        Mockito.when(realm.getBody()).thenReturn(realmBody);
    }

    private EncryptedAssertion createAssertion(Resource encAssertionResource) {
        EncryptedAssertion encryptedAssertion = null;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(encAssertionResource.getInputStream());
            Unmarshaller unm = Configuration.getUnmarshallerFactory().getUnmarshaller(EncryptedAssertion.DEFAULT_ELEMENT_NAME);
            XMLObject obj = unm.unmarshall(doc.getDocumentElement());
            encryptedAssertion = (EncryptedAssertion) obj;
            encryptedAssertion = (EncryptedAssertion) Configuration.getUnmarshallerFactory().getUnmarshaller(EncryptedAssertion.DEFAULT_ELEMENT_NAME).unmarshall(doc.getDocumentElement());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return encryptedAssertion;
    }

}
