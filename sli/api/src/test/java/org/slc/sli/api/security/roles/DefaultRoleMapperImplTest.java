package org.slc.sli.api.security.roles;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * Test class to test the basic role mapper used for default roles.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultRoleMapperImplTest {
    

    private DefaultRoleMapperImpl mapper;
    private List<String> incomingRoles;

    @Before
    public void setUp() throws Exception {
        incomingRoles = new ArrayList<String>();

    }

    @After
    public void tearDown() throws Exception {
        incomingRoles.clear();

    }

    @Test
    public void testBuildMappedRoles() throws Exception {
        incomingRoles.add(DefaultRoles.EDUCATOR.getRoleName());
        incomingRoles.add(DefaultRoles.AGGREGATOR.getRoleName());
        mapper = new DefaultRoleMapperImpl(incomingRoles);
        
        List<GrantedAuthorityImpl> finalRoles = mapper.buildMappedRoles();
        assertTrue(finalRoles.size() == 2);

    }

    @Test
    public void testBuildMappedRolesWithBadRoles() throws Exception {
        incomingRoles.add("Waffles");
        incomingRoles.add("Peanuts");
        mapper = new DefaultRoleMapperImpl(incomingRoles);
        
        List<GrantedAuthorityImpl> finalRoles = mapper.buildMappedRoles();
        assertTrue(finalRoles.size() == 0);
    }

    @Test
    public void testBuildMappedRolesWithMixture() throws Exception {
        incomingRoles.add("Waffles");
        incomingRoles.add(DefaultRoles.ADMINISTRATOR.getRoleName());
        incomingRoles.add("Peanuts");
        incomingRoles.add(DefaultRoles.AGGREGATOR.getRoleName());
        mapper = new DefaultRoleMapperImpl(incomingRoles);
        List<GrantedAuthorityImpl> finalRoles = mapper.buildMappedRoles();
        
        assertTrue(finalRoles.size() == 2);
    }
}
