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

import org.junit.Test;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;

public class StaffTeacherDirectRelatedContextResolverTest {

    StaffTeacherDirectRelatedContextResolver resolver = new StaffTeacherDirectRelatedContextResolver();

    @Test
    public void canResolveTeacherAndStaffRelatedEntities() {
        assertEquals(ParameterConstants.TEACHER_ID, resolver.getReferenceProperty(EntityNames.TEACHER_SCHOOL_ASSOCIATION));
        assertEquals(ParameterConstants.TEACHER_ID, resolver.getReferenceProperty(EntityNames.TEACHER_SECTION_ASSOCIATION));
        assertEquals(ParameterConstants.STAFF_REFERENCE, resolver.getReferenceProperty(EntityNames.STAFF_ED_ORG_ASSOCIATION));
        assertEquals(ParameterConstants.STAFF_ID, resolver.getReferenceProperty(EntityNames.STAFF_COHORT_ASSOCIATION));
        assertEquals(ParameterConstants.STAFF_ID, resolver.getReferenceProperty(EntityNames.STAFF_PROGRAM_ASSOCIATION));
        assertEquals(null, resolver.getReferenceProperty(EntityNames.STAFF));
        assertEquals(null, resolver.getReferenceProperty(EntityNames.TEACHER));
    }
}
