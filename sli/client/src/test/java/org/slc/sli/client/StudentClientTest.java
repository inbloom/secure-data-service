package org.slc.sli.client;

import static java.lang.Math.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.ws.rs.ext.ContextResolver;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.client.config.ClientJacksonContextResolver;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.enums.SexType;

/**
 * Tests the student client.
 * 
 * @author Dong Liu <dliu@wgen.net>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentClientTest {
    @Autowired
    private StudentClient studentClient;
    
    @Autowired
    private ApplicationContext context;
    private Student student;
    
    private String xmlInput;
    
    private String jsonInput;
    
    @Before
    public void init() throws Exception {
        student = new Student();
        student.setFirstName("Alex");
        student.setLastSurname("Taylor");
        // student.setStudentId( 1000 );
        // student.setId(UUID.randomUUID().toString());
        // student.setStudentSchoolId("123456789");
        student.setStudentSchoolId(String.valueOf((int) (random() * 1000000000)));
        student.setStudentId((int) (random() * 10000));
        System.out.println("studen id is:" + student.getStudentId());
        student.setBirthDate(new Date());
        student.setSex(SexType.Male);
        
        ContextResolver<ObjectMapper> resolver = new ClientJacksonContextResolver();
        studentClient.setContextResolver(resolver);
        Resource xmlResource = context.getResource("classpath:student/test-student.xml");
        Resource jsonResource = context.getResource("classpath:student/test-student.json");
        xmlInput = inputStreamToString(xmlResource.getInputStream());
        jsonInput = inputStreamToString(jsonResource.getInputStream());
        
    }
    
    @Test
    public void testXmlParser() throws Exception {
        List<Student> xmlStudents = studentClient.xmlParser(xmlInput);
        assertNotNull(xmlStudents);
        assertEquals(xmlStudents.size(), 8);
        Student xmlStudent = xmlStudents.get(0);
        assertNotNull(xmlStudent);
        assertEquals(xmlStudent.getFirstName(), "Harlan");
        assertEquals(xmlStudent.getLastSurname(), "Dennis");
    }
    
    @Test
    public void testJsonParser() throws Exception {
        List<Student> jsonStudents = studentClient.jsonParser(jsonInput);
        assertNotNull(jsonStudents);
        assertEquals(jsonStudents.size(), 8);
        Student jsonStudent = jsonStudents.get(0);
        assertNotNull(jsonStudent);
        assertEquals(jsonStudent.getFirstName(), "Harlan");
        assertEquals(jsonStudent.getLastSurname(), "Dennis");
        
    }
    
    // @Test
    public void testGetAll() throws Exception {
        List<Student> students = studentClient.getAll();
        assertNotNull(students);
        assertTrue(students.size() > 0);
        for (Student student : students) {
            System.out.println(student);
        }
        /*
         * student=students.get(0);
         * student.setFirstName("Alex");
         * student.setLastSurname("Taylor");
         * student.setStudentSchoolId("123456789");
         * studentClient.addNewStudent(student);
         * students=studentClient.getAll();
         * for(Student student:students){
         * System.out.println(student);
         * }
         */
        
    }
    
    // @Test
    public void testAddNewStudent() throws Exception {
        studentClient.addNewStudent(student);
        List<Student> students = studentClient.getAll();
        assertNotNull(students);
        for (Student student : students) {
            System.out.println(student);
        }
        
    }
    
    // @Test
    public void testDeleteStudent() throws Exception {
        
        List<Student> students = studentClient.getAll();
        student = students.get(0);
        studentClient.deleteStudent(student.getStudentId());
        students = studentClient.getAll();
        assertNotNull(students);
        // for(Student student:students){
        // System.out.println(student);
        // }
        
    }
    
    /*
     * test cases for Exception handling when the Jersey Web App (API) is Offline!
     * need to comment out these test cases when the (API) is Online!
     */
    
    /*
     * @Test(expected=Exception.class)
     * public void testGetAll() throws Exception{
     * List<Student> students=studentClient.getAll();
     * }
     * 
     * @Test(expected=Exception.class)
     * public void testAddNewStudent()throws Exception{
     * studentClient.addNewStudent(student);
     * }
     * 
     * @Test(expected=Exception.class)
     * public void testDeleteStudent()throws Exception{
     * studentClient.deleteStudent(student.getId());
     * }
     */
    
    private String inputStreamToString(InputStream input) throws Exception {
        StringWriter writer = new StringWriter();
        IOUtils.copy(input, writer);
        return writer.toString();
    }
}
