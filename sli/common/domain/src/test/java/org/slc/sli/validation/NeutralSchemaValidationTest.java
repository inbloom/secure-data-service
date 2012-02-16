package org.slc.sli.validation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.validation.schema.NeutralSchemaValidator;

/**
 * Tests sample fixture data against Neutral schema.
 * 
 * @author Dong Liu <dliu@wgen.net>
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class NeutralSchemaValidationTest {
    @Autowired
    private SchemaRepository schemaRepo;
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidSchool() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/test/resources/school_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "school");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidStudent() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/test/resources/student_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "student");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidAssessment() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/test/resources/assessment_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "assessment");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidStudentAssessmentAssociation() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(
                    "src/test/resources/student_assessment_association_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "studentAssessmentAssociation");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidTeacherSectionAssociation() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(
                    "src/test/resources/teacher_section_association_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "teacherSectionAssociation");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidSessionCourseAssociation() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(
                    "src/test/resources/session_course_association_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "sessionCourseAssociation");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void mapValidation(Map<String, Object> obj, String schemaName) {
        NeutralSchemaValidator validator = new NeutralSchemaValidator();
        validator.setSchemaRegistry(schemaRepo);
        
        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(obj);
        when(e.getType()).thenReturn(schemaName);
        
        try {
            assertTrue(validator.validate(e));
        } catch (EntityValidationException ex) {
            for (ValidationError err : ex.getValidationErrors()) {
                System.err.println(err);
            }
            fail();
        }
    }
}
