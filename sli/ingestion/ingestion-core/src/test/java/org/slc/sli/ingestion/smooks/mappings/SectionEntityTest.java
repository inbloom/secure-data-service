package org.slc.sli.ingestion.smooks.mappings;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.ingestion.validation.IngestionDummyEntityRepository;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private IngestionDummyEntityRepository repo;

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
    + "   <Section>                                                                                    "
    + "       <UniqueSectionCode>UniqueSectionCode0</UniqueSectionCode>                                "
    + "       <SequenceOfCourse>4</SequenceOfCourse>                                                   "
    + "       <EducationalEnvironment>Classroom</EducationalEnvironment>                               "
    + "       <MediumOfInstruction>Televised</MediumOfInstruction>                                     "
    + "       <PopulationServed>Regular Students</PopulationServed>                                    "
    + "       <AvailableCredit CreditType=\"Carnegie unit\" CreditConversion=\"0\">                                                                        "
    + "           <Credit>50.00</Credit>                                                               "
    + "       </AvailableCredit>                                                                       "
    + "       <CourseOfferingReference id=\"ID003\" ref=\"ID001\">                                                                "
    + "           <CourseOfferingIdentity>                                                             "
    + "               <LocalCourseCode>LocalCourseCode0</LocalCourseCode>                              "
    + "               <CourseCode IdentificationSystem=\"CSSC course code\" AssigningOrganizationCode=\"AssigningOrganizationCode1\">                             "
    + "                   <ID>ID0</ID>                                                                 "
    + "               </CourseCode>                                                                    "
    + "               <CourseCode IdentificationSystem=\"CSSC course code\" AssigningOrganizationCode=\"AssigningOrganizationCode3\">                             "
    + "                   <ID>ID1</ID>                                                                 "
    + "               </CourseCode>                                                                    "
    + "               <Term>Fall Semester</Term>                                                       "
    + "               <SchoolYear>1996-1997</SchoolYear>                                               "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID2</ID>                                                                 "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID3</ID>                                                                 "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <StateOrganizationId>StateOrganizationId0</StateOrganizationId>                  "
    + "           </CourseOfferingIdentity>                                                            "
    + "       </CourseOfferingReference>                                                               "
    + "       <SchoolReference id=\"ID005\" ref=\"ID005\">                                                                        "
    + "           <EducationalOrgIdentity>                                                             "
    + "               <StateOrganizationId>StateOrganizationId1</StateOrganizationId>                  "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID4</ID>                                                                 "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID5</ID>                                                                 "
    + "               </EducationOrgIdentificationCode>                                                "
    + "           </EducationalOrgIdentity>                                                            "
    + "       </SchoolReference>                                                                       "
    + "       <SessionReference id=\"ID007\" ref=\"ID003\">                                                                       "
    + "           <SessionIdentity>                                                                    "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID6</ID>                                                                 "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID7</ID>                                                                 "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID8</ID>                                                                 "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID9</ID>                                                                 "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <SessionName>SessionName0</SessionName>                                          "
    + "           </SessionIdentity>                                                                   "
    + "       </SessionReference>                                                                      "
    + "       <LocationReference id=\"ID009\" ref=\"ID000\">                                                                      "
    + "           <LocationIdentity>                                                                   "
    + "               <ClassroomIdentificationCode>ClassroomIdentificat</ClassroomIdentificationCode>  "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID10</ID>                                                                "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID11</ID>                                                                "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID12</ID>                                                                "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
    + "                   <ID>ID13</ID>                                                                "
    + "               </EducationOrgIdentificationCode>                                                "
    + "           </LocationIdentity>                                                                  "
    + "       </LocationReference>                                                                     "
    + "       <ClassPeriodReference id=\"ID011\" ref=\"ID008\">                                                                   "
    + "           <ClassPeriodIdentity>                                                                "
    + "               <ClassPeriodName>ClassPeriodName0</ClassPeriodName>                              "
    + "               <StateOrganizationId>StateOrganizationId2</StateOrganizationId>                  "
    + "               <StateOrganizationId>StateOrganizationId3</StateOrganizationId>                  "
    + "           </ClassPeriodIdentity>                                                               "
    + "       </ClassPeriodReference>                                                                  "
    + "       <ProgramReference id=\"ID013\" ref=\"ID011\">                                            "
    + "           <ProgramIdentity>                                                                    "
    + "               <ProgramType>Adult/Continuing Education</ProgramType>                            "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                 "
    + "                   <ID>ID14</ID>                                                                "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                 "
    + "                   <ID>ID15</ID>                                                                "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                 "
    + "                   <ID>ID16</ID>                                                                "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                 "
    + "                   <ID>ID17</ID>                                                                "
    + "               </EducationOrgIdentificationCode>                                                "
    + "           </ProgramIdentity>                                                                   "
    + "       </ProgramReference>                                                                      "
    + "       <ProgramReference id=\"ID015\" ref=\"ID008\">                                            "
    + "           <ProgramIdentity>                                                                    "
    + "               <ProgramId>ProgramId0</ProgramId>                                                "
    + "               <StateOrganizationId>StateOrganizationId4</StateOrganizationId>                  "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                 "
    + "                   <ID>ID18</ID>                                                                "
    + "               </EducationOrgIdentificationCode>                                                "
    + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                 "
    + "                   <ID>ID19</ID>                                                                "
    + "               </EducationOrgIdentificationCode>                                                "
    + "           </ProgramIdentity>                                                                   "
    + "       </ProgramReference>                                                                      "
    + "   </Section>                                                                                   "
    + "</InterchangeMasterSchedule>";


    public void testValidSection() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, validXmlTestData);

        Assert.assertEquals(4, neutralRecord.getLocalParentIds().size());
        Assert.assertNotNull(neutralRecord.getLocalParentIds().get("Course"));
        Assert.assertNotNull(neutralRecord.getLocalParentIds().get("educationOrganization#schoolId"));
        Assert.assertNotNull(neutralRecord.getLocalParentIds().get("Session"));
        Assert.assertNotNull(neutralRecord.getLocalParentIds().get("program#programReference"));

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        /*when(repo.find("section", "152901001")).thenReturn(makeDummyEntity("school", "152901001"));
        when(repo.find("session", "223")).thenReturn(makeDummyEntity("session", "223"));
        when(repo.find("course", "ELA4")).thenReturn(makeDummyEntity("ELA4", "152901001"));*/

        repo.addEntity("educationOrganization", "StateOrganizationId1", makeDummyEntity("educationOrganization", "StateOrganizationId1"));
        repo.addEntity("session", "SessionName0", makeDummyEntity("session", "SessionName0"));
        repo.addEntity("course", "LocalCourseCode0", makeDummyEntity("course", "LocalCourseCode0"));
        repo.addEntity("program", "ProgramId0", makeDummyEntity("program", "ProgramId0"));

        PrivateAccessor.setField(validator, "validationRepo", repo);

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

    @Ignore
    @Test
    public void testValidSectionCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-section-csv.xml";
        String targetSelector = "csv-record";

        String csvTestData = "UniqueSectionCode0,4,Classroom,Televised,Regular Students,Carnegie unit,0.0,50.0,LocalCourseCode0,1,1996-1997,NCES Pilot SNCCS course code,ELU,23,StateOrganizationId1,NCES Pilot SNCCS course code,23,SessionName0,2,1997-1998,ELU,,ProgramId0,Bilingual";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                csvTestData);

        checkValidSectionNeutralRecord(neutralRecord);

    }


    public void testValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig,
                targetSelector, validXmlTestData);

        checkValidSectionNeutralRecord(neutralRecord);

    }

    private void checkValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();

        Assert.assertEquals("UniqueSectionCode0", entity.get("uniqueSectionCode"));
        Assert.assertEquals("4", entity.get("sequenceOfCourse").toString());

        Assert.assertEquals("Classroom", entity.get("educationalEnvironment"));
        Assert.assertEquals("Televised", entity.get("mediumOfInstruction"));
        Assert.assertEquals("Regular Students", entity.get("populationServed"));

        @SuppressWarnings("unchecked")
        Map<String, Object> availableCredit = (Map<String, Object>) entity.get("availableCredit");
        Assert.assertTrue(availableCredit != null);
        Assert.assertEquals("Carnegie unit", availableCredit.get("creditType"));
        Assert.assertEquals("0.0", availableCredit.get("creditConversion").toString());
        Assert.assertEquals("50.0", availableCredit.get("credit").toString());

        Assert.assertEquals("LocalCourseCode0", entity.get("courseId"));

        Assert.assertEquals("StateOrganizationId1", entity.get("schoolId"));

        Assert.assertEquals("SessionName0", entity.get("sessionId"));

        @SuppressWarnings("unchecked")
        List<String> programReferenceList = (List<String>) entity.get("programReference");
        Assert.assertTrue(programReferenceList != null);
        Assert.assertEquals("ProgramId0", programReferenceList.get(0));
    }
}
