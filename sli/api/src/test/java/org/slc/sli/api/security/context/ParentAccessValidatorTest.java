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
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.sun.jersey.spi.container.ContainerRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.generic.util.ResourceMethod;

public class ParentAccessValidatorTest {
    
    @InjectMocks
    ParentAccessValidator underTest = new ParentAccessValidator();
    
    @Mock
    StudentAccessValidator studentValidator;
    
    ContainerRequest request;

    @Before
    public void setUp() throws Exception {
        studentValidator = Mockito.mock(StudentAccessValidator.class);
        request = Mockito.mock(ContainerRequest.class);
        MockitoAnnotations.initMocks(this);
        when(request.getMethod()).thenReturn(ResourceMethod.GET.toString());
    }
    
    @Test
    public void followStudents() {
        when(request.getPath()).thenReturn("something works for student");
        when(studentValidator.isAllowed(request)).thenReturn(true);
        assertTrue(underTest.isAllowed(request));
        
        when(request.getPath()).thenReturn("something doesn't work for student");
        when(studentValidator.isAllowed(request)).thenReturn(false);
        assertFalse(underTest.isAllowed(request));
    }
    
    @Test
    public void parentCannotWrite() {
        List<String> allowed = Arrays.asList(ResourceNames.GRADES,
                ResourceNames.STUDENT_GRADEBOOK_ENTRIES,
                ResourceNames.STUDENT_ASSESSMENTS,
                ResourceNames.STUDENTS);
        
        List<String> writeOps = Arrays.asList(ResourceMethod.PUT.toString(),
                ResourceMethod.PATCH.toString(), ResourceMethod.DELETE.toString(), ResourceMethod.POST.toString());
        
        for (String op : writeOps) {
            when(request.getMethod()).thenReturn(op);
            for (String s : allowed) {
                assertFalse(underTest.isAllowed(request));
            }
        }
    }
    
    @Test
    public void parentURLsAreAllowed() {
        when(request.getPath()).thenReturn("v1.2/parents/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentParentAssociations");
        assertTrue(underTest.isAllowed(request));
        when(request.getPath()).thenReturn("v1.2/parents/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentParentAssociations/students");
        assertTrue(underTest.isAllowed(request));
        when(request.getPath()).thenReturn("v1/parents/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentParentAssociations/students");
        assertTrue(underTest.isAllowed(request));
        when(request.getPath()).thenReturn("v1./parents/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentParentAssociations/students");
        assertTrue(underTest.isAllowed(request));
    }
    
    @Test
    public void denyStudentURLs() {
        when(request.getPath()).thenReturn("v1.2/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentParentAssociations");
        assertFalse(underTest.isAllowed(request));
        when(request.getPath()).thenReturn("v1.2/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentParentAssociations/parents");
        assertFalse(underTest.isAllowed(request));
        when(request.getPath()).thenReturn("v2./students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentParentAssociations/parents");
        assertFalse(underTest.isAllowed(request));
        when(request.getPath()).thenReturn("v1/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentParentAssociations/parents");
        assertFalse(underTest.isAllowed(request));
    }
}
