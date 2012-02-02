package org.slc.sli.ingestion.smooks;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
*
* @author ablum
*
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SectionEntityTest {

    @Test
    public void testValidSectionCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-section-csv.xml";
        String targetSelector = "csv-record";

        String testData = "A-ELA4,1,Mainstream (Special Education),Face-to-face instruction,Regular Students,Semester hour credit,0.05,0.05,ELA4,1,1996-1997,NCES Pilot SNCCS course code,ELU,23,152901001,NCES Pilot SNCCS course code,23,223,2,1997-1998,ELU,,223,Bilingual";

        ByteArrayInputStream testInput = new ByteArrayInputStream(testData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            //Tests that the NeutralRecord was created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();
            checkValidSectionNeutralRecord(record);

        } finally {
            nrfr.close();
        }

    }

    @Test
    public void testValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrganization/Section";

        String testData = "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
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

        ByteArrayInputStream testInput = new ByteArrayInputStream(testData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            //Tests that the NeutralRecords were created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();

            checkValidSectionNeutralRecord(record);

        } finally {
           nrfr.close();
        }


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

        Map<String, Object> courseOfferingReference = (Map<String, Object>) entity.get("courseOfferingReference");
        Assert.assertTrue(courseOfferingReference != null);
        Assert.assertEquals("ELA4", courseOfferingReference.get("localCourseCode"));
        Assert.assertEquals("1", courseOfferingReference.get("term"));
        Assert.assertEquals("1996-1997", courseOfferingReference.get("schoolYear"));
        List<Map<String, Object>> courseCodeList = (List<Map<String, Object>>) courseOfferingReference.get("courseCode");
        Assert.assertTrue(courseCodeList != null);
        Map<String, Object> courseCode = courseCodeList.get(0);
        Assert.assertTrue(courseCode != null);
        Assert.assertEquals("NCES Pilot SNCCS course code", courseCode.get("identificationSystem"));
        Assert.assertEquals("ELU", courseCode.get("assigningOrganizationCode"));
        Assert.assertEquals("23", courseCode.get("iD"));

        Map<String, Object> schoolReference = (Map<String, Object>) entity.get("schoolReference");
        Assert.assertTrue(schoolReference != null);
        Assert.assertEquals("152901001", schoolReference.get("stateOrganizationId"));
        List<Map<String, Object>> educationalOrgIdentificationCodeList = (List<Map<String, Object>>) schoolReference.get("educationalOrgIdentificationCode");
        Assert.assertTrue(educationalOrgIdentificationCodeList != null);
        Map<String, Object> educationalOrgIdentificationCode = educationalOrgIdentificationCodeList.get(0);
        Assert.assertTrue(educationalOrgIdentificationCode != null);
        Assert.assertEquals("NCES Pilot SNCCS course code", educationalOrgIdentificationCode.get("identificationSystem"));
        Assert.assertEquals("23", educationalOrgIdentificationCode.get("iD"));

        Map<String, Object> locationReference = (Map<String, Object>) entity.get("locationReference");
        Assert.assertTrue(locationReference != null);
        Assert.assertEquals("ELU", locationReference.get("classroomIdentificationCode"));

        Map<String, Object> sessionReference = (Map<String, Object>) entity.get("sessionReference");
        Assert.assertTrue(sessionReference != null);
        Assert.assertEquals("223", sessionReference.get("sessionName"));
        Assert.assertEquals("2", sessionReference.get("term"));
        Assert.assertEquals("1997-1998", sessionReference.get("schoolYear"));

        List<Map<String, Object>> programReferenceList = (List<Map<String, Object>>) entity.get("programReference");
        Assert.assertTrue(programReferenceList != null);
        Map<String, Object> programReference = (Map<String, Object>) programReferenceList.get(0);
        Assert.assertTrue(programReference != null);
        Assert.assertEquals("223", programReference.get("programId"));
        Assert.assertEquals("Bilingual", programReference.get("programType"));

    }
}
