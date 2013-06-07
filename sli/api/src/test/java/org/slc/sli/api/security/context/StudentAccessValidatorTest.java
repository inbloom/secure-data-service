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
package org.slc.sli.api.security.context;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.common.constants.EntityNames;

public class StudentAccessValidatorTest {
    
    StudentAccessValidator underTest = new StudentAccessValidator();
    
    @Test
    public void disciplineRelatedEntityShouldNeverBeAllowed() {
        List<String> paths = Arrays.asList("noise", "white_noise", EntityNames.DISCIPLINE_ACTION, "noise");
        assertFalse(underTest.isAllowed(paths));
        // two parts
        paths = Arrays.asList(ResourceNames.DISCIPLINE_ACTIONS, "id123");
        assertFalse(underTest.isAllowed(paths));
        paths = Arrays.asList(ResourceNames.DISCIPLINE_INCIDENTS, "id123");
        assertFalse(underTest.isAllowed(paths));
        paths = Arrays.asList(ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS, "id123");
        assertFalse(underTest.isAllowed(paths));
    }
    
    @Test
    public void whiteListedURLAllowed() {
        List<String> paths = Arrays.asList(ResourceNames.SESSIONS, "id123", ResourceNames.COURSE_OFFERINGS);
        assertTrue(underTest.isAllowed(paths));
        
        paths = Arrays.asList(ResourceNames.SESSIONS, "id123", ResourceNames.COURSE_OFFERINGS, ResourceNames.COURSES);
        assertTrue(underTest.isAllowed(paths));

    }
    
    @Test
    public void notInWhiteListBlocked() {
        List<String> paths = Arrays.asList(ResourceNames.SESSIONS, "id123", ResourceNames.STUDENT_ACADEMIC_RECORDS);
        assertFalse(underTest.isAllowed(paths));
    }
    
    @Test
    public void twoPartsAreAllowed() {
        List<String> paths = Arrays.asList("anything", "id123");
        assertTrue(underTest.isAllowed(paths));
    }

}
