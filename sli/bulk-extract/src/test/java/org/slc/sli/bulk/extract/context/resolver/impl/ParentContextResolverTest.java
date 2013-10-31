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
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

/**
 * JUnit for parent context resolver
 * 
 * @author nbrown
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ParentContextResolverTest {
    @InjectMocks
    private final ParentContextResolver underTest = new ParentContextResolver();
    @Mock
    private Repository<Entity> repo;
    @Mock
    private StudentContextResolver studentResolver;
    
    @Test
    public void test() {
        Entity parent = mock(Entity.class);
        Entity kid1 = mock(Entity.class);
        when(kid1.getEntityId()).thenReturn("kid1");

        Entity kid2 = mock(Entity.class);
        when(kid2.getEntityId()).thenReturn("kid2");

        when(parent.getEntityId()).thenReturn("parentId");
        when(repo.findEach(eq("student"), argThat(new BaseMatcher<Query>() {
            
            @Override
            public boolean matches(Object item) {
                Query q = (Query) item;
                return q.getQueryObject().get("studentParentAssociation.body.parentId").equals("parentId");
            }
            
            @Override
            public void describeTo(Description description) {
            }
        }))).thenReturn(Arrays.asList(kid1, kid2).iterator());
        // I don't know, maybe the parent has a step kid who used to live in a different district.
        when(studentResolver.findGoverningEdOrgs("kid1", parent)).thenReturn(new HashSet<String>(Arrays.asList("lea1", "lea2")));
        when(studentResolver.findGoverningEdOrgs("kid2", parent)).thenReturn(new HashSet<String>(Arrays.asList("lea1", "lea3")));
        assertEquals(new HashSet<String>(Arrays.asList("lea1", "lea2", "lea3")), underTest.findGoverningEdOrgs(parent));
    }
    
}
