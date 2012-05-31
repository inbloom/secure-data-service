package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.ingestion.validation.IngestionDummyEntityRepository;
import org.slc.sli.validation.EntityValidator;

/**
 * Test the smooks mappings for Course entity
 *
 * @author jtully
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class CourseEntityTest {

    @Autowired
    private EntityValidator validator;

    @Test
    public void testValidCourse() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrganization/Course";

        String testData = "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Course>"
                + "    <CourseTitle>Science7</CourseTitle>"
                + "    <NumberOfParts>7</NumberOfParts>"
                + "    <CourseCode IdentificationSystem=\"LEA course code\""
                + "        AssigningOrganizationCode=\"orgCode\">"
                + "        <ID>science7</ID>"
                + "    </CourseCode>"
                + "    <CourseCode IdentificationSystem=\"LEA course code\""
                + "        AssigningOrganizationCode=\"orgCode2\">"
                + "        <ID>science72</ID>"
                + "    </CourseCode>"
                + "    <CourseLevel>Honors</CourseLevel>"
                + "    <CourseLevelCharacteristics>"
                + "        <CourseLevelCharacteristic>Advanced</CourseLevelCharacteristic>"
                + "    </CourseLevelCharacteristics>"
                + "    <GradesOffered>"
                + "        <GradeLevel>Third grade</GradeLevel>"
                + "    </GradesOffered>"
                + "    <SubjectArea>Science</SubjectArea>"
                + "    <CourseDescription>A seventh grade science course</CourseDescription>"
                + "    <DateCourseAdopted>2012-02-01</DateCourseAdopted>"
                + "    <HighSchoolCourseRequirement>true</HighSchoolCourseRequirement>"
                + "    <CourseGPAApplicability>Applicable</CourseGPAApplicability>"
                + "    <CourseDefinedBy>LEA</CourseDefinedBy>"
                + "    <MinimumAvailableCredit CreditType=\"Carnegie unit\""
                + "        CreditConversion=\"1.0\">"
                + "        <Credit>1.0</Credit>"
                + "    </MinimumAvailableCredit>"
                + "    <MaximumAvailableCredit CreditType=\"Carnegie unit\""
                + "        CreditConversion=\"1.0\">"
                + "        <Credit>2.0</Credit>"
                + "    </MaximumAvailableCredit>"
                + "    <CareerPathway>Science, Technology, Engineering and Mathematics</CareerPathway>"
                + "    <EducationOrganizationReference>"
                + "        <EducationalOrgIdentity>"
                + "            <EducationOrgIdentificationCode IdentificationSystem=\"School\">"
                + "                <ID>ID1</ID>"
                + "            </EducationOrgIdentificationCode>"
                + "        </EducationalOrgIdentity>"
                + "    </EducationOrganizationReference>"
                + "</Course>"
                + "</InterchangeEducationOrganization>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, testData);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("course");

        IngestionDummyEntityRepository repo = mock(IngestionDummyEntityRepository.class);
        when(repo.exists("educationOrganization", "ID1")).thenReturn(true);
        PrivateAccessor.setField(validator, "validationRepo", repo);

        Assert.assertTrue(validator.validate(e));
    }

    @Test
    public void edfiXmlCourseTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeEducationOrganization/Course";

        String edfiCourseXml = "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Course>"
                + "    <CourseTitle>Science7</CourseTitle>"
                + "    <NumberOfParts>7</NumberOfParts>"
                + "    <CourseCode IdentificationSystem=\"LEA course code\""
                + "        AssigningOrganizationCode=\"orgCode\">"
                + "        <ID>science7</ID>"
                + "    </CourseCode>"
                + "    <CourseCode IdentificationSystem=\"LEA course code\""
                + "        AssigningOrganizationCode=\"orgCode2\">"
                + "        <ID>science72</ID>"
                + "    </CourseCode>"
                + "    <CourseLevel>Honors</CourseLevel>"
                + "    <CourseLevelCharacteristics>"
                + "        <CourseLevelCharacteristic>Advanced</CourseLevelCharacteristic>"
                + "    </CourseLevelCharacteristics>"
                + "    <GradesOffered>"
                + "        <GradeLevel>Seventh grade</GradeLevel>"
                + "        <GradeLevel>Eighth grade</GradeLevel>"
                + "    </GradesOffered>"
                + "    <SubjectArea>Science</SubjectArea>"
                + "    <CourseDescription>A seventh grade science course</CourseDescription>"
                + "    <DateCourseAdopted>2012-02-01</DateCourseAdopted>"
                + "    <HighSchoolCourseRequirement>true</HighSchoolCourseRequirement>"
                + "    <CourseGPAApplicability>Applicable</CourseGPAApplicability>"
                + "    <CourseDefinedBy>LEA</CourseDefinedBy>"
                + "    <MinimumAvailableCredit CreditType=\"Carnegie unit\""
                + "        CreditConversion=\"1.0\">"
                + "        <Credit>1.0</Credit>"
                + "    </MinimumAvailableCredit>"
                + "    <MaximumAvailableCredit CreditType=\"Carnegie unit\""
                + "        CreditConversion=\"1.0\">"
                + "        <Credit>2.0</Credit>"
                + "    </MaximumAvailableCredit>"
                + "    <CareerPathway>Science Technology Engineering and Mathematics</CareerPathway>"
                + "    <EducationOrganizationReference>"
                + "        <EducationalOrgIdentity>"
                + "            <EducationOrgIdentificationCode IdentificationSystem=\"School\">"
                + "                <ID>ID1</ID>"
                + "            </EducationOrgIdentificationCode>"
                + "        </EducationalOrgIdentity>"
                + "    </EducationOrganizationReference>"
                + "</Course>"
                + "</InterchangeEducationOrganization>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiCourseXml);

        checkValidCourseNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidCourseNeutralRecord(NeutralRecord neutralRecord) {

        assertEquals("course", neutralRecord.getRecordType());

        assertEquals("Science7", neutralRecord.getLocalId());

        assertEquals("Science7", neutralRecord.getAttributes().get("courseTitle"));

        List courseCodeList = (List) neutralRecord.getAttributes().get("courseCode");
        Map courseCodeMap = (Map) courseCodeList.get(0);
        EntityTestUtils.assertObjectInMapEquals(courseCodeMap, "identificationSystem", "LEA course code");
        EntityTestUtils.assertObjectInMapEquals(courseCodeMap, "assigningOrganizationCode", "orgCode");
        EntityTestUtils.assertObjectInMapEquals(courseCodeMap, "ID", "science7");
        if (courseCodeList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            Map courseCodeMap2 = (Map) courseCodeList.get(1);
            EntityTestUtils.assertObjectInMapEquals(courseCodeMap2, "identificationSystem", "LEA course code");
            EntityTestUtils.assertObjectInMapEquals(courseCodeMap2, "assigningOrganizationCode", "orgCode2");
            EntityTestUtils.assertObjectInMapEquals(courseCodeMap2, "ID", "science72");
        }

        assertEquals("Honors", neutralRecord.getAttributes().get("courseLevel"));

        List courseLevelCharacteristicList = (List) neutralRecord.getAttributes().get("courseLevelCharacteristics");
        assertEquals(1, courseLevelCharacteristicList.size());
        assertEquals("Advanced", courseLevelCharacteristicList.get(0));
        
        //check that all grades offered are reflected in entity
        List gradesOfferedList = (List) neutralRecord.getAttributes().get("gradesOffered");
        assertEquals(gradesOfferedList.size(), 2);
        assertEquals("Seventh grade", gradesOfferedList.get(0));
        assertEquals("Eighth grade", gradesOfferedList.get(1));
        

        assertEquals("Science", neutralRecord.getAttributes().get("subjectArea"));

        assertEquals("A seventh grade science course", neutralRecord.getAttributes().get("courseDescription"));

        assertEquals("2012-02-01", neutralRecord.getAttributes().get("dateCourseAdopted"));

        assertEquals(true, neutralRecord.getAttributes().get("highSchoolCourseRequirement"));

        assertEquals("Applicable", neutralRecord.getAttributes().get("courseGPAApplicability"));

        assertEquals("LEA", neutralRecord.getAttributes().get("courseDefinedBy"));

        Map minCreditMap = (Map) neutralRecord.getAttributes().get("minimumAvailableCredit");
        EntityTestUtils.assertObjectInMapEquals(minCreditMap, "creditType", "Carnegie unit");
        EntityTestUtils.assertObjectInMapEquals(minCreditMap, "creditConversion", 1.0);
        EntityTestUtils.assertObjectInMapEquals(minCreditMap, "credit", 1.0);

        Map maxCreditMap = (Map) neutralRecord.getAttributes().get("maximumAvailableCredit");
        EntityTestUtils.assertObjectInMapEquals(maxCreditMap, "creditType", "Carnegie unit");
        EntityTestUtils.assertObjectInMapEquals(maxCreditMap, "creditConversion", 1.0);
        EntityTestUtils.assertObjectInMapEquals(maxCreditMap, "credit", 2.0);

        assertEquals("Science Technology Engineering and Mathematics",
                neutralRecord.getAttributes().get("careerPathway"));
    }

}
