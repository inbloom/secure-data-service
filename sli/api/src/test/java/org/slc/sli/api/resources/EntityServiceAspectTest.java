package org.slc.sli.api.resources;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.security.aspects.EntityServiceAspect;
import org.slc.sli.api.service.EntityService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Unit tests for Aspect on EntityService.
 * 
 * @author shalka
 */
public class EntityServiceAspectTest {
    
    private static Object returnObj;
    private EntityServiceAspect myAspect;
    
    private static final String ENTITY_TYPE_STUDENT = "student";
    private static final String ENTITY_TYPE_REALM = "realm";
    private static final String ROLE_SPRING_NAME_EDUCATOR = "ROLE_EDUCATOR";
    private static final String ASPECT_FUNCTION_GET = "get";
    private static final String ASPECT_FUNCTION_CREATE = "create";
    private static final String ASPECT_FUNCTION_GETDEFN = "getEntityDefinition";
    
    @Before
    public void init() {
        returnObj = new Object();
        myAspect = new EntityServiceAspect();
    }
    
    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testGetRealmCallAsEducator() throws Throwable {
        SecurityContextInjection.setEducatorContext();
        ProceedingJoinPoint pjp = mockProceedingJoinPoint(ASPECT_FUNCTION_GET, ENTITY_TYPE_REALM,
                ROLE_SPRING_NAME_EDUCATOR);
        
        Object resp = myAspect.controlAccess(pjp);
        Assert.assertEquals("Response doesn't match", returnObj, resp);
    }
    
    @Test
    public void testGetEntityDefnStudentCallAsEducator() throws Throwable {
        SecurityContextInjection.setEducatorContext();
        ProceedingJoinPoint pjp = mockProceedingJoinPoint(ASPECT_FUNCTION_GETDEFN, ENTITY_TYPE_STUDENT,
                ROLE_SPRING_NAME_EDUCATOR);
        
        Object resp = myAspect.controlAccess(pjp);
        Assert.assertEquals("Response doesn't match", returnObj, resp);
    }
    
    @Test
    public void testGetStudentCallAsEducator() throws Throwable {
        SecurityContextInjection.setEducatorContext();
        ProceedingJoinPoint pjp = mockProceedingJoinPoint(ASPECT_FUNCTION_GET, ENTITY_TYPE_STUDENT,
                ROLE_SPRING_NAME_EDUCATOR);
        
        Object resp = myAspect.controlAccess(pjp);
        Assert.assertEquals("Response doesn't match", returnObj, resp);
    }
    
    @Test(expected = AccessDeniedException.class)
    public void testCreateStudentCallAsEducator() throws Throwable {
        SecurityContextInjection.setEducatorContext();
        ProceedingJoinPoint pjp = mockProceedingJoinPoint(ASPECT_FUNCTION_CREATE, ENTITY_TYPE_STUDENT,
                ROLE_SPRING_NAME_EDUCATOR);
        
        @SuppressWarnings("unused")
        Object resp = myAspect.controlAccess(pjp);
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