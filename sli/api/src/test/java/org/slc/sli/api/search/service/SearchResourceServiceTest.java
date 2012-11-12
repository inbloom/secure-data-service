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
import java.util.Arrays;
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

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.PreConditionFailedException;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.enums.Right;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
public class SearchResourceServiceTest {

    @Autowired
    SearchResourceService resourceService;

    @Test(expected = HttpClientErrorException.class)
    @Ignore
    public void testNotEnoughToken() throws URISyntaxException {
        setupAuth(EntityNames.STAFF);
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=t");
        resourceService.filterCriteria(new ApiQuery(queryUri));
        Assert.fail("should be trown HttpClientErrorException");
    }

    @Test(expected = HttpClientErrorException.class)
    @Ignore
    public void testNotEnoughTotalCharacters() throws URISyntaxException {
        setupAuth(EntityNames.STAFF);
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=a%20b");
        resourceService.filterCriteria(new ApiQuery(queryUri));
        Assert.fail("should be trown HttpClientErrorException");
    }

    @Test
    @Ignore
    public void testWithTeacher() throws URISyntaxException {
        setupAuth(EntityNames.TEACHER);
        Resource resource = new Resource("v1", "search");
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=David%20Wu");
        ServiceResponse serviceResponse = resourceService.list(resource, null, queryUri);
        Assert.assertNotNull(serviceResponse);
    }

    @Test
    public void testNeutralCriteriaForNotES() throws URISyntaxException {
        setupAuth(EntityNames.STAFF);
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?abc=David%20Wu&efg=hij");
        ApiQuery apiQuery = new ApiQuery(queryUri);
        resourceService.filterCriteria(apiQuery);
        List<NeutralCriteria> criteria = apiQuery.getCriteria();
        Assert.assertEquals(2, criteria.size());
    }

    @Test
    public void testNeutralCriteriaForES() throws URISyntaxException {
        setupAuth(EntityNames.STAFF);
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=David%20Wu&abc=efg");
        ApiQuery apiQuery = new ApiQuery(queryUri);
        resourceService.filterCriteria(apiQuery);
        List<NeutralCriteria> criterias = apiQuery.getCriteria();
        Assert.assertEquals(1, criterias.size());
        NeutralCriteria criteria = criterias.get(0);
        Assert.assertEquals("david* wu*", criteria.getValue());
    }

    @Test(expected = PreConditionFailedException.class)
    public void testSearchLimits() throws URISyntaxException {
        setupAuth(EntityNames.STAFF);
        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=David%20Wu&offset=0&limit=300");
        ApiQuery apiQuery = new ApiQuery(queryUri);
        resourceService.retrieveResults(apiQuery);
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
        TenantContext.setTenantId("IL");

        Collection<GrantedAuthority> auths = new LinkedList<GrantedAuthority>();
        auths.add(Right.FULL_ACCESS);
        Mockito.when(mockAuth.getAuthorities()).thenReturn(auths);
        Mockito.when(mockAuth.getPrincipal()).thenReturn(principal);
        Mockito.when(entity.getType()).thenReturn(type);
        Mockito.when(entity.getEntityId()).thenReturn("id");
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
    }

    @Test
    public void testCheckAccessibleAsStaff() {

        setupAuth(EntityNames.STAFF);
        SearchResourceService rs = Mockito.spy(resourceService);

        // for staff, list of entities should not change
        Mockito.doReturn(true).when(rs).isAccessible(Mockito.anyString(), Mockito.anyString());
        List<EntityBody> result = rs.filterResultsBySecurity(getEntities());
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void testCheckAccessibleAsTeacher() {

        setupAuth(EntityNames.TEACHER);

        // test varied accessibility
        SearchResourceService rs = Mockito.spy(resourceService);
        Mockito.doReturn(true).when(rs).isAccessible("student", "1");
        Mockito.doReturn(false).when(rs).isAccessible("student", "2");
        Mockito.doReturn(true).when(rs).isAccessible("student", "5");
        Mockito.doReturn(false).when(rs).isAccessible("section", "1");
        Mockito.doReturn(true).when(rs).isAccessible("section", "3");
        Mockito.doReturn(true).when(rs).isAccessible("someRandomType", "1");
        List<EntityBody> result = rs.filterResultsBySecurity(getEntities());
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("1", result.get(0).get("id"));
        Assert.assertEquals("3", result.get(1).get("id"));

        // test when all entities are inaccessible
        Mockito.doReturn(false).when(rs).isAccessible(Mockito.anyString(), Mockito.anyString());
        result = rs.filterResultsBySecurity(getEntities());
        Assert.assertEquals(0, result.size());

    }

    private List<EntityBody> getEntities() {
        // set up entities
        List<EntityBody> entities = new ArrayList<EntityBody>();
        EntityBody e1 = new EntityBody();
        e1.put("id", "1");
        e1.put("type", "student");
        EntityBody e2 = new EntityBody();
        e2.put("id", "2");
        e2.put("type", "student");
        EntityBody e3 = new EntityBody();
        e3.put("id", "3");
        e3.put("type", "section");
        entities.add(e1);
        entities.add(e2);
        entities.add(e3);
        return entities;
    }

    @Test
    public void testPagination() throws URISyntaxException {

        setupAuth(EntityNames.TEACHER);

        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=Anna&offset=0&limit=10");
        runPaginationTest(Arrays.asList(20, 20, 20), 4, queryUri, 10);
        runPaginationTest(Arrays.asList(8), 6, queryUri, 6);
        runPaginationTest(Arrays.asList(20, 8), 4, queryUri, 8);
        runPaginationTest(Arrays.asList(20, 20), 8, queryUri, 10);
        runPaginationTest(Arrays.asList(0), 0, queryUri, 0);

        queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=Anna&offset=5&limit=10");
        runPaginationTest(Arrays.asList(8), 7, queryUri, 2);
        runPaginationTest(Arrays.asList(30), 15, queryUri, 10);

        queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=Anna&offset=20&limit=10");
        runPaginationTest(Arrays.asList(60, 50), 7, queryUri, 0);
        runPaginationTest(Arrays.asList(60, 20), 12, queryUri, 4);
        runPaginationTest(Arrays.asList(60, 40), 18, queryUri, 10);
    }

    private void runPaginationTest(List<Integer> numSearchHits, int filterNum, URI queryUri, int numResults) {

        SearchResourceService rs = Mockito.spy(resourceService);
        EntityDefinition mockDef = Mockito.mock(EntityDefinition.class);
        MockBasicService mockService = new MockBasicService();
        mockService.setNumToReturn(numSearchHits);
        Mockito.when(mockDef.getService()).thenReturn(mockService);
        Mockito.when(rs.getService()).thenReturn(mockService);
        Mockito.doReturn(getResults(filterNum)).when(rs).filterResultsBySecurity(Mockito.isA(List.class));
        ApiQuery apiQuery = rs.prepareQuery(new Resource("v1", "student"), null, queryUri);
        List<EntityBody> results = rs.retrieveResults(apiQuery);
        Assert.assertEquals(numResults, results.size());
    }

    private List<EntityBody> getResults(int num) {
        List<EntityBody> results = new ArrayList<EntityBody>();
        for (int i=0; i<num; i++) {
            results.add(new EntityBody());
        }
        return results;
    }
}
