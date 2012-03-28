package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceConstants;
import org.slc.sli.api.resources.v1.CrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for v1 entity resources CRUD methods
 * 
 * @author chung
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class EntityResourcesTest {

    private String packageName;
    private String[] classesToTest;
    
    @Autowired
    CrudEndpoint crudEndPoint;
    
    @Autowired
    private SecurityContextInjector injector;
    
    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;
    
    @Before
    public void setup() throws Exception {
        packageName = this.getClass().getPackage().getName();
        classesToTest = getClassNames(packageName);
        
        uriInfo = buildMockUriInfo(null);
        
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();
        
        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);
        
        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(httpHeaders.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
    }
    
    public Map<String, Object> createTestEntity(String resourceName) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", "1");
        entity.put("field2", 2);
        String resId = resourceName.substring(0, 1).toLowerCase() + resourceName.substring(1);
        resId = resId.replace("Resource", "Id");
        entity.put(resId, 1234);
        return entity;
    }
    
    public Map<String, Object> createTestUpdateEntity(String resourceName) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 8);
        entity.put("field2", 2);
        String resId = resourceName.substring(0, 1).toLowerCase() + resourceName.substring(1);
        resId = resId.replace("Resource", "Id");
        entity.put(resId, 1234);
        return entity;
    }
    
    public Map<String, Object> createTestSecondaryEntity(String resourceName) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 5);
        entity.put("field2", 6);
        String resId = resourceName.substring(0, 1).toLowerCase() + resourceName.substring(1);
        resId = resId.replace("Resource", "Id");
        entity.put(resId, 5678);
        return entity;
    }

    @Test
    public void testCreate() {
        for (String classToTest : classesToTest) {
            String resourceName = classToTest.replace(packageName + ".", "");
            Response response = getCreateResponse(classToTest, new EntityBody(createTestEntity(resourceName)));
            assertNotNull("Response is null", response);
            assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());
            assertNotNull("ID should not be null", parseIdFromLocation(response));
        }
    }
    
    @Test
    public void testRead() {
        for (String classToTest : classesToTest) {
            String resourceName = classToTest.replace(packageName + ".", "");
            String id = parseIdFromLocation(getCreateResponse(classToTest, new EntityBody(createTestEntity(resourceName))));
            Response response = getReadResponse(classToTest, id);
            assertNotNull("Response is null", response);
            Object responseEntityObj = response.getEntity();
            assertNotNull("Should return an entity", responseEntityObj);
        }
    }
    
    @Test
    public void testUpdate() {
        for (String classToTest : classesToTest) {
            Response response = getUpdateResponse(classToTest);
            assertNotNull("Response is null", response);
            assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());            
        }
    }
    
    @Test
    public void testDelete() {
        for (String classToTest : classesToTest) {    
            Response response = getDeleteResponse(classToTest);
            assertNotNull("Response is null", response);
            assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }
    
    @Test
    public void testReadAll() {
        for (String classToTest : classesToTest) {    
            String resourceName = classToTest.replace(packageName + ".", "");
            getCreateResponse(classToTest, new EntityBody(createTestEntity(resourceName)));
            getCreateResponse(classToTest, new EntityBody(createTestSecondaryEntity(resourceName)));
            Response response = getReadAllResponse(classToTest);
            assertNotNull("Response is null", response);
            assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());
            
            @SuppressWarnings("unchecked")
            List<EntityBody> results = (List<EntityBody>) response.getEntity();
            assertNotNull("Should return entities", results);
            assertTrue("Should have at least two entities", results.size() >= 2);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Response getCreateResponse(String classToTest, EntityBody newEntity) {
        Response response = null;
        try {
            Class cls = Class.forName(classToTest);
            Constructor ct = cls.getConstructor(CrudEndpoint.class);
            Object instance = ct.newInstance(crudEndPoint);
            
            Class[] paramTypes = { EntityBody.class, HttpHeaders.class, UriInfo.class };
            Method method = cls.getMethod("create", paramTypes);
            
            Object[] args = { newEntity, httpHeaders, uriInfo };
            response = (Response) method.invoke(instance, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Response getReadResponse(String classToTest, String id) {
        Response response = null;
        try {
            Class cls = Class.forName(classToTest);
            Constructor ct = cls.getConstructor(CrudEndpoint.class);
            Object instance = ct.newInstance(crudEndPoint);
            
            Class[] paramTypes = { String.class, HttpHeaders.class, UriInfo.class };
            Method method = cls.getMethod("read", paramTypes);

            Object[] args = { id, httpHeaders, uriInfo };
            response = (Response) method.invoke(instance, args);
        } catch (InvocationTargetException ite) {
            throw new EntityNotFoundException();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Response getUpdateResponse(String classToTest) {
        Response response = null;
        try {
            String resourceName = classToTest.replace(packageName + ".", "");
            String id = parseIdFromLocation(getCreateResponse(classToTest, new EntityBody(createTestEntity(resourceName))));
            
            Class cls = Class.forName(classToTest);
            Constructor ct = cls.getConstructor(CrudEndpoint.class);
            Object instance = ct.newInstance(crudEndPoint);
            
            Class[] paramTypes = { String.class, EntityBody.class, HttpHeaders.class, UriInfo.class };
            Method method = cls.getMethod("update", paramTypes);
            
            Object[] args = { id, new EntityBody(createTestUpdateEntity(resourceName)), httpHeaders, uriInfo };
            response = (Response) method.invoke(instance, args);

            String resId = resourceName.substring(0, 1).toLowerCase() + resourceName.substring(1);
            resId = resId.replace("Resource", "Id");
            EntityBody body = (EntityBody) getReadResponse(classToTest, id).getEntity();
            assertNotNull("Should return an entity", body);
            assertEquals("field1 should be 8", body.get("field1"), 8);
            assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
            assertEquals(resId + " should be 1234", body.get(resId), 1234);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Response getDeleteResponse(String classToTest) {
        Response response = null;
        try {
            String resourceName = classToTest.replace(packageName + ".", "");
            String id = parseIdFromLocation(getCreateResponse(classToTest, new EntityBody(createTestEntity(resourceName))));
            
            Class cls = Class.forName(classToTest);
            Constructor ct = cls.getConstructor(CrudEndpoint.class);
            Object instance = ct.newInstance(crudEndPoint);
            
            Class[] paramTypes = { String.class, HttpHeaders.class, UriInfo.class };
            Method method = cls.getMethod("delete", paramTypes);
            
            Object[] args = { id, httpHeaders, uriInfo };
            response = (Response) method.invoke(instance, args);

            try {
                @SuppressWarnings("unused")
                Response readResponse = getReadResponse(classToTest, id);
                fail("You should never see this - should have thrown EntityNotFoundException");
            } catch (EntityNotFoundException enfe) {
                System.out.println("Entity not found after delete: " + resourceName + ", id=" + id);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Response getReadAllResponse(String classToTest) {
        Response response = null;
        try {
            Class cls = Class.forName(classToTest);
            Constructor ct = cls.getConstructor(CrudEndpoint.class);
            Object instance = ct.newInstance(crudEndPoint);
            
            Class[] paramTypes = { Integer.TYPE, Integer.TYPE, HttpHeaders.class, UriInfo.class };
            Method method = cls.getMethod("readAll", paramTypes);

            Object[] args = { 0, 100, httpHeaders, uriInfo };
            response = (Response) method.invoke(instance, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private String[] getClassNames(String packName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<String> classes = new ArrayList<String>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packName));
        }
        return classes.toArray(new String[classes.size()]);
    }

    private List<String> findClasses(File directory, String packName) throws ClassNotFoundException {
        List<String> classes = new ArrayList<String>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packName + "." + file.getName()));
            } else if (!file.getName().contains("Test") && !file.getName().contains("$")
                    && file.getName().endsWith(".class")) {
                classes.add(packName + "." + file.getName().substring(0, file.getName().length() - 6));
            }
        }
        return classes;
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
        when(mock.getQueryParameters(true)).thenAnswer(new Answer<MultivaluedMap>() {
            @Override
            public MultivaluedMap answer(InvocationOnMock invocationOnMock) throws Throwable {
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