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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.common.constants.EntityNames;

public class StaffTeacherDirectRelatedContextResolverTest {
    
    @InjectMocks
    StaffTeacherDirectRelatedContextResolver resolver = new StaffTeacherDirectRelatedContextResolver();
    
    @Mock
    private StaffTeacherContextResolver staffTeacherResolver;

    @Before
    public void setUp() throws Exception {
        staffTeacherResolver = Mockito.mock(StaffTeacherContextResolver.class);
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void canResolveTeacherAndStaffRelatedEntities() {
        assertEquals(StaffTeacherDirectRelatedContextResolver.TEACHER_ID, resolver.getReferenceProperty(EntityNames.TEACHER_SCHOOL_ASSOCIATION));
        assertEquals(StaffTeacherDirectRelatedContextResolver.TEACHER_ID, resolver.getReferenceProperty(EntityNames.TEACHER_SECTION_ASSOCIATION));
        assertEquals(StaffTeacherContextResolver.STAFF_REFERENCE, resolver.getReferenceProperty(EntityNames.STAFF_ED_ORG_ASSOCIATION));
    }
}
