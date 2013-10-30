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
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;

public class StudentCompetencyContextResolverTest {
    
    @InjectMocks
    StudentCompetencyContextResolver underTest = new StudentCompetencyContextResolver();

    @Mock
    StudentContextResolver studentResolver;
    
    @Mock
    Repository<Entity> repo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void noStudentSectionAssociation() {
        Entity studentCompetency = buildStudentCompetency();
        when(repo.findById(EntityNames.STUDENT_SECTION_ASSOCIATION, "association123")).thenReturn(null);
        assertEquals(Collections.<String> emptySet(), underTest.findGoverningEdOrgs(studentCompetency));
    }

    @Test
    public void shouldFollowStudent() {
        Entity studentCompetency = buildStudentCompetency();
        when(repo.findById(EntityNames.STUDENT_SECTION_ASSOCIATION, "association123")).thenReturn(buildStudentSectionAssociation());
        Set<String> topLeas = new HashSet<String>(Arrays.asList("lea1", "lea2"));
        when(studentResolver.findGoverningEdOrgs("student123", studentCompetency)).thenReturn(topLeas);
        
        assertEquals(topLeas, underTest.findGoverningEdOrgs(studentCompetency));
    }
    
    private Entity buildStudentSectionAssociation() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId", "student123");
        body.put("sectionId", "section123");
        Entity e = new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, "association123", body, new HashMap<String, Object>());
        return e;
    }

    private Entity buildStudentCompetency() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, "association123");
        Entity e = new MongoEntity("studentCompetency", "studentCompetency123", body, new HashMap<String, Object>());
        return e;
    }
    
}
