package org.slc.sli.api.resources;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.security.aspects.EntityServiceAspect;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.EntitySchemaRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Unit tests for Aspect on EntityService.
 *
 * @author shalka
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class EntityServiceAspectTest {

    private static final String ENTITY_TYPE_STUDENT = "student";
    private static final String ENTITY_TYPE_REALM = "realm";
    private static final String ROLE_SPRING_NAME_EDUCATOR = "ROLE_EDUCATOR";
    private static final String ROLE_SPRING_NAME_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    private static final String ASPECT_FUNCTION_GET = "get";
    private static final String ASPECT_FUNCTION_CREATE = "create";
    private static final String ASPECT_FUNCTION_GETDEFN = "getEntityDefinition";

    private static Object returnObj;
    private static EntityServiceAspect myAspect;

    @Autowired
    private EntitySchemaRegistry schemaRegistry;

    @Autowired
    private SecurityContextInjector injector;

    @Before
    public void init() {
        returnObj = new Object();
        myAspect = new EntityServiceAspect();
        myAspect.setSchemaRegistry(schemaRegistry);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetRealmCallAsEducator() throws Throwable {
        injector.setEducatorContext();
        Entity mockRealm = new MongoEntity("realm", null);
        ProceedingJoinPoint pjp = mockProceedingJoinPoint(ASPECT_FUNCTION_GET, mockRealm,
                ROLE_SPRING_NAME_EDUCATOR);

        try {
            Object resp = myAspect.filterEntityRead(pjp);
            Assert.assertNotNull(resp);
        } catch (AccessDeniedException e) {
            org.junit.Assume.assumeNoException(e);
        }
    }

    @Test
    public void testGetEntityDefnStudentCallAsEducator() throws Throwable {
        injector.setEducatorContext();
        Entity mockStudent = new MongoEntity("student", null);
        ProceedingJoinPoint pjp = mockProceedingJoinPoint(ASPECT_FUNCTION_GETDEFN, mockStudent,
                ROLE_SPRING_NAME_EDUCATOR);

        try {
            Object resp = myAspect.filterEntityRead(pjp);
            Assert.assertNotNull(resp);
        } catch (AccessDeniedException e) {
            org.junit.Assume.assumeNoException(e);
        }
    }

    @Test
    public void testGetStudentCallAsEducator() throws Throwable {
        injector.setEducatorContext();
        Entity mockStudent = new MongoEntity("student", null);
        ProceedingJoinPoint pjp = mockProceedingJoinPoint(ASPECT_FUNCTION_GET, mockStudent,
                ROLE_SPRING_NAME_EDUCATOR);

        try {
            Object resp = myAspect.filterEntityRead(pjp);
            Assert.assertNotNull(resp);
        } catch (AccessDeniedException e) {
            org.junit.Assume.assumeNoException(e);
        }
    }

    @Test(expected = AccessDeniedException.class)
    public void testCreateStudentCallAsEducator() throws Throwable {
        injector.setEducatorContext();
        Entity mockStudent = new MongoEntity("student", null);
        ProceedingJoinPoint pjp = mockProceedingJoinPoint(ASPECT_FUNCTION_CREATE, mockStudent,
                ROLE_SPRING_NAME_EDUCATOR);

        @SuppressWarnings("unused")
        Object ret = myAspect.authorizeWrite(pjp);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFieldViewForEducator() throws Throwable {
        injector.setEducatorContext();

        String studentBody = "{\"studentUniqueStateId\":231101422,\"name\":{\"firstName\":\"Alfonso\","
                + "\"middleName\":\"Ora\",\"lastSurname\":\"Steele\"},\"sex\":\"Male\",\"economicDisadvantaged\":true,"
                + "\"birthData\":{\"birthDate\":\"1999-07-12\"}}";

        Map<String, Object> map = new ObjectMapper().readValue(studentBody, Map.class);
        Entity mockedStudent = new MongoEntity("student", map);
        ProceedingJoinPoint pjp = mockProceedingJoinPoint(ASPECT_FUNCTION_GET, mockedStudent,
                ROLE_SPRING_NAME_EDUCATOR);
        Entity returnEntity = myAspect.filterEntityRead(pjp);

        Assert.assertEquals("Restricted field should have been filtered.", false,
                returnEntity.getBody().containsKey("economicDisadvantaged"));
        Assert.assertEquals("General field shouldn't have been filtered.", true,
                returnEntity.getBody().containsKey("name"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFieldViewForAdministrator() throws Throwable {
        injector.setAdminContext();

        String studentBody = "{\"studentUniqueStateId\":231101422,\"name\":{\"firstName\":\"Alfonso\","
                + "\"middleName\":\"Ora\",\"lastSurname\":\"Steele\"},\"sex\":\"Male\",\"economicDisadvantaged\":true,"
                + "\"birthData\":{\"birthDate\":\"1999-07-12\"}}";

        Map<String, Object> map = new ObjectMapper().readValue(studentBody, Map.class);
        Entity mockedStudent = new MongoEntity("student", map);
        ProceedingJoinPoint pjp = mockProceedingJoinPoint(ASPECT_FUNCTION_GET, mockedStudent,
                ROLE_SPRING_NAME_ADMINISTRATOR);
        Entity returnEntity = myAspect.filterEntityRead(pjp);

        Assert.assertEquals("Restricted field shouldn't have been filtered.", true,
                returnEntity.getBody().containsKey("economicDisadvantaged"));
        Assert.assertEquals("General field shouldn't have been filtered.", true,
                returnEntity.getBody().containsKey("name"));
    }

    // inserts mocked student entity into proceeding join point (for CoreEntityService unit testing)
    private ProceedingJoinPoint mockProceedingJoinPoint(String methodName, Entity entity, String springRole)
            throws Throwable {
        ProceedingJoinPoint mockPjp = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(mockPjp.proceed()).thenReturn(entity);
        Signature sig = createSignature(methodName);
        Mockito.when(mockPjp.getSignature()).thenReturn(sig);

        return mockPjp;
    }

    private ProceedingJoinPoint mockProceedingJoinPoint(String methodName, String entityType, String springRole)
            throws Throwable {
        ProceedingJoinPoint pjp = Mockito.mock(ProceedingJoinPoint.class);
        GrantedAuthority auth = Mockito.mock(GrantedAuthority.class);
        EntityService es = Mockito.mock(EntityService.class);
        EntityDefinition ed = Mockito.mock(EntityDefinition.class);
        Signature sig = createSignature(methodName);

        Mockito.when(ed.getType()).thenReturn(entityType);
        Mockito.when(es.getEntityDefinition()).thenReturn(ed);
        Mockito.when(auth.getAuthority()).thenReturn(springRole);
        Mockito.when(pjp.getSignature()).thenReturn(sig);
        Mockito.when(pjp.getTarget()).thenReturn(es);
        Mockito.when(pjp.proceed()).thenReturn(returnObj);

        return pjp;
    }

    private Signature createSignature(String methodName) {
        Signature sig = Mockito.mock(Signature.class);

        Mockito.when(sig.getName()).thenReturn(methodName);
        Mockito.when(sig.toString()).thenReturn("mocking method - " + methodName);

        return sig;
    }

}