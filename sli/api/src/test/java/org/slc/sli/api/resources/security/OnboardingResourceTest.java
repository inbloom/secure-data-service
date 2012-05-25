package org.slc.sli.api.resources.security;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import junit.framework.Assert;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.client.constants.ResourceConstants;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.security.TenantResource.LandingZoneInfo;
import org.slc.sli.api.resources.security.TenantResource.TenantResourceCreationException;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * JUnit for Onboarding Resource
 *
 * @author nbrown
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class OnboardingResourceTest {

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    @InjectMocks
    private OnboardingResource resource;

    @Autowired
    private MockRepo repo;

    @Mock
    private TenantResource mockTenantResource;

    UriInfo uriInfo = null;
    HttpHeaders headers = null;

    String dashboardId = "";
    String databrowserId = "";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        injector.setDeveloperContext();
        resource.isSandboxImpersonationEnabled = true;
        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);
        headers = mock(HttpHeaders.class);
        when(headers.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(headers.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());

        // mockTenantResource = mock(TenantResource.class);

        // clear all related collections
        repo.deleteAll("educationOrganization");
        repo.deleteAll(OnboardingResource.APPLICATION_AUTH_RESOURCE_NAME);
        repo.deleteAll(OnboardingResource.APPLICATION_RESOURCE_NAME);

        // create Dashboard application in mongod
        dashboardId = repo.create(OnboardingResource.APPLICATION_RESOURCE_NAME, buildDashboardBody()).getEntityId();

        // create Databrowser application in mongod
        databrowserId = repo.create(OnboardingResource.APPLICATION_RESOURCE_NAME, buildDatabrowserBody()).getEntityId();
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        repo.deleteAll("educationalOrganization");
        repo.deleteAll(OnboardingResource.APPLICATION_AUTH_RESOURCE_NAME);
        repo.deleteAll(OnboardingResource.APPLICATION_RESOURCE_NAME);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProvision() {
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put(OnboardingResource.STATE_EDORG_ID, "TestOrg");
        requestBody.put(ResourceConstants.ENTITY_METADATA_TENANT_ID, "12345");

        LandingZoneInfo landingZone = new LandingZoneInfo("LANDING ZONE", "INGESTION SERVER");

        Map<String, String> tenantBody = new HashMap<String, String>();
        tenantBody.put("landingZone", "LANDING ZONE");
        tenantBody.put("ingestionServer", "INGESTION SERVER");

        // Entity tenantEntity = Mockito.mock(Entity.class);
        // when(tenantEntity.getBody()).thenReturn(tenantBody);
        try {
            when(mockTenantResource.createLandingZone("12345", "TestOrg")).thenReturn(landingZone);
        } catch (TenantResourceCreationException e) {
            Assert.fail(e.getMessage());
        }

        Response res = resource.provision(requestBody, null);
        assertTrue(Status.fromStatusCode(res.getStatus()) == Status.CREATED);
        Map<String, String> result = (Map<String, String>) res.getEntity();
        assertNotNull(result.get("landingZone"));
        Assert.assertEquals("LANDING ZONE", result.get("landingZone"));
        assertNotNull(result.get("edOrg"));
        assertNotNull(result.get("serverName"));
        Assert.assertEquals("landingZone", result.get("serverName"));

        // check new edorg has been created in mongod
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(OnboardingResource.STATE_EDORG_ID, NeutralCriteria.OPERATOR_EQUAL,
                "TestOrg"));
        String edorgId = repo.findOne("educationOrganization", query).getEntityId();
        assertNotNull("educationOrganization Id should not be null", edorgId);

        // check dashboard application authorized_ed_orgs field include new edorg Id
        Entity dashboardEntity = repo.findById(OnboardingResource.APPLICATION_RESOURCE_NAME, dashboardId);
        List<String> ids = (List<String>) dashboardEntity.getBody().get("authorized_ed_orgs");
        assertNotNull("dashboard application authorized_ed_orgs should not be null", ids);
        assertTrue("dashboard application authorized_ed_orgs should include new edorg Id", ids.contains(edorgId));

        // check databrowser application authorized_ed_orgs field include new edorg Id
        Entity databrowserEntity = repo.findById(OnboardingResource.APPLICATION_RESOURCE_NAME, databrowserId);
        ids = (List<String>) databrowserEntity.getBody().get("authorized_ed_orgs");
        assertNotNull("databrowser application authorized_ed_orgs should not be null", ids);
        assertTrue("databrowser application authorized_ed_orgs should include new edorg Id", ids.contains(edorgId));

        // check applicationAuthorization is created with authId is new edorg Id
        query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(OnboardingResource.AUTH_ID, NeutralCriteria.OPERATOR_EQUAL, edorgId));
        Entity appAuthEntity = repo.findOne(OnboardingResource.APPLICATION_AUTH_RESOURCE_NAME, query);
        assertNotNull("new applicationAuthorization should be created", appAuthEntity);

        // check the field "appIds" in applicationAuthorization include databrowser id and dashboard
        // id
        List<String> appIds = (List<String>) appAuthEntity.getBody().get(OnboardingResource.APP_IDS);
        assertTrue("the appIds field in applicationAuthorization should include databrowser Id",
                appIds.contains(databrowserId));
        assertTrue("the appIds field in applicationAuthorization should include dashboard Id",
                appIds.contains(dashboardId));

        // Attempt to create the same edorg.
        res = resource.provision(requestBody, null);
        assertTrue(Status.fromStatusCode(res.getStatus()) == Status.CONFLICT);
    }

    @Test
    public void testNotAuthorized() {
        injector.setEducatorContext();
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put(OnboardingResource.STATE_EDORG_ID, "TestOrg-NotAuthorized");
        requestBody.put(ResourceConstants.ENTITY_METADATA_TENANT_ID, "12345");
        Response res = resource.provision(requestBody, null);
        assertTrue(Status.fromStatusCode(res.getStatus()) == Status.FORBIDDEN);

    }

    private Map<String, Object> buildDashboardBody() {

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("version", "0.0");
        body.put("image_url", "http://placekitten.com/150/150");
        body.put("administration_url", "http://local.slidev.org:8888/dashboard/");
        body.put("application_url", "http://local.slidev.org:8888/dashboard/");
        body.put("client_secret", "evGN04xEHyZYYIz2fxiJkM1bY3JnGWq8O6dNcIOnw2wDXxkG");
        body.put("redirect_uri", "http://local.slidev.org:8888/dashboard/callback");
        body.put("description", "SLI dashboard application");
        body.put("name", "Dashboard");
        body.put("is_admin", false);
        body.put("enabled", true);
        body.put("bootstrap", true);
        body.put("client_id", "K2e7Dwhq5J");
        body.put("behavior", "Full Window App");
        Map<String, String> developerInfo = new HashMap<String, String>();
        developerInfo.put("organization", "SLC");
        body.put("developer_info", developerInfo);

        return body;
    }

    private Map<String, Object> buildDatabrowserBody() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("version", "0.0");
        body.put("image_url", "http://placekitten.com/150/150");
        body.put("administration_url", "http://local.slidev.org:3000/");
        body.put("application_url", "http://local.slidev.org:3000/");
        body.put("client_secret", "uOoKXLWihlz39EEQ7Uoqqc7TeogsnQnDAUs3HWYFouZFG5sk");
        body.put("redirect_uri", "http://local.slidev.org:3000/callback");
        body.put("description", "SLI Databrowser");
        body.put("name", "Databrowser");
        body.put("is_admin", true);
        body.put("enabled", true);
        body.put("bootstrap", true);
        body.put("client_id", "2zhRrEXh8r");
        body.put("behavior", "Iframe App");
        Map<String, String> developerInfo = new HashMap<String, String>();
        developerInfo.put("organization", "SLC");
        body.put("developer_info", developerInfo);
        return body;
    }

}
