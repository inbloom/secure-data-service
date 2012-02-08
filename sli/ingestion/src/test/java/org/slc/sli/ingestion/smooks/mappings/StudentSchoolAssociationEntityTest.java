package org.slc.sli.ingestion.smooks.mappings;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.validation.EntityValidator;

/**
 *
 * @author mpatel
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentSchoolAssociationEntityTest {

    @Autowired
    private EntityValidator validator;

    String xmlTestData = "<InterchangeStudentEnrollment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentEnrollment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<StudentSchoolAssociation>"
            + " <StudentReference>"
            + "  <StudentIdentity>"
            + "      <StudentUniqueStateId>900000001</StudentUniqueStateId>"
            + "  </StudentIdentity>"
            + " </StudentReference>"
            + " <SchoolReference>"
            + "  <EducationalOrgIdentity>"
            + "      <StateOrganizationId>990000001</StateOrganizationId>"
            + "  </EducationalOrgIdentity>"
            + " </SchoolReference>"
            + " <EntryDate>2012-01-17</EntryDate>"
            + " <EntryGradeLevel>Eighth grade</EntryGradeLevel>"
            + " <EntryType>Next year school</EntryType>"
            + " <GraduationPlan>Distinguished</GraduationPlan>"
            + " <RepeatGradeIndicator>false</RepeatGradeIndicator>"
            + " <EducationalPlans>"
            + "   <EducationalPlan>Full Time Employment</EducationalPlan>"
            + " </EducationalPlans>" + "</StudentSchoolAssociation>" + "</InterchangeStudentEnrollment>";

    private void checkValidSSANeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();
        Assert.assertEquals("900000001", entity.get("studentId"));
        Assert.assertEquals("990000001", entity.get("schoolId"));
        Assert.assertEquals("Eighth grade", entity.get("entryGradeLevel"));
        Assert.assertEquals("2012-01-17", entity.get("entryDate"));
        Assert.assertEquals("Next year school", entity.get("entryType"));
        // Assert.assertEquals("Full Time Employment", entity.get("educationPlans"));
        Assert.assertEquals("false", entity.get("repeatGradeIndicator").toString());
    }

    @Test
    public void testValidStudentSchoolAssociationCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-studentSchoolAssociation-csv.xml";
        String targetSelector = "csv-record";

        String testData = ",,,900000001,,,,,,,,,,,,,,,,,,,,,,,990000001,,,2012-01-17,Eighth grade,Next year school,false,,,,,Full Time Employment";

        ByteArrayInputStream testInput = new ByteArrayInputStream(testData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            // Tests that the NeutralRecord was created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();
            checkValidSSANeutralRecord(record);
        } finally {
            nrfr.close();
        }
    }

    @Test
    public void testValidatorStudentSchoolAssociation() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSchoolAssociation";

        ByteArrayInputStream testInput = new ByteArrayInputStream(xmlTestData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            // Tests that the NeutralRecord was created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();
            EntityTestUtils.mapValidation(record.getAttributes(), "studentSchoolAssociation", validator);

        } finally {
            nrfr.close();
        }

    }

    @Test
    public void testValidStudentSchoolAssociationXML() throws Exception {

        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSchoolAssociation";

        ByteArrayInputStream testInput = new ByteArrayInputStream(xmlTestData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);
            // Tests that the NeutralRecords were created
            Assert.assertTrue(nrfr.hasNext());
            NeutralRecord record = nrfr.next();
            checkValidSSANeutralRecord(record);
        } finally {
            nrfr.close();
        }

    }

}
