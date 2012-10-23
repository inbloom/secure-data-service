/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
package org.slc.sli.api.search.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.web.client.HttpClientErrorException;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.enums.Right;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SearchResourceServiceTest {

    @Autowired
    SearchResourceService resourceService;


    @Test(expected = HttpClientErrorException.class)
    public void testNotEnoughToken() throws URISyntaxException {
        setupAuth(EntityNames.STAFF);
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=t");
        resourceService.doFilter(new ApiQuery(queryUri));
        Assert.fail("should be trown HttpClientErrorException");
    }

    @Test(expected = HttpClientErrorException.class)
    public void testNotEnoughTotalCharacters() throws URISyntaxException {
        setupAuth(EntityNames.STAFF);
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=a%20b");
        resourceService.doFilter(new ApiQuery(queryUri));
        Assert.fail("should be trown HttpClientErrorException");
    }

    @Test
    @Ignore
    public void testWithTeacher() throws URISyntaxException {
        setupAuth(EntityNames.TEACHER);
        Resource resource = new Resource("v1", "search");
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=David%20Wu");
        ServiceResponse serviceResponse = resourceService.list(resource, queryUri);
        Assert.assertNotNull(serviceResponse);
    }

    @Test(expected = HttpClientErrorException.class)
    public void testNotEnoughCharactersInToken() throws URISyntaxException {
        setupAuth(EntityNames.STAFF);
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=a%20b%20c");
        resourceService.doFilter(new ApiQuery(queryUri));
        Assert.fail("should be trown HttpClientErrorException");
    }

    @Test
    public void testNeutralCriteriaForNotES() throws URISyntaxException {
        setupAuth(EntityNames.STAFF);
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?abc=David%20Wu&efg=hij");
        ApiQuery apiQuery=new ApiQuery(queryUri);
        resourceService.doFilter(apiQuery);
        List<NeutralCriteria> criteria=apiQuery.getCriteria();
        Assert.assertEquals(2, criteria.size());
    }

    @Test
    public void testNeutralCriteriaForES() throws URISyntaxException {
        setupAuth(EntityNames.STAFF);
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=David%20Wu&abc=efg");
        ApiQuery apiQuery=new ApiQuery(queryUri);
        resourceService.doFilter(apiQuery);
        List<NeutralCriteria> criterias=apiQuery.getCriteria();
        Assert.assertEquals(1, criterias.size());
        NeutralCriteria criteria=criterias.get(0);
        Assert.assertEquals("david* wu*", criteria.getValue());
    }

    private static void setupAuth(String type) {
        Authentication mockAuth = Mockito.mock(Authentication.class);
        Entity entity = Mockito.mock(Entity.class);
        ArrayList<GrantedAuthority> rights = new ArrayList<GrantedAuthority>();
        rights.add(Right.ADMIN_ACCESS);
        rights.add(Right.EDORG_APP_AUTHZ);
        Mockito.when(mockAuth.getAuthorities()).thenReturn(rights);
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEdOrgId("fake");
        principal.setTenantId("IL");
        principal.setEntity(entity);

        Collection<GrantedAuthority> auths = new LinkedList<GrantedAuthority>();
        auths.add(Right.FULL_ACCESS);
        Mockito.when(mockAuth.getAuthorities()).thenReturn(auths);

        Mockito.when(mockAuth.getPrincipal()).thenReturn(principal);
        Mockito.when(entity.getType()).thenReturn(type);
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
    }

}
