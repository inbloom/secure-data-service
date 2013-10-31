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
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;

/**
 * Unit test for gradebookEntry resolver
 * @author: npandey
 */
@RunWith(MockitoJUnitRunner.class)
public class GradebookEntryContextResolverTest {

    @InjectMocks
    GradebookEntryContextResolver resolver = new GradebookEntryContextResolver();

    @Mock
    private Repository<Entity> repo;

    @Mock
    private StudentContextResolver studentResolver;

    @Test
    public void test() {
        Entity gradebookEntry = Mockito.mock(Entity.class);
        Mockito.when(gradebookEntry.getEntityId()).thenReturn("123_id");
        Mockito.when(gradebookEntry.getType()).thenReturn(EntityNames.GRADEBOOK_ENTRY);

        Entity sgbe1 = Mockito.mock(Entity.class);
        Map<String, Object> body1 = new HashMap<String, Object>();
        body1.put(ParameterConstants.STUDENT_ID, "student1");
        Mockito.when(sgbe1.getBody()).thenReturn(body1);

        Entity sgbe2 = Mockito.mock(Entity.class);
        Map<String, Object> body2 = new HashMap<String, Object>();
        body2.put(ParameterConstants.STUDENT_ID, "student2");
        Mockito.when(sgbe2.getBody()).thenReturn(body2);



        Mockito.when(repo.findEach(eq(EntityNames.STUDENT_GRADEBOOK_ENTRY), argThat(new BaseMatcher<Query>() {

            @Override
            public boolean matches(Object item) {
                Query q = (Query) item;
                return q.getQueryObject().get("body.gradebookEntryId").equals("123_id");
            }

            @Override
            public void describeTo(Description description) {
            }
        }))).thenReturn(Arrays.asList(sgbe1, sgbe2).iterator());


        Mockito.when(studentResolver.findGoverningEdOrgs("student1", gradebookEntry)).thenReturn(new HashSet<String>(Arrays.asList("lea1", "lea2")));
        Mockito.when(studentResolver.findGoverningEdOrgs("student2", gradebookEntry)).thenReturn(new HashSet<String>(Arrays.asList("lea1", "lea3")));

        Assert.assertEquals(new HashSet<String>(Arrays.asList("lea1", "lea2", "lea3")), resolver.findGoverningEdOrgs(gradebookEntry));
    }
}
