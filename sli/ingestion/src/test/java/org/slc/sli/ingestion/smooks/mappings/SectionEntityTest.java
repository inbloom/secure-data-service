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
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SectionEntityTest {

    @Autowired
    private EntityValidator validator;

    String xmlTestData = "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Section> "
            + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
            + "<SequenceOfCourse>1</SequenceOfCourse>"
            + "<EducationalEnvironment>Mainstream (Special Education)</EducationalEnvironment>"
            + "<MediumOfInstruction>Face-to-face instruction</MediumOfInstruction>"
            + "<PopulationServed>Regular Students</PopulationServed>"
            + "<AvailableCredit CreditType=\"Semester hour credit\" CreditConversion=\"0.05\">"
            +    "<Credit>0.05</Credit>"
            + "</AvailableCredit>"
            + "<CourseOfferingReference>"
            +    "<CourseOfferingIdentity>"
            +       "<LocalCourseCode>ELA4</LocalCourseCode>"
            +       "<Term>1</Term>"
            +       "<SchoolYear>1996-1997</SchoolYear>"
            +       "<CourseCode IdentificationSystem=\"NCES Pilot SNCCS course code\" AssigningOrganizationCode=\"ELU\">"
            +            "<Id>23</Id>"
            +       "</CourseCode>"
            +    "</CourseOfferingIdentity>"
            + "</CourseOfferingReference>"
            + "<SchoolReference>"
            +    "<EducationalOrgIdentity>"
            +       "<StateOrganizationId>152901001</StateOrganizationId>"
            +       "<EducationalOrgIdentificationCode IdentificationSystem=\"NCES Pilot SNCCS course code\">"
            +           "<Id>23</Id>"
            +       "</EducationalOrgIdentificationCode>"
            +    "</EducationalOrgIdentity>"
            + "</SchoolReference>"
            + "<SessionReference>"
            +   "<SessionIdentity>"
            +       "<SessionName>223</SessionName>"
            +       "<Term>2</Term>"
            +       "<SchoolYear>1997-1998</SchoolYear>"
            +   "</SessionIdentity>"
            + "</SessionReference>"
            + "<LocationReference>"
            +   "<LocationIdentity>"
            +       "<ClassroomIdentificationCode>ELU</ClassroomIdentificationCode>"
            +   "</LocationIdentity>"
            + "</LocationReference>"
            + "<ProgramReference>"
            +   "<ProgramIdentity>"
            +       "<ProgramId>223</ProgramId>"
            +       "<ProgramType>Bilingual</ProgramType>"
            +   "</ProgramIdentity>"
            + "</ProgramReference>"
        + "</Section>"
    + "</InterchangeEducationOrganization>";

    String csvTestData = "A-ELA4,1,Mainstream (Special Education),Face-to-face instruction,Regular Students,Semester hour credit,0.05,0.05,ELA4,1,1996-1997,NCES Pilot SNCCS course code,ELU,23,152901001,NCES Pilot SNCCS course code,23,223,2,1997-1998,ELU,,223,Bilingual";

    @Test
    public void testValidatorSection() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrganization/Section";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, xmlTestData);

        EntityTestUtils.mapValidation(neutralRecord.getAttributes(), "section", validator);

    }

    @Test
    public void testValidSectionCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-section-csv.xml";
        String targetSelector = "csv-record";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                csvTestData);

        checkValidSectionNeutralRecord(neutralRecord);

    }

    @Test
    public void testValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrganization/Section";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig,
                targetSelector, xmlTestData);

        checkValidSectionNeutralRecord(neutralRecord);

    }

    private void checkValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();

        Assert.assertEquals("A-ELA4", entity.get("uniqueSectionCode"));
        Assert.assertEquals("1", entity.get("sequenceOfCourse").toString());

        Assert.assertEquals("Mainstream (Special Education)", entity.get("educationalEnvironment"));
        Assert.assertEquals("Face-to-face instruction", entity.get("mediumOfInstruction"));
        Assert.assertEquals("Regular Students", entity.get("populationServed"));

        Map<String, Object> availableCredit = (Map<String, Object>) entity.get("availableCredit");
        Assert.assertTrue(availableCredit != null);
        Assert.assertEquals("Semester hour credit", availableCredit.get("creditType"));
        Assert.assertEquals("0.05", availableCredit.get("creditConversion").toString());
        Assert.assertEquals("0.05", availableCredit.get("credit").toString());

        Assert.assertEquals("ELA4", entity.get("courseOfferingReference"));

        Assert.assertEquals("152901001", entity.get("schoolReference"));

        Assert.assertEquals("223", entity.get("sessionReference"));

        List<String> programReferenceList = (List<String>) entity.get("programReference");
        Assert.assertTrue(programReferenceList != null);
        Assert.assertEquals("223", programReferenceList.get(0));

    }
}
