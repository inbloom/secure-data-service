/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.resources.security;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Test(expected = EntityNotFoundException.class)
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
        ((SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setEdOrgId("1234");


        Assert.assertEquals(resource.getSingleDelegation().getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testSetLocalDelegationNoEdOrg() throws Exception {

        securityContextInjector.setLeaAdminContext();
        UriInfo uriInfo = ResourceTestUtil.buildMockUriInfo("");
        Assert.assertEquals(resource.setLocalDelegation(null, uriInfo).getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

    }

    @Test
    public void testSetLocalDelegation() throws Exception {

        securityContextInjector.setLeaAdminContext();

        ((SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setEdOrg("1234");
        ((SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setEdOrgId("1234");

        EntityBody body = new EntityBody();
        body.put(resource.LEA_ID, "1234");
        UriInfo uriInfo = ResourceTestUtil.buildMockUriInfo("");
        Assert.assertEquals(Response.Status.CREATED.getStatusCode(), resource.setLocalDelegation(body, uriInfo).getStatus());

    }
}
