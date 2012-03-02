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
        addDummyEntity("student", "7afddec3-89ec-402c-8fe6-cced79ae3ef5");
        addDummyEntity("student", "1023bfc9-5cb8-4126-ae6a-4fefa74682c8");
        addDummyEntity("student", "034e6e7f-9da2-454a-b67c-b95bd9f36433");
        addDummyEntity("student", "bda1a4df-c155-4897-85c2-953926a3ebd8");
        addDummyEntity("student", "54c6546e-7998-4c6b-ad5c-b8d72496bf78");
        addDummyEntity("student", "714c1304-8a04-4e23-b043-4ad80eb60992");
        addDummyEntity("student", "e1af7127-743a-4437-ab15-5b0dacd1bde0");
        addDummyEntity("student", "61f13b73-92fa-4a86-aaab-84999c511148");
        addDummyEntity("student", "289c933b-ca69-448c-9afd-2c5879b7d221");
        addDummyEntity("student", "c7146300-5bb9-4cc6-8b95-9e401ce34a03");
        addDummyEntity("assessment", "6c572483-fe75-421c-9588-d82f1f5f3af5");
        addDummyEntity("assessment", "6a53f63e-deb8-443d-8138-fc5a7368239c");
        addDummyEntity("assessment", "7b2e6133-4224-4890-ac02-73962eb09645");
        addDummyEntity("assessment", "dd9165f2-65fe-4e27-a8ac-bec5f4b757f6");
        addDummyEntity("assessment", "a22532c4-6455-41da-b24d-4f93224f526d");
        addDummyEntity("assessment", "b5f684d4-9a12-40c3-a59e-0c0d1b971a1e");

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
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStudentAssessmentAssociation() throws Exception {
        addDummyCollection("student");
        addDummyCollection("assessment");

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
        addDummyEntity("teacher", "eb424dcc-6cff-a69b-c1b3-2b1fc86b2c94");
        addDummyEntity("section", "4efb4262-bc49-f388-0000-0000c9355700");
        addDummyEntity("section", "58c9ef19-c172-4798-8e6e-c73e68ffb5a3");
        addDummyEntity("section", "5c4b1a9c-2fcd-4fa0-b21c-f867cf4e7431");
        
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
	
	
    @SuppressWarnings("unchecked")
    @Test
    public void testValidStaffEducationOrganizationAssociation() throws Exception {
        this.addDummyEntity("staff", "269be4c9-a806-4051-a02d-15a7af3ffe3e");
        this.addDummyEntity("staff", "f0e41d87-92d4-4850-9262-ed2f2723159b");
        this.addDummyEntity("staff", "858bf25e-51b8-450a-ade6-adda0a570d9e");
        this.addDummyEntity("staff", "55015e96-56dd-4d13-a091-5cef847ca085");
        this.addDummyEntity("staff", "ad878c6d-4eaf-4a8a-8284-8fb6570cea64");
        this.addDummyEntity("staff", "0e26de79-222a-4d67-9201-5113ad50a03b");
        this.addDummyEntity("educationOrganization", "4f0c9368-8488-7b01-0000-000059f9ba56");
        this.addDummyEntity("educationOrganization", "2d7583b1-f8ec-45c9-a6da-acc4e6fde458");
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(
                    "src/test/resources/staff_educationOrganization_association_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                this.mapValidation((Map<String, Object>) obj.get("body"), "staffEducationOrganizationAssociation");
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
    public void testInvalidStaffEducationOrganizationAssociation() throws Exception {
        this.addDummyCollection("staff");
        this.addDummyCollection("educationOrganization");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(
                    "src/test/resources/staff_educationOrganization_association_fixture_neutral.json"));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                this.mapValidation((Map<String, Object>) obj.get("body"), "staffEducationOrganizationAssociation");
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
