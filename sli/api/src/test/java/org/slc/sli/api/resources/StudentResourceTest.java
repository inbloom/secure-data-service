package org.slc.sli.api.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentBuilder;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.domain.enums.EntryType;
import org.slc.sli.domain.enums.GradeLevelType;

/**
 * Tests the student resource.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
public class StudentResourceTest extends ResourceTest {
    
    @Autowired
    private StudentResource studentResource;
    
    @Before
    public void init() throws Exception {
        studentResource.setUriInfo(buildMockUriInfo());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testAddAndDelete() throws Exception {
        
        Student studentNew = StudentBuilder.buildTestStudent();
        studentNew.setStudentId(1);
        
        studentResource.add(studentNew);
        
        Response response = studentResource.getAll();
        Collection<Student> students = (Collection<Student>) response.getEntity();
        
        // pop the last one off the list.
        // FIXME once we get the test database up
        students.iterator().next();
        
        Student student = students.iterator().next();
        
        studentResource.delete(student.getStudentId());
        
        response = studentResource.getAll();
        students = (Collection<Student>) response.getEntity();
        
        boolean contains = students.contains(student);
        assertFalse(contains);
        
        studentResource.add(student);
        
        response = studentResource.getAll();
        students = (Collection<Student>) response.getEntity();
        
        contains = students.contains(student);
        
        assertTrue(contains);
        
    }
    
    @Test
    public void testGetOne() throws Exception {
        
        Student studentNew = StudentBuilder.buildTestStudent();
        studentNew.setStudentId(2);
        
        studentResource.add(studentNew);
        
        Response response = studentResource.getStudent(studentNew.getStudentId());
        Student student = (Student) response.getEntity();
        
        assertEquals(student, studentNew);
        
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
        this.studentResource.setUriInfo(uriInfo);
        
        int schoolId = 1;
        int studentId = 10;
        StudentSchoolAssociation ssa = new StudentSchoolAssociation();
        ssa.setEntryGradeLevel(GradeLevelType.FIRST_GRADE);
        ssa.setEntryType(EntryType.ORIGINAL);
        Response response = studentResource.createAssociation(ssa, studentId, schoolId);
        assertEquals(204, response.getStatus());
        assertNotNull(response.getMetadata());
        
        verify(uriInfo, times(2)).getAbsolutePathBuilder();
        
        verify(builder).path(anyString());
        verify(builder).build();
        assertEquals(1, pathInvocations.size());
        
        int associationId = Integer.parseInt(pathInvocations.get(0));
        
        ssa.setEntryType(EntryType.REENTRY_NO_INTERRUPTION);
        Response updateResponse = studentResource.updateAssociation(ssa, associationId, schoolId, studentId);
        assertEquals(204, updateResponse.getStatus());
        assertNull(updateResponse.getEntity());
        
        Response getResponse = studentResource.getAssociation(studentId, schoolId, associationId);
        StudentSchoolAssociation getSsa = (StudentSchoolAssociation) getResponse.getEntity();
        assertEquals(200, getResponse.getStatus());
        assertEquals(GradeLevelType.FIRST_GRADE, getSsa.getEntryGradeLevel());
        assertEquals(EntryType.REENTRY_NO_INTERRUPTION, getSsa.getEntryType());
        assertEquals(Integer.valueOf(1), getSsa.getSchoolId());
        assertEquals(Integer.valueOf(10), getSsa.getStudentId());
        assertEquals(Integer.valueOf(pathInvocations.get(0)), getSsa.getAssociationId());
        
        Response associationResponse = studentResource.getSchoolStudentAssociations(studentId);
        assertEquals(204, associationResponse.getStatus());
        MultivaluedMap<String, Object> associationMetaData = associationResponse.getMetadata();
        List<Object> associationLinks = associationMetaData.get("Link");
        assertTrue(associationLinks.contains("<./" + schoolId + ">;rel=studentSchoolAssociations"));
        
        Response associationsForStudent = studentResource.getAssociationForStudent(schoolId, studentId);
        assertEquals(204, associationsForStudent.getStatus());
        MultivaluedMap<String, Object> metaData = associationsForStudent.getMetadata();
        List<Object> links = metaData.get("Link");
        assertTrue(links.contains("<./" + associationId + ">;rel=studentSchoolAssociation"));
        assertTrue(links.contains("<../-StudentResource-/" + studentId + ">;rel=student"));
        assertTrue(links.contains("<../-SchoolResource-/" + schoolId + ">;rel=school"));
        
        studentResource.deleteAssociation(getSsa.getAssociationId(), getSsa.getStudentId(), getSsa.getSchoolId());
        
        Response getResponse2 = studentResource.getAssociation(getSsa.getAssociationId(), getSsa.getStudentId(),
                getSsa.getSchoolId());
        assertNull(getResponse2.getEntity());
        assertEquals(404, getResponse2.getStatus());
    }
    
    @Test
    public void testUpdateStudent() throws Exception {
        Student initialStudent = StudentBuilder.buildTestStudent();
        initialStudent.setStudentId(42);
        studentResource.setUriInfo(buildMockUriInfo());
        studentResource.add(initialStudent);
        
        Student studentToUpdate = StudentBuilder.buildTestStudent();
        studentToUpdate.setCityOfBirth("Springfield");
        studentResource.update(studentToUpdate, 42);
        
        Student updatedStudent = (Student) studentResource.getStudent(42).getEntity();
        assertEquals("Springfield", updatedStudent.getCityOfBirth());
        
    }
}
