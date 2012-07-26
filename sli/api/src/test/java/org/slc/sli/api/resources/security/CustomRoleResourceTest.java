package org.slc.sli.api.resources.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for CustomRoleResource
 * @author jnanney
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class CustomRoleResourceTest {
    
    @Before
    public void setUp() {
    }

    @Test
    public void testValidCreate() {
        
    }

    @Test
    public void testValidUpdate() {
        
    }
    
    @Test
    public void testReadAll() {
        
    }
    
    @Test
    public void testReadAccessible() {
    }
    
    @Test
    public void testReadInaccessible() {
        
    }
    
    @Test
    public void testUpdateWithDuplicateRoles() {
        
    }
    
    @Test
    public void testUpdateWithInvalidRight() {
        
    }
    
    @Test
    public void testUpdateWithInvalidRealmId() {
        
    }
    
    @Test
    public void testUpdateRealmId() {
        
    }
    
    @Test
    public void testCreateWithDuplicateRoles() {
        
    }
    
    @Test
    public void testCreateWithInvalidRight() {
        
    }
    
    @Test
    public void testCreateWithInvalidRealmId() {
        
    }
    
    @Test
    public void testCreateDuplicate() {
        
    }

}
