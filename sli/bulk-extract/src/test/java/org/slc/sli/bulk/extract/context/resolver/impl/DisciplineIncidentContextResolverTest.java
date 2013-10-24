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

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * @author: tshewchuk
 */
@RunWith(MockitoJUnitRunner.class)
public class DisciplineIncidentContextResolverTest {

    @InjectMocks
    DisciplineIncidentContextResolver resolver = new DisciplineIncidentContextResolver();

    @Mock
    Repository<Entity> repo;

    @Mock
    StudentContextResolver studentResolver;

    @Mock
    Entity disciplineIncident;

    @Mock
    Entity student1;

    @Mock
    Entity student2;

    @Mock
    Entity student3;

    @Mock
    Entity student4;

    @Before
    public void setUp() throws Exception {
        disciplineIncident = Mockito.mock(Entity.class);
        Mockito.when(disciplineIncident.getEntityId()).thenReturn("123_id");

        Mockito.when(student1.getEntityId()).thenReturn("student1");
        Mockito.when(student2.getEntityId()).thenReturn("student2");
        Mockito.when(student3.getEntityId()).thenReturn("student3");
        Mockito.when(student4.getEntityId()).thenReturn("student4");

        Mockito.when(studentResolver.findGoverningEdOrgs(Mockito.eq("student1"), Mockito.eq(disciplineIncident))).thenReturn(new HashSet<String>(Arrays.asList("school1")));
        Mockito.when(studentResolver.findGoverningEdOrgs(Mockito.eq("student2"), Mockito.eq(disciplineIncident))).thenReturn(new HashSet<String>(Arrays.asList("school1", "school2")));
        Mockito.when(studentResolver.findGoverningEdOrgs(Mockito.eq("student3"), Mockito.eq(disciplineIncident))).thenReturn(new HashSet<String>(Arrays.asList("school3")));
        Mockito.when(studentResolver.findGoverningEdOrgs(Mockito.eq("student4"), Mockito.eq(disciplineIncident))).thenReturn(new HashSet<String>());
    }

    @Test
    public void testSingleStudent() {
        setFindEachReturn(Arrays.asList(student1));

        Assert.assertEquals(new HashSet<String>(Arrays.asList("school1")), resolver.findGoverningEdOrgs(disciplineIncident));
    }

    @Test
    public void testMultipleStudents() {
        setFindEachReturn(Arrays.asList(student1, student2, student3));

        Assert.assertEquals(new HashSet<String>(Arrays.asList("school1", "school2", "school3")), resolver.findGoverningEdOrgs(disciplineIncident));
    }

    @Test
    public void testNoStudentDIAssociation() {
        setFindEachReturn(Arrays.asList(student4));

        Assert.assertTrue(resolver.findGoverningEdOrgs(disciplineIncident).isEmpty());
    }

    private void setFindEachReturn(List<Entity> studentEntities) {
        Mockito.when(repo.findEach(eq(EntityNames.STUDENT), argThat(new BaseMatcher<Query>() {
            @Override
            public boolean matches(Object item) {
                Query q = (Query) item;
                return q.getQueryObject().get("studentDisciplineIncidentAssociation.body.disciplineIncidentId").equals("123_id");
            }

            @Override
            public void describeTo(Description description) {
            }
        }))).thenReturn(studentEntities.iterator());
    }
}
