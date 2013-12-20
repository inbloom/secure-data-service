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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.DOMBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.opensaml.saml2.core.ArtifactResolve;
import org.opensaml.saml2.core.ArtifactResponse;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.ws.soap.soap11.Body;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.ws.soap.soap11.impl.EnvelopeImpl;
import org.opensaml.xml.XMLObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.util.LinkedMultiValueMap;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.slc.sli.api.representation.CustomStatus;
import org.slc.sli.api.security.OauthMongoSessionManager;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.exceptions.APIAccessDeniedException;
import org.slc.sli.api.security.context.resolver.RealmHelper;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.security.roles.EdOrgContextualRoleBuilder;
import org.slc.sli.api.security.saml.SamlAttributeTransformer;
import org.slc.sli.api.security.saml.SamlHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Unit tests for the Saml Federation Resource class.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SamlFederationResourceTest {

    @InjectMocks
    @Autowired
    private SamlFederationResource resource;

    @Mock
    private Repository<Entity> repo;

    @Mock
    private UserLocator users;

    @Mock
    private RealmHelper realmHelper;

    @Mock
    private SamlHelper samlHelper;

    @Mock
    private OauthMongoSessionManager sessionManager;

    @Mock
    private SamlAttributeTransformer transformer;

    @Mock
    private EdOrgContextualRoleBuilder edOrgRoleBuilder;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ArtifactBindingHelper artifactBindingHelper;

    @Mock
    private SOAPHelper soapHelper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Entity realm = Mockito.mock(Entity.class);
    private Entity edOrg = Mockito.mock(Entity.class);

    private Document doc = Mockito.mock(Document.class);
    private Element rootElement = Mockito.mock(Element.class);


    private SLIPrincipal principal;

    private static final int STATUS_OK = 200;

    private org.opensaml.saml2.core.Response samlResponse;

    private Issuer issuer;
    private String issuerString = "http://localhost";

    private String targetEdorg = "My School";

    private Entity session;

    private Map<String, Object> appSession = new HashMap<String, Object>();

    @SuppressWarnings("unchecked")
    @Before
    public void init() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);

        List<String> roles = new ArrayList<String>();
        roles.add("Educator");
        Entity entity = new MongoEntity("user", "My User", new HashMap<String, Object>(), new HashMap<String, Object>());
        principal = new SLIPrincipal();
        principal.setEntity(entity);
        principal.setRoles(roles);

        Mockito.when(transformer.apply(eq(realm), any(LinkedMultiValueMap.class)))
            .thenAnswer(new Answer<LinkedMultiValueMap<String, String>>() {
                @Override
                public LinkedMultiValueMap<String, String> answer(InvocationOnMock invocation) throws Throwable {
                    return (LinkedMultiValueMap<String, String>) invocation.getArguments()[1];
                }
            });

        Map<String, List<String>> sliEdOrgRoleMap = new HashMap<String, List<String>>();
        sliEdOrgRoleMap.put("My EdOrg", roles);
        Mockito.when(edOrgRoleBuilder.buildValidStaffRoles(anyString(), anyString(), anyString(), eq(roles))).thenReturn(sliEdOrgRoleMap);


        ArtifactResolve artifactResolve = Mockito.mock(ArtifactResolve.class);
        Envelope envelope = Mockito.mock(Envelope.class);
        Mockito.when(artifactBindingHelper.generateArtifactResolveRequest(Mockito.anyString(), Mockito.any(KeyStore.PrivateKeyEntry.class), Mockito.anyString())).thenReturn(artifactResolve);

        Mockito.when(artifactBindingHelper.generateSOAPEnvelope(artifactResolve)).thenReturn(envelope);

        EnvelopeImpl response = Mockito.mock(EnvelopeImpl.class);
        Mockito.when(soapHelper.sendSOAPCommunication(Mockito.any(Envelope.class), Mockito.anyString(), Mockito.any(KeyStore.PrivateKeyEntry.class))).thenReturn(response);

        Mockito.when(samlHelper.parseToDoc(anyString())).thenReturn(doc);

        ArtifactResponse artifactResponse = Mockito.mock(ArtifactResponse.class);
        List<XMLObject> artifactResponses = new ArrayList<XMLObject>();
        artifactResponses.add(artifactResponse);
        Body body = Mockito.mock(Body.class);
        Mockito.when(response.getBody()).thenReturn(body);
        Mockito.when(body.getUnknownXMLObjects()).thenReturn(artifactResponses);

        samlResponse = Mockito.mock(org.opensaml.saml2.core.Response.class);

        issuer = Mockito.mock(Issuer.class);

        Mockito.when(artifactResponse.getMessage()).thenReturn(samlResponse);
        Mockito.when(samlResponse.hasChildren()).thenReturn(true);
        Mockito.when(samlResponse.getIssuer()).thenReturn(issuer);
        Mockito.when(issuer.getValue()).thenReturn(issuerString);

        Mockito.doNothing().when(samlHelper).validateSignature(Mockito.any(org.opensaml.saml2.core.Response.class), Mockito.any(Assertion.class));

        session = Mockito.mock(Entity.class);
        Mockito.when(sessionManager.getSessionForSamlId(Mockito.anyString())).thenReturn(session);


        Map<String, Object> sessionBody = new HashMap<String, Object>();
        sessionBody.put("requestedRealmId", "My Realm");
        sessionBody.put("edOrg", "My School");

        Map<String, String> code = new HashMap<String, String>();

        List<Map<String, Object>> appSessions = new ArrayList<Map<String, Object>>();

        appSessions.add(appSession);
        code.put("value", "testAuthorizationCode");
        sessionBody.put("appSession", appSessions);
        appSession.put("code", code);
        appSession.put("samlId", issuerString);

        Mockito.when(session.getBody()).thenReturn(sessionBody);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getMetadataTest() {
        Response response = resource.getMetadata();

        Assert.assertNotNull(response);
        Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
        Assert.assertNotNull(response.getEntity());

        Exception exception = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader((String) response.getEntity()));
            org.w3c.dom.Document doc = db.parse(is);
            DOMBuilder builder = new DOMBuilder();
            org.jdom.Document jdomDocument = builder.build(doc);
            Iterator<org.jdom.Element> itr = jdomDocument.getDescendants(new ElementFilter());

            while (itr.hasNext()) {
                org.jdom.Element el = itr.next();
                if(el.getName().equals("X509Certificate")) {
                    Assert.assertNotNull(el.getText());
                }
            }
        } catch (ParserConfigurationException e) {
            exception = e;
        } catch (SAXException e) {
            exception = e;
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertNull(exception);
    }

    @Test
    public void testValidPost() {
        String postData = "9A66FHO12BSO3NFH";
        String decodedData = "Test";

        Mockito.when(doc.getDocumentElement()).thenReturn(rootElement);

        org.opensaml.saml2.core.Response samlResponse = Mockito.mock(org.opensaml.saml2.core.Response.class);
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        SamlFederationResource spyResource = Mockito.spy(resource);

        Mockito.when(samlHelper.decodeSAMLPostResponse(postData)).thenReturn(decodedData);
        Mockito.when(samlHelper.parseToDoc(postData)).thenReturn(doc);
        Mockito.when(samlHelper.convertToSAMLResponse(rootElement)).thenReturn(samlResponse);

        Mockito.doReturn(Mockito.mock(Response.class)).when(spyResource).processSAMLResponse(Mockito.any(org.opensaml.saml2.core.Response.class), Mockito.any(UriInfo.class));

        spyResource.processPostBinding(postData, uriInfo);

        Mockito.verify(spyResource, Mockito.times(1)).processSAMLResponse(samlResponse, uriInfo);
    }

    @Test (expected= APIAccessDeniedException.class)
    public void testInvalidEncodedPost() {
        String postData = "9A66FHO12BSO3NFH";
        String decodedData = "Test";
        org.opensaml.saml2.core.Response samlResponse = Mockito.mock(org.opensaml.saml2.core.Response.class);
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        SamlFederationResource spyResource = Mockito.spy(resource);

        Mockito.when(samlHelper.decodeSAMLPostResponse(postData)).thenReturn(decodedData);
        Mockito.when(samlHelper.parseToDoc(decodedData)).thenThrow(new IllegalArgumentException("exception"));

        spyResource.processPostBinding(postData, uriInfo);

        Mockito.verify(spyResource, Mockito.never()).processSAMLResponse(samlResponse, uriInfo);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidDataFromIdp() {
        UriInfo uriInfo = Mockito.mock(UriInfo.class);

        resource.processPostBinding(null, uriInfo);
        Mockito.verify(resource, Mockito.never()).processSAMLResponse(samlResponse, uriInfo);

        resource.processPostBinding("", uriInfo);
        Mockito.verify(resource, Mockito.never()).processSAMLResponse(samlResponse, uriInfo);
    }

    @Test (expected= APIAccessDeniedException.class)
    public void processArtifactBindingInvalidRequest() {
        setRealm(false);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(request.getParameter("RelayState")).thenReturn("My Realm");
        resource.processArtifactBinding(request, uriInfo);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void processArtifactBindingValidRequest() throws URISyntaxException {
        setRealm(false);

        Mockito.when(edOrg.getEntityId()).thenReturn(targetEdorg);
        Mockito.when(realm.getEntityId()).thenReturn("My Realm");
        Mockito.when(repo.findOne(eq("realm"), any(NeutralQuery.class))).thenReturn(realm);
        Mockito.when(repo.findById(eq("realm"), anyString())).thenReturn(realm);
        Mockito.when(repo.findOne(eq(EntityNames.EDUCATION_ORGANIZATION), any(NeutralQuery.class))).thenReturn(edOrg);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        UriInfo uriInfo = Mockito.mock(UriInfo.class);

        URI uri = new URI(issuerString);

        Mockito.when(uriInfo.getRequestUri()).thenReturn(uri);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(uri);

        Mockito.when(request.getParameter("SAMLart")).thenReturn("AAQAAjh3bwgbBZ+LiIx3/RVwDGy0aRUu+xxuNtTZVbFofgZZVCKJQwQNQ7Q=");
        Mockito.when(request.getParameter("RelayState")).thenReturn("My Realm");

        Assertion assertion = createAssertion("01/01/2011", "01/10/2011", issuerString);
        Mockito.when(samlHelper.getAssertion(Mockito.any(org.opensaml.saml2.core.Response.class), Mockito.any(KeyStore.PrivateKeyEntry.class))).thenReturn(assertion);

        Response response = Mockito.mock(Response.class);

        SamlFederationResource spyResource = Mockito.spy(resource);
        Mockito.doReturn(response).when(spyResource).authenticateUser(Mockito.any(LinkedMultiValueMap.class), Mockito.any(Entity.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.any(Entity.class), Mockito.any(URI.class));

        Response resResponse = spyResource.processArtifactBinding(request, uriInfo);
        Assert.assertEquals(response, resResponse);
    }

    @Test
    public void processArtifactBindingInvalidCondition() throws URISyntaxException {
        setRealm(false);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        UriInfo uriInfo = Mockito.mock(UriInfo.class);

        URI uri = new URI(issuerString);

        Mockito.when(uriInfo.getRequestUri()).thenReturn(uri);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(uri);

        Mockito.when(request.getParameter("SAMLart")).thenReturn("AAQAAjh3bwgbBZ+LiIx3/RVwDGy0aRUu+xxuNtTZVbFofgZZVCKJQwQNQ7Q=");
        Mockito.when(request.getParameter("RelayState")).thenReturn("My Realm");

        List<Assertion> assertions = new ArrayList<Assertion>();

        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
        DateTime datetime = DateTime.now();
        datetime = datetime.plusMonths(2) ;
        Assertion assertion = createAssertion(datetime.toString(fmt), "01/10/2011", issuerString);
        assertions.add(assertion);
        Mockito.when(samlHelper.getAssertion(Mockito.any(org.opensaml.saml2.core.Response.class), Mockito.any(KeyStore.PrivateKeyEntry.class))).thenReturn(assertion);

        //invalid condition
        expectedException.expect(APIAccessDeniedException.class);
        expectedException.expectMessage("Authorization could not be verified.");
        resource.processArtifactBinding(request, uriInfo);

        //null subject
        Mockito.when(assertion.getSubject()).thenReturn(null);
        resource.processArtifactBinding(request, uriInfo);

        //invalid subject
        assertions.clear();
        assertions.add(createAssertion("01/10/2011", datetime.toString(fmt),  issuerString));
        resource.processArtifactBinding(request, uriInfo);
    }

    private Assertion createAssertion(String conditionNotBefore,  String subjectNotBefore, String recipient) {
        Assertion assertion = Mockito.mock(Assertion.class);

        Conditions conditions = Mockito.mock(Conditions.class);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");

        DateTime datetime = DateTime.now();
        datetime = datetime.plusMonths(1) ;

        Mockito.when(conditions.getNotBefore()).thenReturn(DateTime.parse(conditionNotBefore, fmt));
        Mockito.when(conditions.getNotOnOrAfter()).thenReturn(DateTime.parse(datetime.toString(fmt), fmt));

        Subject subject = Mockito.mock(Subject.class);
        SubjectConfirmationData subjectConfirmationData = Mockito.mock(SubjectConfirmationData.class);

        SubjectConfirmation subjectConfirmation = Mockito.mock(SubjectConfirmation.class);
        Mockito.when(subjectConfirmation.getSubjectConfirmationData()).thenReturn(subjectConfirmationData);

        ArrayList<SubjectConfirmation> res = new ArrayList<SubjectConfirmation>();
        res.add(subjectConfirmation);

        Mockito.when(subject.getSubjectConfirmations()).thenReturn(res);

        Mockito.when(subjectConfirmationData.getNotBefore()).thenReturn(DateTime.parse(subjectNotBefore, fmt));
        Mockito.when(subjectConfirmationData.getNotOnOrAfter()).thenReturn(DateTime.parse(datetime.toString(fmt), fmt));
        Mockito.when(subjectConfirmationData.getRecipient()).thenReturn(recipient);

        Mockito.when(assertion.getConditions()).thenReturn(conditions);
        Mockito.when(assertion.getSubject()).thenReturn(subject);

        return assertion;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAuthenticateUserValid() throws URISyntaxException {
        LinkedMultiValueMap<String, String> attributes = new LinkedMultiValueMap<String, String>();
        URI uri = new URI(issuerString);

        Mockito.when(sessionManager.getAppSession(anyString(), any(Entity.class))).thenCallRealMethod();
        appSession.put("installed", false);
        appSession.put("redirectUri", issuerString);

        SamlFederationResource spyResource = Mockito.spy(resource);
        Mockito.doReturn(principal).when(spyResource).createPrincipal(Mockito.anyBoolean(), Mockito.anyString(), Mockito.any(LinkedMultiValueMap.class),
                Mockito.anyBoolean(), Mockito.anyString(), Mockito.any(Entity.class), Mockito.anyString(), Mockito.anyString());

        Response res = spyResource.authenticateUser(attributes, realm, targetEdorg, issuerString, session, uri);
        Assert.assertEquals(CustomStatus.FOUND.getStatusCode(), res.getStatus());

        appSession.put("installed", true);
        res = spyResource.authenticateUser(attributes, realm, targetEdorg, issuerString, session, uri);
        Assert.assertEquals(STATUS_OK, res.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAuthenticateUserInvalid() throws URISyntaxException, NoSuchFieldException, IllegalAccessException {
        LinkedMultiValueMap<String, String> attributes = new LinkedMultiValueMap<String, String>();
        URI uri = new URI(issuerString);

        appSession.put("installed", false);
        appSession.put("redirectUri", issuerString);

        setSandboxEnabled(true);

        SamlFederationResource spyResource = Mockito.spy(resource);
        Mockito.doReturn(principal).when(spyResource).createPrincipal(Mockito.anyBoolean(), Mockito.anyString(), Mockito.any(LinkedMultiValueMap.class),
                Mockito.anyBoolean(), Mockito.anyString(), Mockito.any(Entity.class), Mockito.anyString(), Mockito.anyString());

        expectedException.expect(APIAccessDeniedException.class);
        expectedException.expectMessage("Invalid user configuration.");

        setRealm(true);
        spyResource.authenticateUser(attributes, realm, targetEdorg, issuerString, session, uri);
    }


    private void setSandboxEnabled(boolean sandboxEnabled) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field sandboxEnabledField = SamlFederationResource.class.getDeclaredField("sandboxEnabled");
        sandboxEnabledField.setAccessible(true);
        sandboxEnabledField.set(resource, sandboxEnabled);
    }

    private void setRealm(Boolean isAdminRealm) {
        Map<String, Object> realmBody = new HashMap<String, Object>();
        realmBody.put("edOrg", "My School");
        Map<String, List<Map<String, String>>> saml = new HashMap<String, List<Map<String, String>>>();
        saml.put("field", new ArrayList<Map<String, String>>());
        realmBody.put("saml", saml);
        realmBody.put("tenantId", "My Tenant");
        realmBody.put("admin", isAdminRealm);
        realmBody.put("developer", Boolean.FALSE);
        Map<String, Object> idp = new HashMap<String, Object>();
        idp.put("artifactResolutionEndpoint", "https://example");
        idp.put("sourceId", "38776f081b059f8b888c77fd15700c6cb469152e");
        realmBody.put("idp", idp);
        Mockito.when(realm.getBody()).thenReturn(realmBody);
    }

}
