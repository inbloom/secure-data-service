package org.slc.sli.validation;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
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
    private EntityValidator validator;

    @Autowired
    private SchemaRepository schemaRepo;
    
    @Autowired
    private DummyEntityRepository repo;
    
    @Before
    public void init() {
        repo.clean();
    }

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
        addDummyEntity("session", "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e");
        addDummyEntity("course", "53777181-3519-4111-9210-529350429899");
        addDummyEntity("session", "31e8e04f-5b1a-4631-91b3-a5433a735d3b");
        addDummyEntity("course", "93d33f0b-0f2e-43a2-b944-7d182253a79a");
        addDummyEntity("course", "a7444741-8ba1-424e-b83f-df88c57f8b8c");
        
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
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidTeacher() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/test/resources/teacher_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "teacher");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidSection() throws Exception {
        addDummyEntity("school", "eb3b8c35-f582-df23-e406-6947249a19f2");
        addDummyEntity("session", "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e");
        addDummyEntity("course", "53777181-3519-4111-9210-529350429899");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/test/resources/section_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "section");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidSection() throws Exception {
        addDummyCollection("school");
        addDummyCollection("session");
        addDummyCollection("course");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/test/resources/section_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "section");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidStudentSectionAssociation() throws Exception {
        addDummyEntity("section", "cb7a932f-2d44-800c-d574-cdb25a29fc76");
        addDummyEntity("student", "2899a720-4196-6112-9874-edde0e2541db");
        addDummyEntity("student", "9e6d1d73-a488-4311-877a-718b897a17c5");
        addDummyEntity("student", "54c6548e-1196-86ca-ad5c-b8d72496bf78");
        addDummyEntity("student", "a63ee073-cd6c-9a12-a124-fa6a1b4dfc7c");
        addDummyEntity("student", "51dbb0cd-4f25-2d58-b587-5fac7605e4b3");
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(
                    "src/test/resources/student_section_association_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "studentSectionAssociation");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStudentSectionAssociation() throws Exception {
        addDummyCollection("section");
        addDummyCollection("student");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(
                    "src/test/resources/student_section_association_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "studentSectionAssociation");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidSectionAssessmentAssociation() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(
                    "src/test/resources/section_assessment_association_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                mapValidation((Map<String, Object>) obj.get("body"), "sectionAssessmentAssociation");
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
        validator.setEntityRepository(repo);
        
        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(obj);
        when(e.getType()).thenReturn(schemaName);
        
        try {
            assertTrue(validator.validate(e));
        } catch (EntityValidationException ex) {
            for (ValidationError err : ex.getValidationErrors()) {
                System.err.println(err);
            }
            throw ex;
        }
    }

    private void addDummyEntity(String collection, String id) {
        repo.addEntity(collection, id,
                ValidationTestUtils.makeDummyEntity(collection, id));
    }

    private void addDummyCollection(String collection) {
        repo.addCollection(collection);
    }
}
