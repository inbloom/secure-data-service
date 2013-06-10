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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class CourseOfferingContextResolverTest {
    
    @InjectMocks
    CourseOfferingContextResolver underTest = new CourseOfferingContextResolver();

    @Mock
    SectionContextResolver sectionResolver;
    
    @Mock
    EducationOrganizationContextResolver edorgResolver;

    @Mock
    Repository<Entity> repo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void followSchoolId() {
        Entity courseOffering = buildCourseOffering();
        when(edorgResolver.findGoverningEdOrgs("edorg123")).thenReturn(new HashSet<String>(Arrays.asList("lea1", "lea2")));
        when(repo.findEach("section", new NeutralQuery(
                new NeutralCriteria("courseOfferingId", NeutralCriteria.OPERATOR_EQUAL, "courseOffering1"))))
                .thenReturn(new ArrayList<Entity>().iterator());
        assertEquals(new HashSet<String>(Arrays.asList("lea1", "lea2")), underTest.findGoverningEdOrgs(courseOffering));
    }
  
    @Test
    public void followSections() {
        Entity courseOffering = buildCourseOffering();
        List<Entity> sections = buildSections();
        when(repo.findEach("section", new NeutralQuery(
                new NeutralCriteria("courseOfferingId", NeutralCriteria.OPERATOR_EQUAL, "courseOffering1"))))
                .thenReturn(sections.iterator());
        when(edorgResolver.findGoverningEdOrgs("edorg123")).thenReturn(new HashSet<String>());
        when(sectionResolver.findGoverningEdOrgs(sections.get(0))).thenReturn(new HashSet<String>(Arrays.asList("lea3")));
        when(sectionResolver.findGoverningEdOrgs(sections.get(1))).thenReturn(new HashSet<String>(Arrays.asList("lea3", "lea4")));
        
        assertEquals(new HashSet<String>(Arrays.asList("lea3", "lea4")), underTest.findGoverningEdOrgs(courseOffering));
    }
    
    private List<Entity> buildSections() {
        List<Entity> sections = new ArrayList<Entity>();
        for (int i = 0; i < 2; ++i) {
            sections.add(buildSection("section" + i));
        }
        return sections;
    }

    private Entity buildSection(String id) {
        return new MongoEntity("section", id, new HashMap<String, Object>(), new HashMap<String, Object>());
    }

    private Entity buildCourseOffering() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("schoolId", "edorg123");
        return new MongoEntity(EntityNames.COURSE_OFFERING, "courseOffering1", body, new HashMap<String, Object>());
    }
    
}
