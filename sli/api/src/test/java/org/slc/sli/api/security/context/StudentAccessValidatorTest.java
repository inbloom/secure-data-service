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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.container.ContainerRequest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.common.constants.EntityNames;

public class StudentAccessValidatorTest {
    
    StudentAccessValidator underTest = new StudentAccessValidator();
    
    ContainerRequest request;
    
    List<String> paths;
    
    @Before
    public void setup() {
        request = Mockito.mock(ContainerRequest.class);
        when(request.getPathSegments()).thenAnswer(new Answer<List<PathSegment>>() {
            @Override
            public List<PathSegment> answer(InvocationOnMock invocation) throws Throwable {
                return buildSegment();
            }
        });
        when(request.getQueryParameters()).thenReturn(new MultivaluedMapImpl());
        when(request.getMethod()).thenReturn("GET");
    }

    @Test
    public void disciplineRelatedEntityShouldNeverBeAllowed() {
        paths = Arrays.asList("v1", "noise", "white_noise", EntityNames.DISCIPLINE_ACTION, "noise");
        assertFalse(underTest.isAllowed(request));
        // two parts
        paths = Arrays.asList("v1", ResourceNames.DISCIPLINE_ACTIONS, "id123");
        assertFalse(underTest.isAllowed(request));
        paths = Arrays.asList("v1", ResourceNames.DISCIPLINE_INCIDENTS, "id123");
        assertFalse(underTest.isAllowed(request));
        paths = Arrays.asList("v1", ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS, "id123");
        assertFalse(underTest.isAllowed(request));
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
    public void onePartWithQueryIsAllowed() {
//        // one part by itself is not allowed
//        paths = Arrays.asList("v1", ResourceNames.SCHOOLS);
//        assertFalse(underTest.isAllowed(request));
//
//        // but it's allowed if it comes with any queries
//        MultivaluedMapImpl queries = new MultivaluedMapImpl();
//        queries.add("parentEducationAgencyReference", "1b223f577827204a1c7e9c851dba06bea6b031fe_id");
//        when(request.getQueryParameters()).thenReturn(queries);
//        assertTrue(underTest.isAllowed(request));
    }
    
    @Test
    public void whiteListedURLAllowed() {
        paths = Arrays.asList("v1", ResourceNames.SESSIONS, "id123", ResourceNames.COURSE_OFFERINGS);
        assertTrue(underTest.isAllowed(request));
        
        paths = Arrays.asList("v1", ResourceNames.SESSIONS, "id123", ResourceNames.COURSE_OFFERINGS, ResourceNames.COURSES);
        assertTrue(underTest.isAllowed(request));

    }
    
    @Test
    public void notInWhiteListBlocked() {
        paths = Arrays.asList("v1", ResourceNames.SESSIONS, "id123", ResourceNames.STUDENT_ACADEMIC_RECORDS);
        assertFalse(underTest.isAllowed(request));
    }
    
    @Test
    public void twoPartsAreAllowed() {
        paths = Arrays.asList("v1", "anything", "id123");
        assertTrue(underTest.isAllowed(request));
    }
    
    @Test
    public void testAccessToHome() {
        paths = Arrays.asList("v1", "home");
        assertTrue(underTest.isAllowed(request));
    }
    
    @Test
    public void onePartPOSTOnPublicEntityDenied() {
        when(request.getMethod()).thenReturn("POST");
        paths = Arrays.asList("v1", "assessments");
        assertFalse(underTest.isAllowed(request));
    }
    
    @Test
    public void testAccessToSystem() {
        paths = Arrays.asList("v1", "system", "session");
        assertTrue(underTest.isAllowed(request));
        paths = Arrays.asList("v1", "system", "session", "debug");
        assertTrue(underTest.isAllowed(request));
        paths = Arrays.asList("v1", "system", "session", "check");
        assertTrue(underTest.isAllowed(request));
        paths = Arrays.asList("v1", "system", "session", "logout");
        assertTrue(underTest.isAllowed(request));
    }
    
    @Test
    public void writesAllowedForCertainEntities() {
        List<String> allowed = Arrays.asList(ResourceNames.GRADES,
                ResourceNames.STUDENT_GRADEBOOK_ENTRIES,
                ResourceNames.STUDENT_ASSESSMENTS,
                ResourceNames.STUDENTS);
        
        for (String op : ResourceMethod.getWriteOps()) {
            when(request.getMethod()).thenReturn(op);
            for (String s : allowed) {
                paths = Arrays.asList("v1", s);
                assertTrue(underTest.isAllowed(request));
            }
        }
    }
    
    @Test
    public void writesDeniedForOtherEntities() {
        List<String> writeOps = Arrays.asList(ResourceMethod.PUT.toString(),
                ResourceMethod.PATCH.toString(), ResourceMethod.DELETE.toString(), ResourceMethod.POST.toString());
        List<String> denied = Arrays.asList(ResourceNames.ASSESSMENTS,
                ResourceNames.GRADEBOOK_ENTRIES,
                ResourceNames.STUDENT_ACADEMIC_RECORDS,
                ResourceNames.STAFF);
        for (String op : writeOps) {
            when(request.getMethod()).thenReturn(op);
            for (String s : denied) {
                paths = Arrays.asList("v1", s);
                assertFalse(underTest.isAllowed(request));
            }
        }
    }

    @Ignore
    public void generateWhitelist() {
        for (String s : underTest.getAllWhiteLists()) {
            System.out.println(s);
        }
    }

}
