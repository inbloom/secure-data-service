package org.slc.sli.api.resources.security;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.Response;

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

    @Test(expected = InsufficientAuthenticationException.class)
    public void testGetDelegationsNoEdOrg() throws Exception {

        securityContextInjector.setLeaAdminContext();
        resource.getDelegations();

    }

    @Test
    public void testGetDelegationsBadRole() throws Exception {

        securityContextInjector.setEducatorContext();
        Assert.assertEquals(resource.getDelegations().getStatus(), Response.Status.FORBIDDEN.getStatusCode());

    }

    @Test
    public void testGetSingleDelegation() throws Exception {

        securityContextInjector.setLeaAdminContext();
        ((SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setEdOrg("1234");

        Assert.assertEquals(resource.getSingleDelegation().getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testSetLocalDelegationNoEdOrg() throws Exception {

        securityContextInjector.setLeaAdminContext();
        Assert.assertEquals(resource.setLocalDelegation(null).getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

    }

    @Test
    public void testSetLocalDelegationBadRole() throws Exception {

        securityContextInjector.setEducatorContext();
        Assert.assertEquals(resource.setLocalDelegation(null).getStatus(), Response.Status.FORBIDDEN.getStatusCode());

    }

    @Test
    public void testSetLocalDelegation() throws Exception {

        securityContextInjector.setLeaAdminContext();

        ((SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setEdOrg("1234");

        EntityBody body = new EntityBody();
        body.put(resource.LEA_ID, "1234");

        Assert.assertEquals(resource.setLocalDelegation(body).getStatus(), Response.Status.CREATED.getStatusCode());

    }
}
