package org.slc.sli.validation.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.validation.NeutralSchemaFactory;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * JUnit for XsdToNeturalSchema
 * 
 * @author nbrown
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class XsdToNeutralSchemaTest {
    @Autowired
    SchemaRepository schemaRepo;
    
    @Test
    public void testSchema() throws IOException {
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        NeutralSchema schema = repo.getSchema("TestComplexType");
        assertNotNull(schema);
        assertEquals("TestComplexType", schema.getType());
        NeutralSchema testStringSchema = (NeutralSchema) schema.getFields().get("testString");
        assertNotNull(testStringSchema);
        assertEquals("string", testStringSchema.getType());
        NeutralSchema testSimpleReferenceSchema = (NeutralSchema) schema.getFields().get("testSimpleReference");
        assertNotNull(testSimpleReferenceSchema);
        assertEquals("BaseSimpleType", testSimpleReferenceSchema.getType());
        NeutralSchema testDateSchema = (NeutralSchema) schema.getFields().get("testDate");
        assertNotNull(testDateSchema);
        assertEquals("date", testDateSchema.getType());
    }
    
    @Test
    public void testSliXsdSchema() throws IOException {
        assertNotNull(schemaRepo);
        assertNull(schemaRepo.getSchema("non-exist-schema"));
        String[] testSchemas = { "student", "school", "teacher", "section", "assessment", "bellSchedule", "cohort",
                "course", "disciplineIncident", "educationOrgAssociation", "eventBellScheduleAssociation",
                "gradebookEntry", "localEducationAgency", "parent", "program", "schoolSessionAssociation",
                "sectionAssessmentAssociation", "sectionBellScheduleAssociation", "session", "staffCohortAssociation",
                "staffProgramAssociation", "studentAcademicRecordsAssociation", "studentAssessmentAssociation",
                "studentCohortAssociation", "studentDisciplineIncidentAssociation", "studentParentAssociation",
                "studentProgramAssociation", "studentSchoolAssociation", "studentSectionAssociation",
                "studentTranscriptsAssociation", "teacherSchoolAssociation", "teacherSectionAssociation" };
        for (String testSchema : testSchemas) {
            assertNotNull("cant find schema: " + testSchema, schemaRepo.getSchema(testSchema));
            assertEquals(schemaRepo.getSchema(testSchema).getType(), testSchema);
        }
        
    }
    
}
