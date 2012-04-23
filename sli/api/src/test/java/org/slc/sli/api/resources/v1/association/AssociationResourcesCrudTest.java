package org.slc.sli.api.resources.v1.association;

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
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.ResourceConstants;

/**
 * Unit tests for v1 association resources CRUD methods
 *
 * @author chung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class AssociationResourcesCrudTest {

    private String packageName;
    private String[] classesToTest;

    @Autowired
    EntityDefinitionStore entityDefs;

    @Autowired
    private SecurityContextInjector injector;

    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;

    @Before
    public void setup() throws Exception {
        packageName = this.getClass().getPackage().getName();
        classesToTest = getClassNames(packageName);

        uriInfo = ResourceTestUtil.buildMockUriInfo(null);

        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);

        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(httpHeaders.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
    }

    @Test
    public void testCreate() {
        for (String classToTest : classesToTest) {
            String resourceName = classToTest.replace(packageName + ".", "");
            Response response = getCreateResponse(classToTest, new EntityBody(ResourceTestUtil.createTestEntity(resourceName)));
            assertNotNull("Response is null", response);
            assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());
            assertNotNull("ID should not be null", ResourceTestUtil.parseIdFromLocation(response));
        }
    }

    @Test
    public void testRead() {
        for (String classToTest : classesToTest) {
            String resourceName = classToTest.replace(packageName + ".", "");
            String id = ResourceTestUtil.parseIdFromLocation(getCreateResponse(classToTest, new EntityBody(ResourceTestUtil.createTestEntity(resourceName))));
            Response response = getReadResponse(classToTest, id);
            assertNotNull("Response is null", response);
            Object responseEntityObj = response.getEntity();
            assertNotNull("Should return an entity", responseEntityObj);
            EntityResponse entityResponse = (EntityResponse) responseEntityObj;
            EntityBody body = (EntityBody) entityResponse.getEntity();
            assertEquals("field1 should be 1", body.get("field1"), "1");
            assertEquals("field2 should be 2", body.get("field2"), 2);
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
            getCreateResponse(classToTest, new EntityBody(ResourceTestUtil.createTestEntity(resourceName)));
            getCreateResponse(classToTest, new EntityBody(ResourceTestUtil.createTestSecondaryEntity(resourceName)));
            Response response = getReadAllResponse(classToTest);
            assertNotNull("Response is null", response);
            assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

            @SuppressWarnings("unchecked")
            EntityResponse entityResponse = (EntityResponse) response.getEntity();
            List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
            assertNotNull("Should return entities", results);
            assertTrue("Should have at least two entities", results.size() >= 2);
        }
    }

    private Response getCreateResponse(String classToTest, EntityBody newEntity) {
        @SuppressWarnings("rawtypes")
        Class[] paramTypes = { EntityBody.class, HttpHeaders.class, UriInfo.class };
        Object[] args = { newEntity, httpHeaders, uriInfo };

        return getResponse(classToTest, "create", paramTypes, args);
    }

    private Response getReadResponse(String classToTest, String id) {
        @SuppressWarnings("rawtypes")
        Class[] paramTypes = { String.class, HttpHeaders.class, UriInfo.class };
        Object[] args = { id, httpHeaders, uriInfo };

        return getResponse(classToTest, "read", paramTypes, args);
    }

    private Response getUpdateResponse(String classToTest) {
        String resourceName = classToTest.replace(packageName + ".", "");
        String id = ResourceTestUtil.parseIdFromLocation(getCreateResponse(classToTest, new EntityBody(ResourceTestUtil.createTestEntity(resourceName))));

        @SuppressWarnings("rawtypes")
        Class[] paramTypes = { String.class, EntityBody.class, HttpHeaders.class, UriInfo.class };
        Object[] args = { id, new EntityBody(ResourceTestUtil.createTestUpdateEntity(resourceName)), httpHeaders, uriInfo };
        Response response = getResponse(classToTest, "update", paramTypes, args);

        String resId = ResourceTestUtil.getResourceIdName(resourceName);
        EntityResponse entityResponse = (EntityResponse) getReadResponse(classToTest, id).getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();
        assertNotNull("Should return an entity", body);
        assertEquals("field1 should be 8", body.get("field1"), 8);
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
        assertEquals(resId + " should be 1234", body.get(resId), 1234);

        return response;
    }

    private Response getDeleteResponse(String classToTest) {
        String resourceName = classToTest.replace(packageName + ".", "");
        String id = ResourceTestUtil.parseIdFromLocation(getCreateResponse(classToTest, new EntityBody(ResourceTestUtil.createTestEntity(resourceName))));

        @SuppressWarnings("rawtypes")
        Class[] paramTypes = { String.class, HttpHeaders.class, UriInfo.class };
        Object[] args = { id, httpHeaders, uriInfo };
        Response response = getResponse(classToTest, "delete", paramTypes, args);

        try {
            @SuppressWarnings("unused")
            Response readResponse = getReadResponse(classToTest, id);
            fail("You should never see this - should have thrown EntityNotFoundException");
        } catch (EntityNotFoundException e) {
//            System.out.println("Entity not found after delete: " + resourceName + ", id=" + id);
            return response;
        }

        return response;
    }

    private Response getReadAllResponse(String classToTest) {
        @SuppressWarnings("rawtypes")
        Class[] paramTypes = { Integer.TYPE, Integer.TYPE, HttpHeaders.class, UriInfo.class };
        Object[] args = { 0, 100, httpHeaders, uriInfo };

        return getResponse(classToTest, "readAll", paramTypes, args);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Response getResponse(String classToTest, String methodName, Class[] paramTypes, Object[] args) {
        Response response = null;
        try {
            Class cls = Class.forName(classToTest);
            Constructor ct = cls.getConstructor(EntityDefinitionStore.class);
            Object instance = ct.newInstance(entityDefs);
            Method method = cls.getMethod(methodName, paramTypes);
            response = (Response) method.invoke(instance, args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            if (methodName.equals("read")) {
                throw new EntityNotFoundException();
            } else {
                e.printStackTrace();
            }
        }

        return response;
    }

    private String[] getClassNames(String packName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
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
}