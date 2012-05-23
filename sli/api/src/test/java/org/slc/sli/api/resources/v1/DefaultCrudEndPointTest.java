package org.slc.sli.api.resources.v1;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.v1.entity.StudentResource;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderFactory;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.client.constants.ResourceConstants;
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.PathConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Unit tests for the default crud endpoint
 *
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultCrudEndPointTest {

    @Autowired
    private StudentResource crudEndPoint; // class under test

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private EntityDefinitionStore entityDefs;

    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;
    private List<String> resourceList = new ArrayList<String>();

    @Before
    public void setup() throws Exception {
        uriInfo = buildMockUriInfo(null);

        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);

        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);

        // expand this list
        resourceList.add(ResourceNames.SCHOOLS);
    }

    public Map<String, Object> createTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", "1");
        entity.put("field2", 2);
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    public Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 8);
        entity.put("field2", 2);
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    public Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 5);
        entity.put("field2", 6);
        entity.put("studentUniqueStateId", 5678);
        return entity;
    }

    @Test
    public void testCreate() {
        for (String resource : resourceList) {
            Response response = crudEndPoint.create(resource, new EntityBody(createTestEntity()), httpHeaders, uriInfo);
            assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());
            assertNotNull("ID should not be null", parseIdFromLocation(response));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReadMultipleResources() {
        for (String resource : resourceList) {
            String idList = getIDList(resource);
            Response response = crudEndPoint.read(resource, idList, httpHeaders, uriInfo);
            assertEquals("Status code should be 200", Status.OK.getStatusCode(), response.getStatus());

            EntityResponse entityResponse = (EntityResponse) response.getEntity();
            List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
            assertEquals("Should get 2 entities", results.size(), 2);

            EntityBody body1 = results.get(0);
            assertNotNull("Should not be null", body1);
            assertEquals("studentUniqueStateId should be 1234", body1.get("studentUniqueStateId"), 1234);
            assertNotNull("Should include links", body1.get(ResourceConstants.LINKS));

            EntityBody body2 = results.get(1);
            assertNotNull("Should not be null", body2);
            assertEquals("studentUniqueStateId should be 5678", body2.get("studentUniqueStateId"), 5678);
            assertNotNull("Should include links", body2.get(ResourceConstants.LINKS));
        }
    }

    @Test
    public void testCustomEntityLink() {
        for (String resource : resourceList) {
            String idList = getIDList(resource);
            Response response = crudEndPoint.read(resource, idList, httpHeaders, uriInfo);
            EntityResponse entityResponse = (EntityResponse) response.getEntity();
            List<?> results = (List<?>) entityResponse.getEntity();
            for (Object result : results) {
                EntityBody entity = (EntityBody) result;
                List<?> links = (List<?>) entity.get(ResourceConstants.LINKS);
                String customLink = null;
                String selfLink = null;
                for (Object linkObject : links) {
                    EmbeddedLink link = (EmbeddedLink) linkObject;
                    if (link.getRel().equals(ResourceConstants.SELF)) {
                        selfLink = link.getHref();
                    } else if (link.getRel().equals(ResourceConstants.CUSTOM)) {
                        customLink = link.getHref();
                    }
                }
                assertNotNull(selfLink);
                assertNotNull(customLink);
                assertEquals(selfLink + "/" + PathConstants.CUSTOM_ENTITIES, customLink);
            }

        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReadResourceFromKey() {
        for (String resource : resourceList) {
            // create one entity
            crudEndPoint.create(resource, new EntityBody(createTestEntity()), httpHeaders, uriInfo);
            Response response = crudEndPoint.read(resource, "field1", "1", httpHeaders, uriInfo);

            EntityResponse entityResponse = (EntityResponse) response.getEntity();
            List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
            assertTrue("Should have at least one entity", results.size() > 0);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDelete() {
        for (String resource : resourceList) {
            // create one entity
            Response createResponse = crudEndPoint.create(resource, new EntityBody(createTestEntity()), httpHeaders,
                    uriInfo);
            String id = parseIdFromLocation(createResponse);

            // delete it
            Response response = crudEndPoint.delete(resource, id, httpHeaders, uriInfo);
            assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

            try {
                crudEndPoint.read(resource, id, httpHeaders, uriInfo);
                fail("should have thrown EntityNotFoundException");
            } catch (EntityNotFoundException e) {
                return;
            } catch (Exception e) {
                fail("threw wrong exception: " + e);
            }
        }
    }

    @Test
    public void testUpdate() {
        for (String resource : resourceList) {
            // create one entity
            Response createResponse = crudEndPoint.create(resource, new EntityBody(createTestEntity()), httpHeaders,
                    uriInfo);
            String id = parseIdFromLocation(createResponse);

            // update it
            Response response = crudEndPoint.update(resource, id, new EntityBody(createTestUpdateEntity()),
                    httpHeaders, uriInfo);
            assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

            // try to get it
            Response getResponse = crudEndPoint.read(resource, id, httpHeaders, uriInfo);
            assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());
            EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
            EntityBody body = (EntityBody) entityResponse.getEntity();
            assertNotNull("Should return an entity", body);
            assertEquals("studentUniqueStateId should be 1234", body.get("studentUniqueStateId"), 1234);
            assertEquals("studentUniqueStateId should be 8", body.get("field1"), 8);
            assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReadAll() {
        for (String resource : resourceList) {
            // create one entity
            crudEndPoint.create(resource, new EntityBody(createTestEntity()), httpHeaders, uriInfo);

            // read everything
            Response response = crudEndPoint.readAll(resource, httpHeaders, uriInfo);
            assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

            EntityResponse entityResponse = (EntityResponse) response.getEntity();
            List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
            assertNotNull("Should return an entity", results);
            assertTrue("Should have at least one entity", results.size() > 0);
        }
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testAppendOptionalFieldsNoOptionsGiven() {
        UriInfo info = mock(UriInfo.class);
        MultivaluedMap map = new MultivaluedMapImpl();
        when(info.getQueryParameters(true)).thenReturn(map);

        EntityBody body = new EntityBody();
        body.put("student", "{\"somekey\":\"somevalue\"}");

        List<EntityBody> entities = new ArrayList<EntityBody>();
        entities.add(body);

        entities = crudEndPoint.appendOptionalFields(info, entities, ResourceNames.SECTIONS);

        assertEquals("Should only have one", 1, entities.size());
        assertEquals("Should match", body, entities.get(0));
    }

    @Test
    public void testExtractOptionalFieldParams() {
        Map<String, String> values = crudEndPoint.extractOptionalFieldParams("attendances.1");
        assertEquals("Should match", "attendances", values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));
        assertEquals("Should match", "1", values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));

        values = crudEndPoint.extractOptionalFieldParams("attendances");
        assertEquals("Should match", "attendances", values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));
        assertEquals("Should match", null, values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));

        values = crudEndPoint.extractOptionalFieldParams("attendances.1.2.3");
        assertEquals("Should match", "attendances", values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));
        assertEquals("Should match", "1", values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));

        values = crudEndPoint.extractOptionalFieldParams("attendances%1");
        assertEquals("Should match", "attendances%1", values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));
        assertEquals("Should match", null, values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));

        values = crudEndPoint.extractOptionalFieldParams("attendances.someparam");
        assertEquals("Should match", "attendances", values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));
        assertEquals("Should match", "someparam", values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));
    }

    @Test
    public void testGettingTotalCountDoesNotCorruptNeutralQuery() {

        NeutralQuery neutralQuery1 = new NeutralQuery();
        neutralQuery1.setIncludeFields("field1,field2");
        neutralQuery1.setExcludeFields("field3,field4");
        neutralQuery1.setLimit(5);
        neutralQuery1.setOffset(4);
        neutralQuery1.setSortBy("field5");
        neutralQuery1.setSortOrder(NeutralQuery.SortOrder.ascending);
        neutralQuery1.addCriteria(new NeutralCriteria("x=1"));

        NeutralQuery neutralQuery2 = new NeutralQuery(neutralQuery1);

        EntityService mock = mock(EntityService.class);
        when(mock.count(any(NeutralQuery.class))).thenReturn(0L);
        DefaultCrudEndpoint.getTotalCount(mock, neutralQuery1);

        assertEquals(neutralQuery1, neutralQuery2);
    }

    @Test
    public void testAddTypeCriteria() {
        EntityDefinition def = entityDefs.lookupByResourceName(ResourceNames.TEACHERS);
        NeutralQuery query = new NeutralQuery();

        query = crudEndPoint.addTypeCriteria(def, query);

        List<NeutralCriteria> criteriaList = query.getCriteria();
        assertEquals("Should match", 1, criteriaList.size());

        NeutralCriteria criteria = criteriaList.get(0);
        assertEquals("Should match", "type", criteria.getKey());
        assertEquals("Should match", NeutralCriteria.CRITERIA_IN, criteria.getOperator());
        assertEquals("Should match", Arrays.asList(def.getType()), criteria.getValue());
    }

    @Test
    public void testAddTypeCriteriaNoChange() {
        EntityDefinition def = entityDefs.lookupByResourceName(ResourceNames.STAFF);
        NeutralQuery query = new NeutralQuery();

        query = crudEndPoint.addTypeCriteria(def, query);

        List<NeutralCriteria> criteriaList = query.getCriteria();
        assertEquals("Should match", 0, criteriaList.size());
    }

    @Test
    public void testAddTypeCriteriaNullValues() {
        EntityDefinition def = entityDefs.lookupByResourceName(ResourceNames.STAFF);
        NeutralQuery query = null;

        assertNull("Should be null", crudEndPoint.addTypeCriteria(null, null));

        query = new NeutralQuery();
        query = crudEndPoint.addTypeCriteria(null, query);
        List<NeutralCriteria> criteriaList = query.getCriteria();
        assertEquals("Should match", 0, criteriaList.size());
    }

    private String getIDList(String resource) {
        // create one more resource
        Response createResponse1 = crudEndPoint.create(resource, new EntityBody(createTestEntity()), httpHeaders,
                uriInfo);
        Response createResponse2 = crudEndPoint.create(resource, new EntityBody(createTestSecondaryEntity()),
                httpHeaders, uriInfo);

        return parseIdFromLocation(createResponse1) + "," + parseIdFromLocation(createResponse2);
    }

    public UriInfo buildMockUriInfo(final String queryString) throws Exception {
        UriInfo mock = mock(UriInfo.class);
        when(mock.getAbsolutePathBuilder()).thenAnswer(new Answer<UriBuilder>() {

            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("absolute");
            }
        });
        when(mock.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {

            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("base");
            }
        });
        when(mock.getRequestUriBuilder()).thenAnswer(new Answer<UriBuilder>() {

            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("request");
            }
        });
        when(mock.getQueryParameters(true)).thenAnswer(new Answer<MultivaluedMap<String, String>>() {
            @Override
            public MultivaluedMap<String, String> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new MultivaluedMapImpl();
            }
        });

        when(mock.getRequestUri()).thenReturn(new UriBuilderImpl().replaceQuery(queryString).build(new Object[] {}));
        return mock;
    }

    private static String parseIdFromLocation(Response response) {
        List<Object> locationHeaders = response.getMetadata().get("Location");
        assertNotNull(locationHeaders);
        assertEquals(1, locationHeaders.size());
        Pattern regex = Pattern.compile(".+/([\\w-]+)$");
        Matcher matcher = regex.matcher((String) locationHeaders.get(0));
        matcher.find();
        assertEquals(1, matcher.groupCount());
        return matcher.group(1);
    }
}
