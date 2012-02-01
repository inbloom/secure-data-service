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
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.schema.ComplexSchema;
import org.slc.sli.validation.schema.DateSchema;
import org.slc.sli.validation.schema.IntegerSchema;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slc.sli.validation.schema.Restriction;
import org.slc.sli.validation.schema.StringSchema;

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
    XsdToNeutralSchemaRepo schemaRepo;
    
    @Test
    public void testSchema() throws IOException {
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        
        NeutralSchema schema = repo.getSchema("TestComplexType");
        assertNotNull(schema);
        assertEquals("TestComplexType", schema.getType());
        assertEquals(ComplexSchema.class.getCanonicalName(), schema.getValidatorClass());
        
        NeutralSchema testStringSchema = (NeutralSchema) schema.getFields().get("testString");
        assertNotNull(testStringSchema);
        assertEquals(NeutralSchemaType.STRING.getName(), testStringSchema.getType());
        assertEquals(StringSchema.class.getCanonicalName(), testStringSchema.getValidatorClass());
        
        NeutralSchema testSimpleReferenceSchema = (NeutralSchema) schema.getFields().get("testSimpleReference");
        assertNotNull(testSimpleReferenceSchema);
        assertEquals("BaseSimpleType", testSimpleReferenceSchema.getType());
        assertEquals(StringSchema.class.getCanonicalName(), testSimpleReferenceSchema.getValidatorClass());
        assertEquals("1", testSimpleReferenceSchema.getProperties().get(Restriction.MIN_LENGTH.getValue()));
        assertEquals("30", testSimpleReferenceSchema.getProperties().get(Restriction.MAX_LENGTH.getValue()));
        
        NeutralSchema testDateSchema = (NeutralSchema) schema.getFields().get("testDate");
        assertNotNull(testDateSchema);
        assertEquals("date", testDateSchema.getType());
        assertEquals(DateSchema.class.getCanonicalName(), testDateSchema.getValidatorClass());
        
        NeutralSchema anonSchema = (NeutralSchema) schema.getFields().get("testAnonymousSimpleType");
        assertNotNull(anonSchema);
        assertEquals("testAnonymousSimpleType", anonSchema.getType());
        assertEquals(IntegerSchema.class.getCanonicalName(), anonSchema.getValidatorClass());
        assertEquals("1", anonSchema.getProperties().get(Restriction.MIN_INCLUSIVE.getValue()));
        assertEquals("2", anonSchema.getProperties().get(Restriction.MAX_INCLUSIVE.getValue()));
        
        NeutralSchema extSchema = (NeutralSchema) schema.getFields().get("testComplexContentExtension");
        assertNotNull(extSchema);
        assertEquals("testComplexContentExtension", extSchema.getType());
        assertEquals(ComplexSchema.class.getCanonicalName(), extSchema.getValidatorClass());
        NeutralSchema extField1 = (NeutralSchema) extSchema.getFields().get("stringElement");
        assertNotNull(extField1);
        assertEquals("string", extField1.getType());
        assertEquals(StringSchema.class.getCanonicalName(), extField1.getValidatorClass());
        NeutralSchema extField2 = (NeutralSchema) extSchema.getFields().get("extension1");
        assertNotNull(extField2);
        assertEquals("complexType2", extField2.getType());
        assertEquals(ComplexSchema.class.getCanonicalName(), extField2.getValidatorClass());
        assertNotNull(extField2.getFields().get("intElement"));
        assertEquals(IntegerSchema.class.getCanonicalName(),
                ((NeutralSchema) extField2.getFields().get("intElement")).getValidatorClass());
        NeutralSchema extField3 = (NeutralSchema) extSchema.getFields().get("extension2");
        assertNotNull(extField3);
        assertEquals("BaseSimpleType", extField3.getType());
        assertEquals(StringSchema.class.getCanonicalName(), extField3.getValidatorClass());
        
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
