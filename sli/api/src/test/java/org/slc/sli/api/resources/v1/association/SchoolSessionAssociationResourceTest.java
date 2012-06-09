package org.slc.sli.api.resources.v1.association;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.entity.SchoolResource;
import org.slc.sli.api.resources.v1.entity.SessionResource;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * JUnit tests for SchoolSessionAssociationResource
 * @author chung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SchoolSessionAssociationResourceTest {

    private final String schoolResourceName = "SchoolResource";
    private final String sessionResourceName = "SessionResource";
    private final String schoolSessionAssociationResourceName = "SchoolSessionAssociationResource";

    @Autowired
    private SecurityContextInjector injector;
    @Autowired
    private SchoolResource schoolResource;
    @Autowired
    private SessionResource sessionResource;
    @Autowired
    private SchoolSessionAssociationResource schoolSessionAssociationResource;

    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;

    @Before
    public void setup() throws Exception {
        uriInfo = ResourceTestUtil.buildMockUriInfo(null);

        // inject administrator security context for unit testing
        injector.setAccessAllAdminContextWithElevatedRights();

        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);

        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(httpHeaders.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
    }

    @Test
    public void testGetSessions() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = sessionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sessionResourceName)), httpHeaders, uriInfo);
        String sessionId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                schoolSessionAssociationResourceName, schoolResourceName, schoolId, sessionResourceName, sessionId);
        createResponse = schoolSessionAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = schoolSessionAssociationResource.getSessions(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "session", body.get("entityType"));
        assertEquals("ID should match", sessionId, body.get("id"));
    }

    @Test
    public void testGetSchools() {
        Response createResponse = schoolResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(schoolResourceName)), httpHeaders, uriInfo);
        String schoolId = ResourceTestUtil.parseIdFromLocation(createResponse);
        createResponse = sessionResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(sessionResourceName)), httpHeaders, uriInfo);
        String sessionId = ResourceTestUtil.parseIdFromLocation(createResponse);

        Map<String, Object> map = ResourceTestUtil.createTestAssociationEntity(
                schoolSessionAssociationResourceName, schoolResourceName, schoolId, sessionResourceName, sessionId);
        createResponse = schoolSessionAssociationResource.create(new EntityBody(map), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        Response response = schoolSessionAssociationResource.getSchools(id, 0, 100, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals("Entity type should match", "school", body.get("entityType"));
        assertEquals("ID should match", schoolId, body.get("id"));
    }
}
