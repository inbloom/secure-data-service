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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author: npandey
 */
public class DisciplineActionContextResolverTest {
    @InjectMocks
    DisciplineActionContextResolver resolver = new DisciplineActionContextResolver();

    @Mock
    Repository<Entity> repo;

    @Mock
    StudentContextResolver studentResolver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSingleStudent() {
        Entity disciplineAction = Mockito.mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.STUDENT_ID, Arrays.asList("student1"));
        Mockito.when(disciplineAction.getBody()).thenReturn(body);

        Mockito.when(studentResolver.findGoverningEdOrgs("student1", disciplineAction)).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));

        Assert.assertEquals(new HashSet<String>(Arrays.asList("edOrg1")), resolver.findGoverningEdOrgs(disciplineAction));
    }

    @Test
    public void testMultipleStudents() {
        Entity disciplineAction = Mockito.mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.STUDENT_ID, Arrays.asList("student1", "student2", "student3", "student4"));
        Mockito.when(disciplineAction.getBody()).thenReturn(body);

        Mockito.when(studentResolver.findGoverningEdOrgs("student1", disciplineAction)).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        Mockito.when(studentResolver.findGoverningEdOrgs("student2", disciplineAction)).thenReturn(new HashSet<String>(Arrays.asList("edOrg2", "edOrg1")));
        Mockito.when(studentResolver.findGoverningEdOrgs("student3", disciplineAction)).thenReturn(new HashSet<String>(Arrays.asList("edOrg3","edOrg2", "edOrg1")));
        Mockito.when(studentResolver.findGoverningEdOrgs("student4", disciplineAction)).thenReturn(new HashSet<String>(Arrays.asList("edOrg4")));

        Assert.assertEquals(new HashSet<String>(Arrays.asList("edOrg1", "edOrg2", "edOrg3", "edOrg4")), resolver.findGoverningEdOrgs(disciplineAction));
    }
}
