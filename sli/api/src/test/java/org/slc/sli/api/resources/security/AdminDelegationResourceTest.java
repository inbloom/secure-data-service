package org.slc.sli.api.resources.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * AdminDelegationResource tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class AdminDelegationResourceTest {

    @Autowired
    private AdminDelegationResource resource;

    @Autowired
    private SecurityContextInjector securityContextInjector;

    @Test
    public void testGetDelegations() throws Exception {
        //securityContextInjector.setLeaAdminContext();
        //resource.getDelegations();
        //securityContextInjector.setSeaAdminContext();
        //resource.getDelegations();
        //securityContextInjector.setOperatorContext();
        //resource.getDelegations();
    }

    @Test
    public void testSetLocalDelegation() throws Exception {

    }
}
