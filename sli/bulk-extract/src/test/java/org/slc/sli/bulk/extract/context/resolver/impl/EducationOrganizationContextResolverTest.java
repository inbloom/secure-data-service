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
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

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
    
    @Before
    public void setup() {
        underTest.getCache().clear();
        Entity school = createSchool();
        Entity level1 = createLevel1();
        Entity level2 = createLevel2();
        Entity sea = createSEA();
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "school")).thenReturn(school);
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "level1")).thenReturn(level1);
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "level2")).thenReturn(level2);
        when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "sea")).thenReturn(sea);
    }
    
    @Test
    public void testFindGoverningLEA() {
        assertEquals(new HashSet<String>(Arrays.asList("level2")), underTest.findGoverningLEA(createSchool()));
    }
    
    @Test
    public void testFindGoverningLEAForId() {
        assertEquals(new HashSet<String>(Arrays.asList("level2")), underTest.findGoverningLEA("school"));
    }
    
    @Test
    public void testCache() {
        Set<String> leas = underTest.findGoverningLEA("school");
        Set<String> secondCall = underTest.findGoverningLEA("school");
        assertEquals(leas, secondCall);
        verify(repo, times(1)).findById(EntityNames.EDUCATION_ORGANIZATION, "school");
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

    private Entity createSEA() {
        Entity e = mock(Entity.class);
        when(e.getEntityId()).thenReturn("sea");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("State Education Agency"));
        when(e.getBody()).thenReturn(body);
        return e;
    }
}
