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

package org.slc.sli.api.resources;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;

import com.sun.jersey.api.Responses;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.core.spi.factory.ResponseImpl;

import org.apache.commons.io.FileUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.resources.security.ApplicationAuthorizationResourceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.security.ApplicationAuthorizationResource;
import org.slc.sli.api.security.context.resolver.AppAuthHelper;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.validator.GenericToEdOrgValidator;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.encrypt.security.CertificateValidationHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Test for support BulkExtract
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class BulkExtractTest {

    private static final HttpContextAdapter CONTEXT = new HttpContextAdapter();
    private static final String INPUT_FILE_NAME = "mock.in.tar.gz";
    private static final String URI_PATH = "http://local.slidev.org:8080/api/rest/v1.2";

    public static final String BULK_DATA = "12345";

    @Autowired
    @InjectMocks
    private BulkExtract bulkExtractToBeSpied;

    private BulkExtract bulkExtract;

    @Autowired
    @InjectMocks
    private AppAuthHelper appAuthHelper;

    @Autowired
    private SecurityContextInjector injector;

    @Mock
    private Repository<Entity> mockMongoEntityRepository;

    @Mock
    private EdOrgHelper edOrgHelper;

    @Mock
    @SuppressWarnings("unused")
    private CertificateValidationHelper helper;

    @Mock
    private HttpServletRequest req;

    @Mock
    private GenericToEdOrgValidator mockValidator;

    @Autowired
    @InjectMocks
    private FileResource fileResource;

    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw1KLTcuf8OpvbHfwMJks\n" +
            "UAXbeaoVqZiK/CRhttWDmlMEs8AubXiSgZCekXeaUqefK544BOgeuNgQmMmo0pLy\n" +
            "j/GoGhf/bSZH2tsx1uKneCUm9Oq1g+juw5HmBa14H914tslvriFpJvN0b7q53Zey\n" +
            "AOxuD06l94UMj7wnMiNypEhowIMyVMMCRR9485hC8YsRtGB+f607bB440+d5zjG8\n" +
            "HGofzWZoCWGR70gJkkOZhwtLw+njpIhmnjDyknngUsOaX1Gza5Fzuz0QtVc/iVHg\n" +
            "iSFSz068XR5+zUmTI3cns6QbGnbsajuaTNQiZUHmQ8LOCddAfZz/7incsD9D9Jfb\n" +
            "YwIDAQAB\n";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Map<String, Object> appBody = new HashMap<String, Object>();
        appBody.put("isBulkExtract", true);

        bulkExtract = Mockito.spy(bulkExtractToBeSpied);
        Mockito.doNothing().when(bulkExtract).logSecurityEvent(Mockito.anyString());

        FileResource spyFileResource = Mockito.spy(fileResource);
        Mockito.doNothing().when(spyFileResource).logSecurityEvent(Mockito.anyString());
        bulkExtract.setFileResource(spyFileResource);

        Set<String> dummySet = new HashSet<String>();
        dummySet.add("dummy");
        when(mockValidator.validate(eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.any(Set.class))).thenReturn(dummySet);

        // Hmm.. this needed?
        bulkExtract.setEdorgValidator(mockValidator);

        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getBody()).thenReturn(appBody);
        when(mockMongoEntityRepository.findOne(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                mockEntity);
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        when(req.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(new X509Certificate[] {cert});
    }

    @Test
    public void testGetSampleExtract() throws Exception {
        injector.setEducatorContext();

        ResponseImpl res = (ResponseImpl) bulkExtract.get(req);
        assertEquals(200, res.getStatus());
        MultivaluedMap<String, Object> headers = res.getMetadata();
        assertNotNull(headers);
        assertTrue(headers.containsKey("content-disposition"));
        assertTrue(headers.containsKey("last-modified"));
        String header = (String) headers.getFirst("content-disposition");
        assertNotNull(header);
        assertTrue(header.startsWith("attachment"));
        assertTrue(header.indexOf("sample-extract.tar") > 0);

        Object entity = res.getEntity();
        assertNotNull(entity);
        StreamingOutput out = (StreamingOutput) entity;
        File file = new File("out.zip");
        FileOutputStream os = new FileOutputStream(file);
        out.write(os);
        os.flush();
        assertTrue(file.exists());

        assertEquals(798669192L, FileUtils.checksumCRC32(file));
        FileUtils.deleteQuietly(file);
    }

  @Test
  public void testGetExtractResponse() throws Exception {
      injector.setOauthAuthenticationWithEducationRole();
      mockApplicationEntity();
      mockBulkExtractEntity(null);

      HttpRequestContext context = new HttpRequestContextAdapter() {
          @Override
          public String getMethod() {
              return "GET";
          }
      };

      Response res = bulkExtract.getExtractResponse(context, null, null, false);
      assertEquals(200, res.getStatus());
      MultivaluedMap<String, Object> headers = res.getMetadata();
      assertNotNull(headers);
      assertTrue(headers.containsKey("content-disposition"));
      assertTrue(headers.containsKey("last-modified"));
      String header = (String) headers.getFirst("content-disposition");
      assertNotNull(header);
      assertTrue(header.startsWith("attachment"));
      assertTrue(header.indexOf(INPUT_FILE_NAME) > 0);

      Object entity = res.getEntity();
      assertNotNull(entity);

      StreamingOutput out = (StreamingOutput) entity;
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      out.write(os);
      os.flush();
      byte[] responseData = os.toByteArray();
      String s = new String(responseData);

      assertEquals(BULK_DATA, s);
  }

  @Test
  public void testHeadTenant() throws Exception {
      injector.setOauthAuthenticationWithEducationRole();
      mockApplicationEntity();
      mockBulkExtractEntity(null);

      HttpRequestContext context = new HttpRequestContextAdapter() {
          @Override
          public String getMethod() {
              return "HEAD";
          }
      };

        Response res = bulkExtract.getExtractResponse(context, null, null, false);
      assertEquals(200, res.getStatus());
      MultivaluedMap<String, Object> headers = res.getMetadata();
      assertNotNull(headers);
      assertTrue(headers.containsKey("content-disposition"));
      assertTrue(headers.containsKey("last-modified"));
      String header = (String) headers.getFirst("content-disposition");
      assertNotNull(header);
      assertTrue(header.startsWith("attachment"));
      assertTrue(header.indexOf(INPUT_FILE_NAME) > 0);

      Object entity = res.getEntity();
      assertNull(entity);
  }

  @Test
  public void testRange() throws Exception {
      injector.setOauthAuthenticationWithEducationRole();
      mockApplicationEntity();
      mockBulkExtractEntity(null);

      HttpRequestContext failureContext = Mockito.mock(HttpRequestContext.class);
      Mockito.when(failureContext.getMethod()).thenReturn("HEAD");
      Mockito.when(failureContext.getHeaderValue("Range")).thenReturn("bytes=0");

        Response failureRes = bulkExtract.getExtractResponse(failureContext, null, null, false);
      assertEquals(416, failureRes.getStatus());

      HttpRequestContext validContext = Mockito.mock(HttpRequestContext.class);
      Mockito.when(validContext.getMethod()).thenReturn("HEAD");
      Mockito.when(validContext.getHeaderValue("Range")).thenReturn("bytes=0-5");

        Response validRes = bulkExtract.getExtractResponse(validContext, null, null, false);
      assertEquals(200, validRes.getStatus());

      HttpRequestContext multiRangeContext = Mockito.mock(HttpRequestContext.class);
      Mockito.when(multiRangeContext.getMethod()).thenReturn("HEAD");
      Mockito.when(multiRangeContext.getHeaderValue("Range")).thenReturn("bytes=0-5,6-10");

        Response multiRangeRes = bulkExtract.getExtractResponse(validContext, null, null, false);
      assertEquals(200, multiRangeRes.getStatus());

  }

  @Test
  public void testFailedEvaluatePreconditions() throws Exception {
      injector.setOauthAuthenticationWithEducationRole();
      mockApplicationEntity();
      mockBulkExtractEntity(null);

      HttpRequestContext context = new HttpRequestContextAdapter() {
          @Override
          public ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag) {
              return Responses.preconditionFailed();
          }
      };

        Response res = bulkExtract.getExtractResponse(context, null, null, false);
      assertEquals(412, res.getStatus());
  }

    @Test
    public void testGetDelta() throws Exception {
        injector.setOauthAuthenticationWithEducationRole();
        mockApplicationEntity();
        mockAppAuth();

        Map<String, Object> body = new HashMap<String, Object>();
        File f = File.createTempFile("bulkExtract", ".tar");

        try {
            body.put(BulkExtract.BULK_EXTRACT_FILE_PATH, f.getAbsolutePath());
            body.put(BulkExtract.BULK_EXTRACT_DATE, "Sun Apr 22 11:00:00 GMT 2013");
            Entity e = new MongoEntity("bulkExtractEntity", body);
            final DateTime d = ISODateTimeFormat.dateTime().parseDateTime("2013-03-31T11:00:00.000Z");
            when(
                    mockMongoEntityRepository.findOne(eq(BulkExtract.BULK_EXTRACT_FILES),
                            argThat(new BaseMatcher<NeutralQuery>() {

                                @Override
                                public boolean matches(Object arg0) {
                                    NeutralQuery query = (NeutralQuery) arg0;
                                    return query.getCriteria().contains(
                                            new NeutralCriteria("date", NeutralCriteria.OPERATOR_EQUAL, d.toDate()))
                                            && query.getCriteria().contains(
                                                    new NeutralCriteria("edorg", NeutralCriteria.OPERATOR_EQUAL,
                                                            "Midvale"));

                                }

                                @Override
                                public void describeTo(Description arg0) {
                                }
                            }))).thenReturn(e);
            Response r = bulkExtract.getDelta(req, CONTEXT, "Midvale", "2013-03-31T11:00:00.000Z");
            assertEquals(200, r.getStatus());
            Response notExisting = bulkExtract.getDelta(req, CONTEXT, "Midvale", "2013-04-01T11:00:00.000Z");
            assertEquals(404, notExisting.getStatus());
        } finally {
            f.delete();
        }
    }

    @Test
    public void testSEADelta() throws Exception {
        String edOrgId = "Midvale";
        Map<String, Object> edOrgBody = new HashMap<String, Object>();
        List<String> orgCategory = new ArrayList<String>();
        orgCategory.add("State Education Agency");
        edOrgBody.put("organizationCategories", orgCategory);
        Entity edOrg = new MongoEntity(EntityNames.EDUCATION_ORGANIZATION, edOrgId, edOrgBody, null);
        when(edOrgHelper.byId(edOrgId)).thenReturn(edOrg);
        when(edOrgHelper.isSEA(edOrg)).thenReturn(true);

        DateTime d = ISODateTimeFormat.dateTime().parseDateTime("2013-05-14T11:00:00.000Z");
        Date date = d.toDate();

        mockBulkExtractEntity(date);
        Mockito.when(edOrgHelper.getDirectChildLEAsOfEdOrg(edOrg)).thenReturn(Arrays.asList("lea123"));
        Set<String> lea = new HashSet<String>();
        lea.add("lea123");

        Mockito.when(mockValidator.validate(EntityNames.EDUCATION_ORGANIZATION, lea)).thenReturn(lea);
        Map<String, Object> authBody = new HashMap<String, Object>();
        authBody.put("applicationId", "App1");
        authBody.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList("lea123"));
        Entity mockAppAuth = Mockito.mock(Entity.class);
        Mockito.when(mockAppAuth.getBody()).thenReturn(authBody);
        Mockito.when(mockMongoEntityRepository.findOne(eq("applicationAuthorization"), Mockito.any(NeutralQuery.class)))
                .thenReturn(mockAppAuth);

        Response res = bulkExtract.getDelta(req, CONTEXT, edOrgId, "2013-05-14T11:00:00.000Z");

        assertEquals(200, res.getStatus());
    }

    private void mockAppAuth() {
        Map<String, Object> authBody = new HashMap<String, Object>();
        authBody.put("applicationId", "App1");
        authBody.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList("Midvale"));
        authBody.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList("Midvale"));
        Entity mockAuth = Mockito.mock(Entity.class);
        when(mockAuth.getBody()).thenReturn(authBody);
        when(mockMongoEntityRepository.findOne(eq("applicationAuthorization"), Mockito.any(NeutralQuery.class)))
                .thenReturn(mockAuth);
    }

    @Test(expected = AccessDeniedException.class)
    public void testAppIsNotAuthorizedForLea() throws Exception {
        injector.setEducatorContext();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("isBulkExtract", true);
        body.put("authorized_ed_orgs", Arrays.asList("ONE"));
        body.put("public_key", "KEY");
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getBody()).thenReturn(body);
        when(mockEntity.getEntityId()).thenReturn("App1");
        when(mockMongoEntityRepository.findOne(eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                mockEntity);

        Map<String, Object> authBody = new HashMap<String, Object>();
        authBody.put("applicationId", "App1");
        authBody.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList("TWO"));
        Entity mockAuth = Mockito.mock(Entity.class);
        when(mockAuth.getBody()).thenReturn(authBody);
        when(mockMongoEntityRepository.findOne(eq("applicationAuthorization"), Mockito.any(NeutralQuery.class)))
                .thenReturn(mockAuth);
        bulkExtract.getEdOrgExtract(CONTEXT, req, "BLEEP");
    }

    @Test(expected = AccessDeniedException.class)
    public void testAppIsNotAuthorizedForDeltaLea() throws Exception {
        injector.setEducatorContext();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("isBulkExtract", true);
        body.put("authorized_ed_orgs", Arrays.asList("ONE"));
        body.put("public_key", "KEY");
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getBody()).thenReturn(body);
        when(mockEntity.getEntityId()).thenReturn("App1");
        when(mockMongoEntityRepository.findOne(eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                mockEntity);

        Map<String, Object> authBody = new HashMap<String, Object>();
        authBody.put("applicationId", "App1");
        authBody.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList("TWO"));
        Entity mockAuth = Mockito.mock(Entity.class);
        when(mockAuth.getBody()).thenReturn(authBody);
        when(mockMongoEntityRepository.findOne(eq("applicationAuthorization"), Mockito.any(NeutralQuery.class)))
                .thenReturn(mockAuth);
        bulkExtract.getDelta(req, CONTEXT, "BLEEP", "2012-12-21");
    }

    @Test(expected = AccessDeniedException.class)
    public void testAppAuthIsEmpty() throws Exception {
        injector.setEducatorContext();
        // No BE Field
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("isBulkExtract", true);
        body.put("authorized_ed_orgs", Arrays.asList("ONE"));
        body.put("public_key", "KEY");
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getBody()).thenReturn(body);
        when(mockEntity.getEntityId()).thenReturn("App1");
        when(mockMongoEntityRepository.findOne(eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                mockEntity);

        // Empty auth
        Map<String, Object> authBody = new HashMap<String, Object>();
        authBody.put("applicationId", "App1");
        authBody.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList());
        Entity mockAuth = Mockito.mock(Entity.class);
        when(mockAuth.getBody()).thenReturn(authBody);
        when(mockMongoEntityRepository.findOne(eq("applicationAuthorization"), Mockito.any(NeutralQuery.class)))
                .thenReturn(mockAuth);
        bulkExtract.getEdOrgExtract(CONTEXT, req, "BLEEP");
    }

    @Test(expected = AccessDeniedException.class)
    public void testAppHasNoAuthorizedEdorgs() throws Exception {
        injector.setEducatorContext();
        // No BE Field
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("isBulkExtract", true);
        body.put("authorized_ed_orgs", Arrays.asList());
        body.put("public_key", "KEY");
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getBody()).thenReturn(body);
        when(mockEntity.getEntityId()).thenReturn("App1");
        when(mockMongoEntityRepository.findOne(eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                mockEntity);
        bulkExtract.getEdOrgExtract(CONTEXT, req, "BLEEP");
    }

    @Test(expected = AccessDeniedException.class)
    public void testUserNotInLEA() throws Exception {
        injector.setEducatorContext();
        // No BE Field
        Mockito.when(mockValidator.validate(eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.any(Set.class)))
                .thenReturn(Collections.EMPTY_SET);
        bulkExtract.getEdOrgExtract(CONTEXT, req, "BLEEP");
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetSLEAListFalseAppAuth() throws Exception {
        injector.setEducatorContext();
        // No BE Field
        Entity mockEntity = mockApplicationEntity();
        mockEntity.getBody().put("isBulkExtract", false);
        bulkExtract.getSEAOrLEAList(req, CONTEXT);
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetSLEAListCheckUserAssociatedSLEAsFailure() throws Exception {
        injector.setEducatorContext();
        mockApplicationEntity();
        bulkExtract.getSEAOrLEAList(req, CONTEXT);
    }

    @Test()
    public void testGetLEAListNoUserAssociatedLEAs() throws Exception {
        injector.setEducatorContext();
        mockApplicationEntity();
        Mockito.when(edOrgHelper.getUserEdorgs(Mockito.any(Entity.class))).thenReturn(Arrays.asList("123"));

        Response res = bulkExtract.getSEAOrLEAList(req, CONTEXT);
        assertEquals(404, res.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test()
    public void testGetSetSEAAndLEAListEmpty() throws Exception {
        injector.setEducatorContext();
        mockApplicationEntity();
        Mockito.when(edOrgHelper.getUserEdorgs(Mockito.any(Entity.class))).thenReturn(Arrays.asList("123"));
        Entity mockAppAuthEntity = Mockito.mock(Entity.class);
        Mockito.when(mockMongoEntityRepository.findOne(Mockito.eq(ApplicationAuthorizationResource.RESOURCE_NAME), Mockito.any(NeutralQuery.class)))
            .thenReturn(mockAppAuthEntity);
        Map<String, Object> body = new HashMap<String, Object>();
        Mockito.when(mockAppAuthEntity.getBody()).thenReturn(body);
        body.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList("123"));
        Mockito.when(mockMongoEntityRepository.findAll(Mockito.eq(BulkExtract.BULK_EXTRACT_FILES), Mockito.any(NeutralQuery.class)))
            .thenReturn(new ArrayList<Entity>());

        Response res = bulkExtract.getSEAOrLEAList(req, CONTEXT);
        assertEquals(200, res.getStatus());
        EntityBody list = (EntityBody) res.getEntity();
        assertNotNull("Links list should not be null", list);
        Map<String, Map<String, String>> fullEdOrgs = (Map<String, Map<String, String>>) list.get("fullEdOrgs");
        assertTrue("LEA full extract list should be empty", fullEdOrgs.isEmpty());
        Map<String, Map<String, String>> deltaEdOrgs = (Map<String, Map<String, String>>) list.get("deltaEdOrgs");
        assertTrue("LEA delta extract list should be empty", deltaEdOrgs.isEmpty());
        Map<String, Map<String, String>> fullSeas = (Map<String, Map<String, String>>) list.get("fullSea");
        assertTrue("SEA full extract list should be empty", fullSeas.isEmpty());
        Map<String, Map<String, String>> deltaSeas = (Map<String, Map<String, String>>) list.get("deltaSea");
        assertTrue("SEA delta extract list should be empty", deltaSeas.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test()
    public void testGetSEAAndLEAListSuccess() throws Exception {
        injector.setEducatorContext();
        mockApplicationEntity();
        List<String> LEAs = Arrays.asList("LEA1");
        Entity mockSEAEntity = Mockito.mock(Entity.class);
        Mockito.when(edOrgHelper.byId("SEA1")).thenReturn(mockSEAEntity);
        Mockito.when(edOrgHelper.isSEA(mockSEAEntity)).thenReturn(true);
        Entity mockLEAEntity = Mockito.mock(Entity.class);
        Mockito.when(edOrgHelper.byId("LEA1")).thenReturn(mockLEAEntity);
        Mockito.when(edOrgHelper.isSEA(mockLEAEntity)).thenReturn(false);
        Mockito.when(edOrgHelper.getSEAOfEdOrg(mockLEAEntity)).thenReturn("SEA1");
        Mockito.when(edOrgHelper.getUserEdorgs(Mockito.any(Entity.class))).thenReturn(LEAs);
        Entity mockAppAuthEntity = Mockito.mock(Entity.class);
        Mockito.when(mockMongoEntityRepository.findOne(Mockito.eq(ApplicationAuthorizationResource.RESOURCE_NAME), Mockito.any(NeutralQuery.class)))
            .thenReturn(mockAppAuthEntity);
        Map<String, Object> body = new HashMap<String, Object>();
        Mockito.when(mockAppAuthEntity.getBody()).thenReturn(body);
        body.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList(LEAs.toArray(new String[]{})));

        Entity fullBulkExtractEntity = mockBulkExtractEntity(null);
        Date deltaTime1 = new Date(1000000000000L);
        String timeStamp1 = ISODateTimeFormat.dateTime().print(new DateTime(deltaTime1));
        Date deltaTime2 = new Date(2000000000000L);
        String timeStamp2 = ISODateTimeFormat.dateTime().print(new DateTime(deltaTime2));
        Entity deltaBulkExtractEntity1 = mockBulkExtractEntity(deltaTime1);
        Entity deltaBulkExtractEntity2 = mockBulkExtractEntity(deltaTime2);
        List<Entity> edOrgs = new ArrayList<Entity>();
        edOrgs.add(fullBulkExtractEntity);
        edOrgs.add(0, deltaBulkExtractEntity1);  // Add in ascending time order,
        edOrgs.add(1, deltaBulkExtractEntity2);  // to assure forward chronology.
        Mockito.when(mockMongoEntityRepository.findAll(Mockito.eq(BulkExtract.BULK_EXTRACT_FILES), Mockito.any(NeutralQuery.class)))
            .thenReturn(edOrgs);

        Response res = bulkExtract.getSEAOrLEAList(req, CONTEXT);
        assertEquals(200, res.getStatus());
        EntityBody list = (EntityBody) res.getEntity();
        assertNotNull("Links list should not be null", list);
        Map<String, Map<String, String>> fullEdOrgs = (Map<String, Map<String, String>>) list.get("fullEdOrgs");
        assertEquals("There should be one LEA full extract link", 1, fullEdOrgs.size());
        assertEquals("Mismatched URI for full extract of LEA \"123\"", URI_PATH + "/bulk/extract/LEA1", fullEdOrgs.get("LEA1").get("uri"));
        Map<String, Map<String, String>> deltaEdOrgs = (Map<String, Map<String, String>>) list.get("deltaEdOrgs");
        assertEquals("There should be one LEA delta extract link list", 1, deltaEdOrgs.size());
        Set<Map<String, String>> leaDeltaLinks = (Set<Map<String, String>>) deltaEdOrgs.get("LEA1");
        assertEquals("There should be two LEA delta extract links for LEA \"LEA1\"", 2, leaDeltaLinks.size());
        Map<String, String> leaDeltaLink1 = (Map<String, String>) leaDeltaLinks.toArray()[0];
        assertEquals("Mismatched delta extraction date for LEA \"LEA1\"", timeStamp2, leaDeltaLink1.get("timestamp"));
        assertEquals("Mismatched URI for delta extract for LEA \"LEA1\"", URI_PATH + "/bulk/extract/LEA1/delta/" + timeStamp2, leaDeltaLink1.get("uri"));
        Map<String, String> leaDeltaLink2 = (Map<String, String>) leaDeltaLinks.toArray()[1];
        assertEquals("Mismatched delta extraction date for LEA \"LEA1\"", timeStamp1, leaDeltaLink2.get("timestamp"));
        assertEquals("Mismatched URI for delta extract for LEA \"LEA1\"", URI_PATH + "/bulk/extract/LEA1/delta/" + timeStamp1, leaDeltaLink2.get("uri"));
        assertEquals("Delta links are not in timestamp order", 1, ISODateTimeFormat.dateTime().parseDateTime(leaDeltaLink1.get("timestamp"))
                .compareTo(ISODateTimeFormat.dateTime().parseDateTime(leaDeltaLink2.get("timestamp"))));
        Map<String, Map<String, String>> fullSeas = (Map<String, Map<String, String>>) list.get("fullSea");
        assertEquals("There should be one SEA full extract link", 1, fullSeas.size());
        assertEquals("Mismatched URI for full extract of SEA \"SEA1\"", URI_PATH + "/bulk/extract/SEA1", fullSeas.get("SEA1").get("uri"));
        Map<String, Map<String, String>> deltaSeas = (Map<String, Map<String, String>>) list.get("deltaSea");
        assertEquals("There should be one SEA delta extract link list", 1, deltaSeas.size());
        Set<Map<String, String>> seaDeltaLinks = (Set<Map<String, String>>) deltaSeas.get("SEA1");
        assertEquals("There should be two SEA delta extract links for SEA \"SEA1\"", 2, seaDeltaLinks.size());
        Map<String, String> seaDeltaLink1 = (Map<String, String>) seaDeltaLinks.toArray()[0];
        assertEquals("Mismatched delta extraction date for SEA \"SEA1\"", timeStamp2, seaDeltaLink1.get("timestamp"));
        assertEquals("Mismatched URI for delta extract for SEA \"SEA1\"", URI_PATH + "/bulk/extract/SEA1/delta/" + timeStamp2, seaDeltaLink1.get("uri"));
        Map<String, String> seaDeltaLink2 = (Map<String, String>) seaDeltaLinks.toArray()[1];
        assertEquals("Mismatched delta extraction date for SEA \"SEA1\"", timeStamp1, seaDeltaLink2.get("timestamp"));
        assertEquals("Mismatched URI for delta extract for SEA \"SEA1\"", URI_PATH + "/bulk/extract/SEA1/delta/" + timeStamp1, seaDeltaLink2.get("uri"));
        assertEquals("Delta links are not in timestamp order", 1, ISODateTimeFormat.dateTime().parseDateTime(seaDeltaLink1.get("timestamp"))
                .compareTo(ISODateTimeFormat.dateTime().parseDateTime(seaDeltaLink2.get("timestamp"))));
    }

    @SuppressWarnings("unchecked")
    @Test()
    public void testGetJustFullLEAListSuccess() throws Exception {
        injector.setEducatorContext();
        mockApplicationEntity();
        Mockito.when(edOrgHelper.getUserEdorgs(Mockito.any(Entity.class))).thenReturn(Arrays.asList("123"));
        Entity mockAppAuthEntity = Mockito.mock(Entity.class);
        Mockito.when(mockMongoEntityRepository.findOne(Mockito.eq(ApplicationAuthorizationResource.RESOURCE_NAME), Mockito.any(NeutralQuery.class)))
            .thenReturn(mockAppAuthEntity);
        Map<String, Object> body = new HashMap<String, Object>();
        Mockito.when(mockAppAuthEntity.getBody()).thenReturn(body);
        body.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList("123"));

        Entity fullBulkExtractEntity = mockBulkExtractEntity(null);
        List<Entity> leas = new ArrayList<Entity>();
        leas.add(fullBulkExtractEntity);
        Mockito.when(mockMongoEntityRepository.findAll(Mockito.eq(BulkExtract.BULK_EXTRACT_FILES), Mockito.any(NeutralQuery.class)))
            .thenReturn(leas);

        Response res = bulkExtract.getSEAOrLEAList(req, CONTEXT);
        assertEquals(200, res.getStatus());
        EntityBody list = (EntityBody) res.getEntity();
        assertNotNull("Links list should not be null", list);
        Map<String, Map<String, String>> fullSeas = (Map<String, Map<String, String>>) list.get("fullSea");
        assertTrue("SEA full extract link list should be empty", fullSeas.isEmpty());
        Map<String, Map<String, String>> deltaSeas = (Map<String, Map<String, String>>) list.get("deltaSea");
        assertTrue("SEA delta extract link list should be empty", deltaSeas.isEmpty());
        Map<String, Map<String, String>> fullEdOrgs = (Map<String, Map<String, String>>) list.get("fullEdOrgs");
        assertEquals("There should be one LEA full extract link", 1, fullEdOrgs.size());
        assertEquals("Mismatched URI for full extract of LEA \"123\"", URI_PATH + "/bulk/extract/123", fullEdOrgs.get("123").get("uri"));
        Map<String, Map<String, String>> deltaEdOrgs = (Map<String, Map<String, String>>) list.get("deltaEdOrgs");
        assertTrue("LEA delta extract link list should be empty", deltaEdOrgs.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test()
    public void testGetJustDeltaLEAListSuccess() throws Exception {
        injector.setEducatorContext();
        mockApplicationEntity();
        Mockito.when(edOrgHelper.getUserEdorgs(Mockito.any(Entity.class))).thenReturn(Arrays.asList("123"));
        Entity mockAppAuthEntity = Mockito.mock(Entity.class);
        Mockito.when(mockMongoEntityRepository.findOne(Mockito.eq(ApplicationAuthorizationResource.RESOURCE_NAME), Mockito.any(NeutralQuery.class)))
            .thenReturn(mockAppAuthEntity);
        Map<String, Object> body = new HashMap<String, Object>();
        Mockito.when(mockAppAuthEntity.getBody()).thenReturn(body);
        body.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList("123"));

        Date deltaTime1 = new Date(1000000000000L);
        String timeStamp1 = ISODateTimeFormat.dateTime().print(new DateTime(deltaTime1));
        Date deltaTime2 = new Date(2000000000000L);
        String timeStamp2 = ISODateTimeFormat.dateTime().print(new DateTime(deltaTime2));
        Entity deltaBulkExtractEntity1 = mockBulkExtractEntity(deltaTime1);
        Entity deltaBulkExtractEntity2 = mockBulkExtractEntity(deltaTime2);
        List<Entity> leas = new ArrayList<Entity>();
        leas.add(0, deltaBulkExtractEntity1);  // Add in ascending time order,
        leas.add(1, deltaBulkExtractEntity2);  // to assure forward chronology.
        Mockito.when(mockMongoEntityRepository.findAll(Mockito.eq(BulkExtract.BULK_EXTRACT_FILES), Mockito.any(NeutralQuery.class)))
            .thenReturn(leas);

        Response res = bulkExtract.getSEAOrLEAList(req, CONTEXT);
        assertEquals(200, res.getStatus());
        EntityBody list = (EntityBody) res.getEntity();
        assertNotNull("Links list should not be null", list);
        Map<String, Map<String, String>> fullSeas = (Map<String, Map<String, String>>) list.get("fullSea");
        assertTrue("SEA full extract link list should be empty", fullSeas.isEmpty());
        Map<String, Map<String, String>> deltaSeas = (Map<String, Map<String, String>>) list.get("deltaSea");
        assertTrue("SEA delta extract link list should be empty", deltaSeas.isEmpty());
        Map<String, Map<String, String>> fullEdOrgs = (Map<String, Map<String, String>>) list.get("fullEdOrgs");
        assertTrue("LEA full extract link list should be empty", fullEdOrgs.isEmpty());
        Map<String, Map<String, String>> deltaEdOrgs = (Map<String, Map<String, String>>) list.get("deltaEdOrgs");
        assertEquals("There should be one LEA delta extract link list", 1, deltaEdOrgs.size());
        Set<Map<String, String>> deltaLinks = (Set<Map<String, String>>) deltaEdOrgs.get("123");
        assertEquals("There should be two LEA delta extract links for LEA \"123\"", 2, deltaLinks.size());
        Map<String, String> deltaLink1 = (Map<String, String>) deltaLinks.toArray()[0];
        assertEquals("Mismatched delta extraction date for LEA \"123\"", timeStamp2, deltaLink1.get("timestamp"));
        assertEquals("Mismatched URI for delta extract for LEA \"123\"", URI_PATH + "/bulk/extract/123/delta/" + timeStamp2, deltaLink1.get("uri"));
        Map<String, String> deltaLink2 = (Map<String, String>) deltaLinks.toArray()[1];
        assertEquals("Mismatched delta extraction date for LEA \"123\"", timeStamp1, deltaLink2.get("timestamp"));
        assertEquals("Mismatched URI for delta extract for LEA \"123\"", URI_PATH + "/bulk/extract/123/delta/" + timeStamp1, deltaLink2.get("uri"));
        assertEquals("Delta links are not in timestamp order", 1, ISODateTimeFormat.dateTime().parseDateTime(deltaLink1.get("timestamp"))
                .compareTo(ISODateTimeFormat.dateTime().parseDateTime(deltaLink2.get("timestamp"))));
    }

    @Test
    public void testNullLEA() {
        try {
            bulkExtract.getDelta(req, CONTEXT, null, "2012-12-21");
            fail("Should have thrown exception for null lea");
        } catch (IllegalArgumentException e) {
            assertTrue(!e.getMessage().isEmpty());
        }
        try {
            bulkExtract.getEdOrgExtract(CONTEXT, req, null);
            fail("Should have thrown exception for null lea");
        } catch (IllegalArgumentException e) {
            assertTrue(!e.getMessage().isEmpty());
        }
    }

    @Test
    public void testNullDeltaDate() {
        try {
            bulkExtract.getDelta(req, CONTEXT, "Midgar", null);
            fail("Should have thrown exception for null date");
        } catch (IllegalArgumentException e) {
            assertTrue(!e.getMessage().isEmpty());
        }
    }

    @Test
    public void testSEAPublicDataExtract() throws IOException, ParseException {
        injector.setOauthAuthenticationWithEducationRole();
        mockApplicationEntity();
        Entity mockedEntity = mockBulkExtractEntity(null);
        Mockito.when(edOrgHelper.byId(eq("SeaPub"))).thenReturn(mockedEntity);
        Mockito.when(edOrgHelper.isSEA(mockedEntity)).thenReturn(true);
        Mockito.when(edOrgHelper.getDirectChildLEAsOfEdOrg(mockedEntity)).thenReturn(Arrays.asList("lea123"));

        Map<String, Object> authBody = new HashMap<String, Object>();
        authBody.put("applicationId", "App1");
        authBody.put(ApplicationAuthorizationResource.EDORG_IDS, ApplicationAuthorizationResourceTest.getAuthList("lea123"));
        Entity mockAppAuth = Mockito.mock(Entity.class);
        Mockito.when(mockAppAuth.getBody()).thenReturn(authBody);
        Mockito.when(mockMongoEntityRepository.findOne(eq("applicationAuthorization"), Mockito.any(NeutralQuery.class)))
                .thenReturn(mockAppAuth);

        Response res = bulkExtract.getEdOrgExtract(CONTEXT, req, "SeaPub");

        assertEquals(200, res.getStatus());
        MultivaluedMap<String, Object> headers = res.getMetadata();
        assertNotNull(headers);
        assertTrue(headers.containsKey("content-disposition"));
        assertTrue(headers.containsKey("last-modified"));
        String header = (String) headers.getFirst("content-disposition");
        assertNotNull(header);
        assertTrue(header.startsWith("attachment"));
        assertTrue(header.indexOf(INPUT_FILE_NAME) > 0);

        Object entity = res.getEntity();
        assertNotNull(entity);

        StreamingOutput out = (StreamingOutput) entity;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        out.write(os);
        os.flush();
        byte[] responseData = os.toByteArray();
        String s = new String(responseData);

        assertEquals(BULK_DATA, s);
    }

    private Entity mockApplicationEntity() {
        Entity mockEntity = Mockito.mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        Mockito.when(mockEntity.getBody()).thenReturn(body);
        Mockito.when(mockEntity.getEntityId()).thenReturn("App1");

        body.put("public_key", PUBLIC_KEY);
        body.put("authorized_ed_orgs", Arrays.asList("ONE"));
        body.put("isBulkExtract", true);
        body.put("_id", "abc123_id");
        Mockito.when(mockMongoEntityRepository.findOne(Mockito.eq(EntityNames.APPLICATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(mockEntity);
        Mockito.when(mockMongoEntityRepository.findOne(eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                mockEntity);
        return mockEntity;
    }

    @SuppressWarnings({ "unchecked" })
    private Entity mockBulkExtractEntity(Date deltaTime) throws IOException, ParseException {
        boolean isDelta = (deltaTime != null);
        File tmpDir = FileUtils.getTempDirectory();
        Entity mockEntity = Mockito.mock(Entity.class);
        Map<String, Object> mockBody = Mockito.mock(Map.class);
        Mockito.when(mockEntity.getBody()).thenReturn(mockBody);

        File inputFile = FileUtils.getFile(tmpDir, INPUT_FILE_NAME);
        FileUtils.writeStringToFile(inputFile, BULK_DATA);
        Mockito.when(mockBody.get(BulkExtract.BULK_EXTRACT_FILE_PATH)).thenReturn(inputFile.getAbsolutePath());
        Mockito.when(mockBody.get("isDelta")).thenReturn(isDelta);
        if (isDelta) {
            Mockito.when(mockBody.get(BulkExtract.BULK_EXTRACT_DATE)).thenReturn(deltaTime);
        } else {
            Mockito.when(mockBody.get(BulkExtract.BULK_EXTRACT_DATE)).thenReturn(new Date());
        }

        Mockito.when(mockMongoEntityRepository.findOne(Mockito.eq(BulkExtract.BULK_EXTRACT_FILES), Mockito.any(NeutralQuery.class)))
                .thenReturn(mockEntity);

        return mockEntity;
    }

    private static class HttpContextAdapter implements HttpContext {

        @Override
        public boolean isTracingEnabled() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void trace(String message) {
            // TODO Auto-generated method stub

        }

        @Override
        public ExtendedUriInfo getUriInfo() {
            ExtendedUriInfo mockUriInfo = Mockito.mock(ExtendedUriInfo.class);
            URI baseURI = null;
            try {
                baseURI = new URI(URI_PATH);
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            UriBuilder mockBuilder = Mockito.mock(UriBuilder.class);
            Mockito.when(mockBuilder.build()).thenReturn(baseURI);
            Mockito.when(mockBuilder.path(Mockito.any(String.class))).thenReturn(null);
            Mockito.when(mockUriInfo.getBaseUriBuilder()).thenReturn(mockBuilder);
            Mockito.when(mockUriInfo.getPath()).thenReturn(URI_PATH  + "/bulk/extract/list");

            return mockUriInfo;
        }

        @Override
        public HttpRequestContext getRequest() {
            return new HttpRequestContextAdapter() {
                @Override
                public String getMethod() {
                    return "GET";
                }
            };
        }

        @Override
        public HttpResponseContext getResponse() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Map<String, Object> getProperties() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private static class HttpRequestContextAdapter implements HttpRequestContext {

        @Override
        public List<String> getRequestHeader(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MultivaluedMap<String, String> getRequestHeaders() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<MediaType> getAcceptableMediaTypes() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Locale> getAcceptableLanguages() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MediaType getMediaType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Locale getLanguage() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Map<String, Cookie> getCookies() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getMethod() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Variant selectVariant(List<Variant> variants) throws IllegalArgumentException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ResponseBuilder evaluatePreconditions(EntityTag eTag) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ResponseBuilder evaluatePreconditions(Date lastModified) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ResponseBuilder evaluatePreconditions() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Principal getUserPrincipal() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isSecure() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public String getAuthenticationScheme() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isTracingEnabled() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void trace(String message) {
            // TODO Auto-generated method stub

        }

        @Override
        public URI getBaseUri() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public UriBuilder getBaseUriBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public URI getRequestUri() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public UriBuilder getRequestUriBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public URI getAbsolutePath() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public UriBuilder getAbsolutePathBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getPath() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getPath(boolean decode) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<PathSegment> getPathSegments() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<PathSegment> getPathSegments(boolean decode) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MultivaluedMap<String, String> getQueryParameters() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getHeaderValue(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        @Deprecated
        public MediaType getAcceptableMediaType(List<MediaType> mediaTypes) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        @Deprecated
        public List<MediaType> getAcceptableMediaTypes(List<QualitySourceMediaType> priorityMediaTypes) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MultivaluedMap<String, String> getCookieNameValueMap() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T getEntity(Class<T> type) throws WebApplicationException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T getEntity(Class<T> type, Type genericType, Annotation[] as) throws WebApplicationException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Form getFormParameters() {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
