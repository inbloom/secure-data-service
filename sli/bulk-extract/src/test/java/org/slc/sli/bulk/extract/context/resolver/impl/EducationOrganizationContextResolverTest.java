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
package org.slc.sli.bulk.extract.context.resolver.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.slc.sli.bulk.extract.delta.DeltaEntityIterator;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;

/**
 * Unit test for the student context resolver
 * 
 * @author nbrown
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class EducationOrganizationContextResolverTest {
    
    @InjectMocks
    private EducationOrganizationContextResolver underTest = new EducationOrganizationContextResolver();
    
    @Mock
    private Repository<Entity> repo;
    
    @Mock
    EdOrgHierarchyHelper helper;

    Entity school = createSchool();
    Entity multiParentSchool = createMultiParentSchool();
    Entity level1 = createLevel1();
    Entity level2 = createLevel2();
    Entity level2_2 = createLevel2_2();
    
    List<Entity> level1_list = createLevel1List();
    List<Entity> level2_list = createLevel2List();
    List<Entity> level2_2_list = createLevel2_2List();

    

    @Before
    public void setup() {
        underTest.getCache().clear();
        Entity sea = createSEA();
        underTest.setHelper(helper);
        
        when(repo.findOne(EntityNames.EDUCATION_ORGANIZATION, DeltaEntityIterator.buildQuery(underTest.getCollection(), "school"))).thenReturn(school);
        when(repo.findOne(EntityNames.EDUCATION_ORGANIZATION, DeltaEntityIterator.buildQuery(underTest.getCollection(), "multiParentSchool"))).thenReturn(multiParentSchool );
        when(repo.findOne(EntityNames.EDUCATION_ORGANIZATION, DeltaEntityIterator.buildQuery(underTest.getCollection(), "level1"))).thenReturn(level1);
        when(repo.findOne(EntityNames.EDUCATION_ORGANIZATION, DeltaEntityIterator.buildQuery(underTest.getCollection(), "level2"))).thenReturn(level2);
        when(repo.findOne(EntityNames.EDUCATION_ORGANIZATION, DeltaEntityIterator.buildQuery(underTest.getCollection(), "level2_2"))).thenReturn(level2_2);
        
        when(repo.findOne(EntityNames.EDUCATION_ORGANIZATION, DeltaEntityIterator.buildQuery(underTest.getCollection(), "sea"))).thenReturn(sea);
        when(helper.isSchool(school)).thenReturn(true);
        when(helper.isSchool(multiParentSchool)).thenReturn(true);
        when(helper.isLEA(level1)).thenReturn(true);
        when(helper.isLEA(level2)).thenReturn(true);
        when(helper.isLEA(level2_2)).thenReturn(true);
        when(helper.isSEA(sea)).thenReturn(true);

        when(helper.getTopLEAOfEdOrg(school)).thenReturn(level2_list);
        when(helper.getTopLEAOfEdOrg(multiParentSchool)).thenReturn(level2_2_list);        
        when(helper.getTopLEAOfEdOrg(level1)).thenReturn(level2_list);
        when(helper.getTopLEAOfEdOrg(level2)).thenReturn(level2_list);
        when(helper.getTopLEAOfEdOrg(level2_2)).thenReturn(level2_list);
        
    }
    
    @Test
    public void testFindGoverningEdOrgs() {
        assertEquals(new HashSet<String>(Arrays.asList("level2")), underTest.findGoverningEdOrgs(school));
    }

    @Test
    public void testFindGoverningEdOrgsForId() {
        assertEquals(new HashSet<String>(Arrays.asList("level2")), underTest.findGoverningEdOrgs("school"));
    }

    @Test
    public void testFindGoverningEdOrgsForSEA() {
        assertEquals(new HashSet<String>(Arrays.asList("sea")), underTest.findGoverningEdOrgs("sea"));
    }
    
    @Test
    public void testCache() {
        Set<String> leas = underTest.findGoverningEdOrgs("school");
        Set<String> secondCall = underTest.findGoverningEdOrgs("school");
        assertEquals(leas, secondCall);
        verify(repo, times(1)).findOne(EntityNames.EDUCATION_ORGANIZATION, DeltaEntityIterator.buildQuery(underTest.getCollection(), "school"));
    }
    
    @Test
    public void testMultiParents() {
        assertEquals(new HashSet<String>(Arrays.asList(new String[] {"level2", "level2_2"})), underTest.findGoverningEdOrgs(multiParentSchool));
    }
   
    
    private Entity createSchool() {
        Entity e = mock(Entity.class);
        when(e.getEntityId()).thenReturn("school");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", "level1");
        when(e.getBody()).thenReturn(body);
        return e;
    }
    
    private Entity createMultiParentSchool() {
        Entity e = mock(Entity.class);
        when(e.getEntityId()).thenReturn("multiParentSchool");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("multiParentSchool"));
    	String [] parents = {"level2_2","level2"};
        body.put("parentEducationAgencyReference", parents);
        when(e.getBody()).thenReturn(body);
        return e;
    }
    
    private Entity createLevel1() {
        Entity e = mock(Entity.class);
        when(e.getEntityId()).thenReturn("level1");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", "level2");
        when(e.getBody()).thenReturn(body);
        return e;
    }
    
    private Entity createLevel2() {
        Entity e = mock(Entity.class);
        when(e.getEntityId()).thenReturn("level2");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", "sea");
        when(e.getBody()).thenReturn(body);
        return e;    
    }    
    
    private Entity createLevel2_2() {
        Entity e = mock(Entity.class);
        when(e.getEntityId()).thenReturn("level2_2");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", "sea");
        when(e.getBody()).thenReturn(body);
        return e;    
    }

    private Entity createSEA() {
        Entity e = mock(Entity.class);
        when(e.getEntityId()).thenReturn("sea");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("State Education Agency"));
        when(e.getBody()).thenReturn(body);
        return e;
    }
    
    private List<Entity> createLevel1List() {
    	List<Entity> level1_list = new ArrayList<Entity>();
    	level1_list.add(level1);
    	return level1_list;
    }
    
    private List<Entity> createLevel2List() {
    	List<Entity> level2_list = new ArrayList<Entity>();
    	level2_list.add(level2);
    	return level2_list;
    }
    
    private List<Entity> createLevel2_2List() {
    	List<Entity> level2_2list = new ArrayList<Entity>();
    	level2_2list.add(level2);
    	level2_2list.add(level2_2);
    	return level2_2list;
    }
}
