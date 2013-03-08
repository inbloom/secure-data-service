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
package org.slc.sli.api.search.service;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
import org.slc.sli.api.search.service.SearchResourceService.Embedded;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.tenantdb.TenantIdToDbName;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.enums.Right;

/**
 * Tests for the search resource/service.
 * 
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
public class SearchResourceServiceTest {

    @Autowired
    SearchResourceService resourceService;

    @Autowired
    Embedded embedded;

    static Client client;
    
    @Before
    public void setup() {
        client = embedded.getClient();
    }
    
    private void indexData(String index, String type, Collection<Map<String, Object>> data) throws Exception {
        if (!client.admin().indices().prepareExists(index).execute().actionGet().isExists()) {
            client.admin().indices().prepareCreate(index).execute().actionGet();
        }
        client.admin()
                .indices()
                .preparePutMapping(index)
                .setType(type)
                .setSource(
                        jsonBuilder().startObject().startObject(type).startObject("properties").startObject("context")
                                .startObject("properties").startObject("schoolId").field("type", "string")
                                .field("index", "not_analyzed").endObject().endObject().endObject().endObject()
                                .endObject()).execute().actionGet();
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (Map<String, Object> object : data) {
            bulkRequest.add(client.prepareIndex(index, type).setId((String) object.get("_id")).setSource(object));
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            throw new RuntimeException("Unable to index provided data " + bulkResponse.buildFailureMessage());
        }
        client.admin().indices().prepareFlush(index).execute().actionGet();
    }
    
    @SuppressWarnings("unchecked")
    private void indexData(String index, String type, Map<String, Object> object) throws Exception {
        indexData(index, type, Arrays.asList(object));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testResponse() throws Exception {
        setupAuth(EntityNames.TEACHER);
        Map<String, Object> student = new HashMap<String, Object>();
        Map<String, Object> name = new HashMap<String, Object>();
        Map<String, Object> context = new HashMap<String, Object>();
        student.put("_id", "david'sid");
        name.put("firstName", "David");
        name.put("lastSurname", "Wu");
        name.put("middleName", "≠");
        student.put("name", name);
        context.put("schoolId", "ALL");
        student.put("context", context);

        String index = TenantIdToDbName.convertTenantIdToDbName(TenantContext.getTenantId());
        indexData(index, "student", student);

        student.put("_id", "another");
        name.put("firstName", "xyz");
        name.put("lastSurname", "Davi");
        indexData(index, "student", student);

        Resource resource = new Resource("v1", "search");
        SearchResourceService rs = Mockito.spy(resourceService);
        Mockito.when(rs.filterResultsBySecurity(Mockito.isA(List.class), Mockito.anyInt(), Mockito.anyInt())).thenAnswer(new Answer<List<EntityBody>>() {
            @Override
            public List<EntityBody> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (List<EntityBody>) args[0];
            }
        });

        ServiceResponse serviceResponse = null;
        serviceResponse = rs.list(resource, null, new URI("http://local.slidev.org:8080/api/rest/v1/search?q=David%20Wu"), false);
        Assert.assertEquals(1L, serviceResponse.getEntityCount());
        EntityBody studentBody = serviceResponse.getEntityBodyList().get(0);
        Assert.assertEquals("david'sid", studentBody.get("_id"));
        Assert.assertEquals("≠", ((Map<String, Object>) studentBody.get("name")).get("middleName"));

        serviceResponse = rs.list(resource, null, new URI("http://local.slidev.org:8080/api/rest/v1/search?name.firstName=davId"), false);
        Assert.assertEquals(1L, serviceResponse.getEntityCount());
        studentBody = serviceResponse.getEntityBodyList().get(0);
        Assert.assertEquals("david'sid", studentBody.get("_id"));

        serviceResponse = rs.list(resource, null, new URI("http://local.slidev.org:8080/api/rest/v1/search/student?q=Davi"), false);
        Assert.assertEquals(2L, serviceResponse.getEntityCount());
    }

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
        ServiceResponse serviceResponse = resourceService.list(resource, null, queryUri, false);
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

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckAccessibleAsStaff() {

        setupAuth(EntityNames.STAFF);
        SearchResourceService rs = Mockito.spy(resourceService);

        // for staff, list of entities should not change
        Mockito.doReturn(getSet("1", "2")).when(rs).filterOutInaccessibleIds(Mockito.eq("student"), Mockito.anySet());
        Mockito.doReturn(getSet("3")).when(rs).filterOutInaccessibleIds(Mockito.eq("section"), Mockito.anySet());
        Collection<EntityBody> result = rs.filterResultsBySecurity(getEntities(), 0, 10);
        Assert.assertEquals(3, result.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckAccessibleAsTeacher() {

        setupAuth(EntityNames.TEACHER);

        // test varied accessibility
        SearchResourceService rs = Mockito.spy(resourceService);
        Mockito.doReturn(getSet("1", "3", "5")).when(rs).filterOutInaccessibleIds(Mockito.eq("student"), Mockito.anySet());
        Mockito.doReturn(getSet("3")).when(rs).filterOutInaccessibleIds(Mockito.eq("section"), Mockito.anySet());
        Mockito.doReturn(getSet("1")).when(rs).filterOutInaccessibleIds(Mockito.eq("someRandomType"), Mockito.anySet());
        Collection<EntityBody> result = rs.filterResultsBySecurity(getEntities(), 0, 10);

        Assert.assertEquals(2, result.size());

        Assert.assertTrue(contains(result, "student", "1"));
        Assert.assertTrue(contains(result, "section", "3"));

        // test when all entities are inaccessible
        Mockito.doReturn(getSet()).when(rs).filterOutInaccessibleIds(Mockito.anyString(), Mockito.anySet());
        result = rs.filterResultsBySecurity(getEntities(), 0, 10);
        // technically you can see one entity, because there is one section in those entities, which are now global
        Assert.assertEquals(1, result.size());

    }

    private boolean contains(Collection<EntityBody> col, String type, String id) {
        Iterator<EntityBody> eb = col.iterator();
        while (eb.hasNext()) {
            EntityBody next = eb.next();
            if (type.equals(next.get("type")) && id.equals(next.get("id"))) {
                return true;
            }
        }
        return false;
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

    @SuppressWarnings("unused")
    private Set<String> getEntitiesIds() {
        // set up entities
        Set<String> entities = new HashSet<String>();
        for (EntityBody eb : getEntities()) {
            entities.add((String) eb.get("id"));
        }
        return entities;
    }

    private Set<String> getSet(String... ids) {
        return new HashSet<String>(Arrays.asList(ids));
    }

    @Test
    public void testPagination() throws URISyntaxException {

        setupAuth(EntityNames.TEACHER);

        URI queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=Anna&offset=0&limit=10");
        runPaginationTest(Arrays.asList(20, 20, 20), 4, queryUri, 10, true);
        runPaginationTest(Arrays.asList(8), 6, queryUri, 6, false);
        runPaginationTest(Arrays.asList(20, 8), 4, queryUri, 8, false);
        runPaginationTest(Arrays.asList(20, 20), 8, queryUri, 10, true);
        runPaginationTest(Arrays.asList(0), 0, queryUri, 0, false);

        queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=Anna&offset=5&limit=10");
        runPaginationTest(Arrays.asList(8), 7, queryUri, 2, false);
        runPaginationTest(Arrays.asList(30, 0), 15, queryUri, 10, false);

        queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=Anna&offset=20&limit=10");
        runPaginationTest(Arrays.asList(60, 50, 0), 7, queryUri, 0, false);
        runPaginationTest(Arrays.asList(60, 20, 0), 12, queryUri, 4, false);
        runPaginationTest(Arrays.asList(60, 40), 18, queryUri, 10, true);

        queryUri = new URI("http://local.slidev.org:8080/api/rest/v1/search?q=Anna&offset=0&limit=2");
        runPaginationTest(Arrays.asList(6, 6), 2, queryUri, 2, true);
    }

    @SuppressWarnings("unchecked")
    private void runPaginationTest(List<Integer> numSearchHits, final int filterNum, URI queryUri, int numResults,
                                   Boolean moreEntities) {

        SearchResourceService rs = Mockito.spy(resourceService);
        EntityDefinition mockDef = Mockito.mock(EntityDefinition.class);
        MockBasicService mockService = new MockBasicService();
        mockService.setNumToReturn(numSearchHits);
        Mockito.when(mockDef.getService()).thenReturn(mockService);
        Mockito.when(rs.getService()).thenReturn(mockService);
        Mockito.when(rs.filterResultsBySecurity(Mockito.isA(List.class), Mockito.anyInt(), Mockito.anyInt())).thenAnswer(new Answer<List<EntityBody>>() {
            @Override
            public List<EntityBody> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return getResults((List<EntityBody>) args[0], filterNum);
            }
        });
        ApiQuery apiQuery = rs.prepareQuery(new Resource("v1", "student"), null, queryUri);
        Pair<? extends List<EntityBody>, Boolean> resultPair = rs.retrieveResults(apiQuery);
        List<EntityBody> results = resultPair.getLeft();
        Boolean hasMore = resultPair.getRight();
        Assert.assertEquals(numResults, results.size());
        Assert.assertEquals(moreEntities, hasMore);
    }

    private List<EntityBody> getResults(List<EntityBody> list, int num) {
        return new ArrayList<EntityBody>(list.subList(0, Math.min(num, list.size())));
    }
}
