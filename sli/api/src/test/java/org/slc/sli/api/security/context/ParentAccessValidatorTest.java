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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.container.ContainerRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.generic.util.ResourceMethod;

public class ParentAccessValidatorTest {
    
    @InjectMocks
    ParentAccessValidator underTest = new ParentAccessValidator();
    
    @Mock
    StudentAccessValidator studentValidator;
    
    ContainerRequest request;
    List<String> paths;

    @Before
    public void setUp() throws Exception {
        studentValidator = Mockito.mock(StudentAccessValidator.class);
        request = Mockito.mock(ContainerRequest.class);
        MockitoAnnotations.initMocks(this);
        when(request.getMethod()).thenReturn(ResourceMethod.GET.toString());
        when(request.getQueryParameters()).thenReturn(new MultivaluedMapImpl());
        when(request.getPathSegments()).thenAnswer(new Answer<List<PathSegment>>() {
            @Override
            public List<PathSegment> answer(InvocationOnMock invocation) throws Throwable {
                return buildSegment();
            }
        });
    }
    
    private List<PathSegment> buildSegment() {
        List<PathSegment> segs = new ArrayList<PathSegment>();
        for (final String s : paths) {
            segs.add(new PathSegment() {
                
                @Override
                public String getPath() {
                    return s;
                }
                
                @Override
                public MultivaluedMap<String, String> getMatrixParameters() {
                    return null;
                }
                
            });
        }
        return segs;
    }

    @Test
    public void followStudents() {
        paths = Arrays.asList("v1", ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, "ssa123", ResourceNames.STUDENTS);
        when(studentValidator.isReadAllowed(Matchers.anyListOf(String.class), any(MultivaluedMapImpl.class))).thenReturn(true);
        assertTrue(underTest.isAllowed(request));
        
        paths = Arrays.asList("v1", ResourceNames.TEACHERS, "teacher123", ResourceNames.TEACHER_SECTION_ASSOCIATIONS);
        when(studentValidator.isReadAllowed(Matchers.anyListOf(String.class), any(MultivaluedMapImpl.class))).thenReturn(false);
        assertFalse(underTest.isAllowed(request));
    }
    
    @Test
    public void parentCannotWriteNonParentOrStudentEntities() {
        List<String> allowed = Arrays.asList(ResourceNames.GRADES,
                ResourceNames.STUDENT_GRADEBOOK_ENTRIES,
                ResourceNames.STUDENT_ASSESSMENTS);
        
        for (String op : ResourceMethod.getWriteOps()) {
            when(request.getMethod()).thenReturn(op);
            for (String s : allowed) {
                paths = Arrays.asList("v1", s);
                assertFalse(underTest.isAllowed(request));
            }
        }
    }
    
    @Test
    public void parentCanWriteToParentAndStudent() {
        List<String> allowed = Arrays.asList(ResourceNames.PARENTS,
                ResourceNames.STUDENTS);
        Set<String> operations = new HashSet<String>(ResourceMethod.getWriteOps());
        operations.remove(ResourceMethod.DELETE.toString());
        operations.remove(ResourceMethod.POST.toString());
        for (String op : operations) {
            when(request.getMethod()).thenReturn(op);
            for (String s : allowed) {
                paths = Arrays.asList("v1", s);
                assertTrue(underTest.isAllowed(request));
            }
        }
    }

    @Test
    public void parentURLsAreAllowed() {
        paths = Arrays.asList("v1", "parents", "067198fd6da91e1aa8d67e28e850f224d6851713_id", "studentParentAssociations");
        assertTrue(underTest.isAllowed(request));
        paths = Arrays.asList("v1", "parents", "067198fd6da91e1aa8d67e28e850f224d6851713_id", "studentParentAssociations", "students");
        assertTrue(underTest.isAllowed(request));
    }
    
}
