package org.slc.sli.bulk.extract.context.resolver.impl;

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
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Unit test for program context resolver
 * 
 * @author nbrown
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ProgramContextResolverTest {
    
    @Mock
    private StudentContextResolver studentResolver = new StudentContextResolver();
    
    @Mock
    private EducationOrganizationContextResolver edOrgResolver;
    
    @Mock
    private Repository<Entity> repo;
    
    @InjectMocks
    private ContextResolver underTest = new ProgramContextResolver();
    
    @Test
    public void test() {
        Entity program = mock(Entity.class);
        Entity river = mock(Entity.class);
        Entity simon = mock(Entity.class);
        Entity school = mock(Entity.class);
        when(edOrgResolver.findGoverningLEA(school)).thenReturn(
                new HashSet<String>(Arrays.asList("OsirisSchoolDistrict")));
        when(studentResolver.findGoverningLEA(river)).thenReturn(new HashSet<String>(Arrays.asList("Academy")));
        when(studentResolver.findGoverningLEA(simon)).thenReturn(new HashSet<String>(Arrays.asList("MedSchool")));
        when(program.getEntityId()).thenReturn("SomeProgram");
        when(
                repo.findEach(eq("educationOrganization"),
                        eq(Query.query(Criteria.where("body.programReference").is("SomeProgram"))))).thenReturn(
                Arrays.asList(school).iterator());
        when(
                repo.findEach(eq("student"),
                        eq(Query.query(Criteria.where("studentProgramAssociation.body.programId").is("SomeProgram")))))
                .thenReturn(Arrays.asList(river, simon).iterator());
        assertEquals(new HashSet<String>(Arrays.asList("OsirisSchoolDistrict", "Academy", "MedSchool")),
                underTest.findGoverningLEA(program));
    }
}
