package org.slc.sli.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
// includes all contexts
public class StudentServiceTest {
    
    @Autowired(required = true)
    private StudentService studentService;
    
    @Before
    public void setUp() throws Exception {
        for (Student s : studentService.getAll()) {
            studentService.deleteById(s.getStudentId());
        }
        // Add some students
        Student student1 = StudentBuilder.buildTestStudent();
        studentService.addOrUpdate(student1);
        
        Student student2 = StudentBuilder.buildTestStudent2();
        studentService.addOrUpdate(student2);
    }
    
    @Test
    public void testGetAll() throws Exception {
        
        Collection<Student> students = studentService.getAll();
        boolean foundJane = false;
        for (Student student : students) {
            if ("Jane".equals(student.getFirstName())) {
                foundJane = true;
                break;
            }
        }
        assertTrue(foundJane);
        assertEquals(2, students.size());
    }
    
    @Test
    public void testDelete() throws Exception {
        Collection<Student> students = studentService.getAll();
        
        Student studentToDelete = students.iterator().next();
        
        studentService.deleteById(studentToDelete.getStudentId());
        
        assertFalse(studentService.getAll().contains(studentToDelete));
        studentService.addOrUpdate(studentToDelete);
    }
}
