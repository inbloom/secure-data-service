package org.slc.sli.api.security.roles;

import java.util.Arrays;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.security.resolve.impl.DefaultRolesToRightsResolver;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Tests default role to rights resolution pipeline
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class RolesToRightsTest {
    
    @Autowired
    private DefaultRolesToRightsResolver resolver;
    
    @Test
    public void testMappedRoles() throws Exception {
        
        Set<GrantedAuthority> rights = resolver.resolveRoles(Arrays.asList(DefaultRoles.EDUCATOR.getRoleName(), DefaultRoles.AGGREGATOR.getRoleName()));
        Assert.assertTrue(rights.size() > 0);
    }
    
    @Test
    public void testBadRoles() throws Exception {
        Set<GrantedAuthority> authorities = resolver.resolveRoles(Arrays.asList("Pink", "Goo"));
        Assert.assertTrue("Authorities must be empty", authorities.size() == 0);
    }
    
    @Test
    public void testMixedRoles() throws Exception {
        Set<GrantedAuthority> authorities = resolver.resolveRoles(Arrays.asList(DefaultRoles.EDUCATOR.getRoleName(), DefaultRoles.AGGREGATOR.getRoleName(), "bad", "doggie"));
        Assert.assertTrue(authorities.size() > 0);
    }
}
