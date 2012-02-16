package org.slc.sli.ingestion.smooks.mappings;

import static org.mockito.Mockito.mock;



import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.ingestion.validation.DummyEntityRepository;
import org.slc.sli.validation.EntityValidationException;
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

    @Autowired
    private DummyEntityRepository repo;

    private Entity makeDummyEntity(final String type, final String id) {
        return new Entity() {

            @Override
            public String getType() {
                return type;
            }

            @Override
            public Map<String, Object> getMetaData() {
                return new HashMap<String, Object>();
            }

            @Override
            public String getEntityId() {
                return id;
            }

            @Override
            public Map<String, Object> getBody() {
                return new HashMap<String, Object>();
            }
        };
    }

    String validXmlTestData = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Section> "
            + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
            + "<SequenceOfCourse>1</SequenceOfCourse>"
            + "<EducationalEnvironment>Mainstream (Special Education) </EducationalEnvironment>"
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
    + "</InterchangeMasterSchedule>";


    @Test
    public void testValidSection() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, validXmlTestData);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        /*when(repo.find("section", "152901001")).thenReturn(makeDummyEntity("school", "152901001"));
        when(repo.find("session", "223")).thenReturn(makeDummyEntity("session", "223"));
        when(repo.find("course", "ELA4")).thenReturn(makeDummyEntity("ELA4", "152901001"));*/

        repo.addEntity("school", "152901001", makeDummyEntity("school", "152901001"));
        repo.addEntity("session", "223", makeDummyEntity("session", "223"));
        repo.addEntity("course", "ELA4", makeDummyEntity("course", "ELA4"));
        repo.addEntity("program", "223", makeDummyEntity("program", "223"));

        junitx.util.PrivateAccessor.setField(validator, "validationRepo", repo);

        EntityTestUtils.mapValidation(neutralRecord.getAttributes(), "section", validator);
    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionMissingUniqueSectionCode() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlMissingUniqueSectionCode = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<SequenceOfCourse>1</SequenceOfCourse>"
                + "<CourseOfferingReference>"
                +    "<CourseOfferingIdentity>"
                +       "<LocalCourseCode>ELA4</LocalCourseCode>"
                +    "</CourseOfferingIdentity>"
                + "</CourseOfferingReference>"
                + "<SchoolReference>"
                +    "<EducationalOrgIdentity>"
                +       "<StateOrganizationId>152901001</StateOrganizationId>"
                +    "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<SessionReference>"
                +   "<SessionIdentity>"
                +       "<SessionName>223</SessionName>"
                +   "</SessionIdentity>"
                + "</SessionReference>"
            + "</Section>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, invalidXmlMissingUniqueSectionCode);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionMissingSequenceOfCourse() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlMissingSequenceOfCourse = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
                + "<CourseOfferingReference>"
                +    "<CourseOfferingIdentity>"
                +       "<LocalCourseCode>ELA4</LocalCourseCode>"
                +    "</CourseOfferingIdentity>"
                + "</CourseOfferingReference>"
                + "<SchoolReference>"
                +    "<EducationalOrgIdentity>"
                +       "<StateOrganizationId>152901001</StateOrganizationId>"
                +    "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<SessionReference>"
                +   "<SessionIdentity>"
                +       "<SessionName>223</SessionName>"
                +   "</SessionIdentity>"
                + "</SessionReference>"
            + "</Section>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, invalidXmlMissingSequenceOfCourse);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionMissingCourseOfferingReference() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlMissingCourseOfferingReference = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
                + "<SequenceOfCourse>1</SequenceOfCourse>"
                + "<SchoolReference>"
                +    "<EducationalOrgIdentity>"
                +       "<StateOrganizationId>152901001</StateOrganizationId>"
                +    "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<SessionReference>"
                +   "<SessionIdentity>"
                +       "<SessionName>223</SessionName>"
                +   "</SessionIdentity>"
                + "</SessionReference>"
            + "</Section>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, invalidXmlMissingCourseOfferingReference);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionMissingSchoolReference() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlMissingSchoolReference = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
                + "<SequenceOfCourse>1</SequenceOfCourse>"
                + "<CourseOfferingReference>"
                +    "<CourseOfferingIdentity>"
                +       "<LocalCourseCode>ELA4</LocalCourseCode>"
                +    "</CourseOfferingIdentity>"
                + "</CourseOfferingReference>"
                + "<SessionReference>"
                +   "<SessionIdentity>"
                +       "<SessionName>223</SessionName>"
                +   "</SessionIdentity>"
                + "</SessionReference>"
            + "</Section>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, invalidXmlMissingSchoolReference);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionMissingSessionReference() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlMissingSessionReference = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
                + "<SequenceOfCourse>1</SequenceOfCourse>"
                + "<CourseOfferingReference>"
                +    "<CourseOfferingIdentity>"
                +       "<LocalCourseCode>ELA4</LocalCourseCode>"
                +    "</CourseOfferingIdentity>"
                + "</CourseOfferingReference>"
                + "<SchoolReference>"
                +    "<EducationalOrgIdentity>"
                +       "<StateOrganizationId>152901001</StateOrganizationId>"
                +    "</EducationalOrgIdentity>"
                + "</SchoolReference>"
            + "</Section>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, invalidXmlMissingSessionReference);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionIncorrectEnum() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlIncorrectEnum = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
                + "<SequenceOfCourse>1</SequenceOfCourse>"
                + "<EducationalEnvironment>Mainstrean (Special Education) </EducationalEnvironment>"
                + "<CourseOfferingReference>"
                +    "<CourseOfferingIdentity>"
                +       "<LocalCourseCode>ELA4</LocalCourseCode>"
                +    "</CourseOfferingIdentity>"
                + "</CourseOfferingReference>"
                + "<SchoolReference>"
                +    "<EducationalOrgIdentity>"
                +       "<StateOrganizationId>152901001</StateOrganizationId>"
                +    "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<SessionReference>"
                +   "<SessionIdentity>"
                +       "<SessionName>223</SessionName>"
                +   "</SessionIdentity>"
                + "</SessionReference>"
            + "</Section>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, invalidXmlIncorrectEnum);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Test
    public void testValidSectionCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-section-csv.xml";
        String targetSelector = "csv-record";

        String csvTestData = "A-ELA4,1,Mainstream (Special Education) ,Face-to-face instruction,Regular Students,Semester hour credit,0.05,0.05,ELA4,1,1996-1997,NCES Pilot SNCCS course code,ELU,23,152901001,NCES Pilot SNCCS course code,23,223,2,1997-1998,ELU,,223,Bilingual";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                csvTestData);

        checkValidSectionNeutralRecord(neutralRecord);

    }

    @Test
    public void testValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig,
                targetSelector, validXmlTestData);

        checkValidSectionNeutralRecord(neutralRecord);

    }

    private void checkValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();

        Assert.assertEquals("A-ELA4", entity.get("uniqueSectionCode"));
        Assert.assertEquals("1", entity.get("sequenceOfCourse").toString());

        Assert.assertEquals("Mainstream (Special Education) ", entity.get("educationalEnvironment"));
        Assert.assertEquals("Face-to-face instruction", entity.get("mediumOfInstruction"));
        Assert.assertEquals("Regular Students", entity.get("populationServed"));

        @SuppressWarnings("unchecked")
        Map<String, Object> availableCredit = (Map<String, Object>) entity.get("availableCredit");
        Assert.assertTrue(availableCredit != null);
        Assert.assertEquals("Semester hour credit", availableCredit.get("creditType"));
        Assert.assertEquals("0.05", availableCredit.get("creditConversion").toString());
        Assert.assertEquals("0.05", availableCredit.get("credit").toString());

        Assert.assertEquals("ELA4", entity.get("courseId"));

        Assert.assertEquals("152901001", entity.get("schoolId"));

        Assert.assertEquals("223", entity.get("sessionId"));

        @SuppressWarnings("unchecked")
        List<String> programReferenceList = (List<String>) entity.get("programReference");
        Assert.assertTrue(programReferenceList != null);
        Assert.assertEquals("223", programReferenceList.get(0));
    }
}
