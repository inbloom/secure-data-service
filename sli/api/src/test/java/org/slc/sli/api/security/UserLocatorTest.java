package org.slc.sli.api.security;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.mock.Mocker;
import org.slc.sli.api.security.resolve.impl.MongoUserLocator;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.dal.repository.EntityRepository;

/**
 * 
 * @author dkornishev
 * 
 */
@Ignore // Needs to be reworked with new querying structure/MockRepo
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class UserLocatorTest {
    
    @Autowired
    private MongoUserLocator locator;
    
    @Autowired
    private EntityRepository repo;
    
    @Before
    public void init() {
        // TODO need to put stateId in the record at top level?
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", Mocker.VALID_USER_ID);
        repo.create("teacher", body);
    }
    
    @Test
    public void testUserFound() {
        SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.VALID_USER_ID);
        
        Assert.assertNotNull(principal);
    }
    
    @Test
    public void testuserNotFound() {
        SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.INVALID_USER_ID);
        
        Assert.assertNull(principal);
    }
    
    @Test
    public void testGarbageInput() {
        SLIPrincipal principal = this.locator.locate(null, null);
        
        Assert.assertNull(principal);
    }
}
