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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

public class StudentDirectRelatedContextResolverTest {
    
    @InjectMocks
    StudentDirectRelatedContextResolver resolver = new StudentDirectRelatedContextResolver();

    @Mock
    StudentContextResolver studentResolver;

    Set<String> topLevelLEAs = new HashSet<String>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        topLevelLEAs.add("lea1");
        topLevelLEAs.add("lea2");
    }
    
    @Test
    public void entityWithNoStudentIdCannotBeResolved() {
        Entity e = new MongoEntity("type", "id", new HashMap<String, Object>(), new HashMap<String, Object>());
        Set<String> leas = resolver.findGoverningEdOrgs(e);
        assertTrue(leas.size() == 0);
    }

    @Test
    public void shouldReturnWhateverStudentResolverReturns() {

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId", "studentId123");
        Entity e = new MongoEntity("type", "id", body, new HashMap<String, Object>());
        Mockito.when(studentResolver.findGoverningEdOrgs("studentId123", e)).thenReturn(topLevelLEAs);

        Set<String> leas = resolver.findGoverningEdOrgs(e);

        assertTrue(leas.size() == 2);
        assertTrue(leas.contains("lea1"));
        assertTrue(leas.contains("lea2"));
        assertFalse(leas.contains("lea3"));
    }
    
}
