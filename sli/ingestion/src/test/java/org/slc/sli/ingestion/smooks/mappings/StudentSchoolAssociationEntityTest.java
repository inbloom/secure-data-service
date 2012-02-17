package org.slc.sli.ingestion.smooks.mappings;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.NeutralRecord;
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
            + " <SchoolChoiceTransfer>true</SchoolChoiceTransfer>"
            + " <ExitWithdrawDate>2011-09-12</ExitWithdrawDate>"
            + " <ExitWithdrawType>End of school year</ExitWithdrawType>"
            + " <EducationalPlans>"
            + "   <EducationalPlan>Full Time Employment</EducationalPlan>"
            + " </EducationalPlans>"
            + "</StudentSchoolAssociation>" + "</InterchangeStudentEnrollment>";

    private void checkValidSSANeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();
        Assert.assertEquals("900000001", entity.get("studentId"));
        Assert.assertEquals("990000001", entity.get("schoolId"));
        Assert.assertEquals("Eighth grade", entity.get("entryGradeLevel"));
        Assert.assertEquals("2012-01-17", entity.get("entryDate"));
        Assert.assertEquals("Next year school", entity.get("entryType"));
        Assert.assertEquals("false", entity.get("repeatGradeIndicator").toString());
        Assert.assertEquals("2011-09-12", entity.get("exitWithdrawDate"));
        Assert.assertEquals("End of school year", entity.get("exitWithdrawType"));
        Assert.assertEquals("true", entity.get("schoolChoiceTransfer").toString());
        List<?> educationalPlans = (List<?>) record.getAttributes().get("educationalPlans");
        Assert.assertTrue(educationalPlans != null);

        Assert.assertEquals("Full Time Employment", educationalPlans.get(0));

    }

    @Test
    public void testValidStudentSchoolAssociationCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-studentSchoolAssociation-csv.xml";
        String targetSelector = "csv-record";

        String testData = ",,,900000001,,,,,,,,,,,,,,,,,,,,,,,990000001,,,2012-01-17,Eighth grade,Next year school,false,true,2011-09-12,End of school year,true,Full Time Employment,Full";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, testData);
        checkValidSSANeutralRecord(record);

    }

    @Test
    public void testValidatorStudentSchoolAssociation() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSchoolAssociation";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, xmlTestData);
        EntityTestUtils.mapValidation(record.getAttributes(), "studentSchoolAssociation", validator);
    }

    @Test
    public void testValidStudentSchoolAssociationXML() throws Exception {

        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSchoolAssociation";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, xmlTestData);
        checkValidSSANeutralRecord(record);
    }

}
