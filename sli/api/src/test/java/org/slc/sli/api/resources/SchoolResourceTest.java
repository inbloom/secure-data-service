package org.slc.sli.api.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.domain.School;
import org.slc.sli.domain.SchoolTestData;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.domain.enums.EntryType;
import org.slc.sli.domain.enums.GradeLevelType;

public class SchoolResourceTest extends ResourceTest {
    @Autowired
    SchoolResource schoolResource;
    
    @Before
    public void init() throws Exception {
        schoolResource.setUriInfo(buildMockUriInfo());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testAddAndDelete() throws Exception {
        School school = SchoolTestData.buildTestSchool1();
        school.setShortName("TEST SCHOOL");
        school.setSchoolId(1);
        
        schoolResource.setUriInfo(buildMockUriInfo());
        schoolResource.add(school);
        
        Response response = schoolResource.getAll();
        assertNotNull(response);
        
        Integer foundId = null;
        for (School s : (Collection<School>) response.getEntity()) {
            if (s.getShortName().equals(school.getShortName())) {
                foundId = s.getSchoolId();
                break;
            }
        }
        
        if (foundId == null) {
            fail("could not find test school");
        } else {
            schoolResource.delete(foundId);
        }
        response = schoolResource.getAll();
        for (School s : (Collection<School>) response.getEntity()) {
            if (s.getShortName().equals(school.getShortName())) {
                fail();
            }
        }
    }
    
    @Test
    public void testAssociationAddAndDelete() throws Exception {
        UriInfo uriInfo = super.buildMockUriInfo();
        final UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        final List<String> pathInvocations = new LinkedList<String>();
        final List<String> path = new LinkedList<String>();
        when(builder.path(anyString())).thenAnswer(new Answer<UriBuilder>() {
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                pathInvocations.add((String) invocation.getArguments()[0]);
                path.add((String) invocation.getArguments()[0]);
                return builder;
            }
        });
        when(builder.path(any(Class.class))).thenAnswer(new Answer<UriBuilder>() {
            
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                path.add("-" + ((Class<?>) invocation.getArguments()[0]).getSimpleName() + "-");
                return builder;
            }
            
        });
        when(builder.build()).thenAnswer(new Answer<URI>() {
            @Override
            public URI answer(InvocationOnMock invocation) throws Throwable {
                URI uri = new URI(StringUtils.join(path, "/"));
                path.clear();
                return uri;
            }
        });
        when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                path.add("..");
                return builder;
            }
        });
        when(uriInfo.getRequestUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                path.add(".");
                return builder;
            }
        });
        this.schoolResource.setUriInfo(uriInfo);
        
        int schoolId = 10;
        int studentId = 1;
        StudentSchoolAssociation ssa = new StudentSchoolAssociation();
        ssa.setEntryGradeLevel(GradeLevelType.FIRST_GRADE);
        ssa.setEntryType(EntryType.ORIGINAL);
        Response response = schoolResource.createAssociation(ssa, studentId, schoolId);
        assertEquals(204, response.getStatus());
        assertNotNull(response.getMetadata());
        
        verify(uriInfo, times(2)).getAbsolutePathBuilder();
        
        verify(builder).path(anyString());
        verify(builder).build();
        assertEquals(1, pathInvocations.size());
        
        int associationId = Integer.parseInt(pathInvocations.get(0));
        
        ssa.setEntryType(EntryType.REENTRY_NO_INTERRUPTION);
        Response updateResponse = schoolResource.updateAssociation(ssa, associationId, schoolId, studentId);
        assertEquals(204, updateResponse.getStatus());
        assertNull(updateResponse.getEntity());
        
        Response getResponse = schoolResource.getAssociation(studentId, schoolId, associationId);
        StudentSchoolAssociation getSsa = (StudentSchoolAssociation) getResponse.getEntity();
        assertEquals(200, getResponse.getStatus());
        assertEquals(GradeLevelType.FIRST_GRADE, getSsa.getEntryGradeLevel());
        assertEquals(EntryType.REENTRY_NO_INTERRUPTION, getSsa.getEntryType());
        assertEquals(Integer.valueOf(schoolId), getSsa.getSchoolId());
        assertEquals(Integer.valueOf(studentId), getSsa.getStudentId());
        assertEquals(Integer.valueOf(pathInvocations.get(0)), getSsa.getAssociationId());
        
        Response associationResponse = schoolResource.getSchoolStudentAssociations(schoolId);
        assertEquals(204, associationResponse.getStatus());
        MultivaluedMap<String, Object> associationMetaData = associationResponse.getMetadata();
        List<Object> associationLinks = associationMetaData.get("Link");
        assertTrue(associationLinks.contains("<./" + studentId + ">;rel=studentSchoolAssociations"));
        
        Response associationsForStudent = schoolResource.getAssociationForStudent(schoolId, studentId);
        assertEquals(204, associationsForStudent.getStatus());
        MultivaluedMap<String, Object> metaData = associationsForStudent.getMetadata();
        List<Object> links = metaData.get("Link");
        assertTrue(links.contains("<./" + associationId + ">;rel=studentSchoolAssociation"));
        assertTrue(links.contains("<../-StudentResource-/" + studentId + ">;rel=student"));
        assertTrue(links.contains("<../-SchoolResource-/" + schoolId + ">;rel=school"));
        
        schoolResource.deleteAssociation(getSsa.getAssociationId(), getSsa.getStudentId(), getSsa.getSchoolId());
        
        Response getResponse2 = schoolResource.getAssociation(getSsa.getAssociationId(), getSsa.getStudentId(),
                getSsa.getSchoolId());
        assertNull(getResponse2.getEntity());
        assertEquals(404, getResponse2.getStatus());
    }
    
    @Test
    public void testUpdateSchool() throws Exception {
        School school = SchoolTestData.buildTestSchool1();
        school.setSchoolId(42);
        
        schoolResource.setUriInfo(buildMockUriInfo());
        schoolResource.add(school);
        
        School schoolToUpdate = SchoolTestData.buildTestSchool1();
        schoolToUpdate.setSchoolId(42);
        String updatedSchoolName = "Bayside High School";
        schoolToUpdate.setFullName(updatedSchoolName);
        schoolResource.update(schoolToUpdate, 42);
        School returnedSchool = (School) schoolResource.getSchool(42).getEntity();
        assertEquals(updatedSchoolName, returnedSchool.getFullName());
        schoolResource.delete(42);
        
    }
}
