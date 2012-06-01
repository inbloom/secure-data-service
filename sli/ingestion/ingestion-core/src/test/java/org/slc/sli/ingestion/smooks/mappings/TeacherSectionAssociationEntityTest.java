package org.slc.sli.ingestion.smooks.mappings;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
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
public class TeacherSectionAssociationEntityTest {

    @InjectMocks
    @Autowired
    private EntityValidator validator;

    @Mock
    private Repository<Entity> mockRepository;

    String xmlTestData = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">"

                + "<TeacherSectionAssociation>"
                + "<TeacherReference>"
                   + "<StaffIdentity>"
                      + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
                + "<Name>"
                   + "<PersonalTitlePrefix>Ms</PersonalTitlePrefix>"
                   + "<FirstName>Jane</FirstName>"
                   + "<MiddleName>Sarah</MiddleName>"
                   + "<LastSurname>Smith</LastSurname>"
                   + "<GenerationCodeSuffix>III</GenerationCodeSuffix>"
                   + "<MaidenName>Jimenez</MaidenName>"
               + "</Name>"
               + "<OtherName OtherNameType=\"Alias\">"
                   + "<PersonalTitlePrefix>Ms</PersonalTitlePrefix>"
                   + "<FirstName>Jo</FirstName>"
                   + "<MiddleName>Gannon</MiddleName>"
                   + "<LastSurname>Grant</LastSurname>"
                   + "<GenerationCodeSuffix>II</GenerationCodeSuffix>"
               + "</OtherName>"
               + "<Sex>Female</Sex>"
                   + "<BirthDate>1999-07-12</BirthDate>"
                   + "<HispanicLatinoEthnicity>true</HispanicLatinoEthnicity>"
                   + "<Race>"
                        + "<RacialCategory>White</RacialCategory>"
                   + "</Race>"
               + "<Telephone TelephoneNumberType=\"Mobile\" PrimaryTelephoneNumberIndicator=\"true\">"
                   + "<TelephoneNumber>410-555-0248</TelephoneNumber>"
               + "</Telephone>"
               + "<ElectronicMail EmailAddressType=\"Home/Personal\">"
                   + "<EmailAddress>sjsmith@email.com</EmailAddress>"
               + "</ElectronicMail>"
               +       "<StaffIdentificationCode IdentificationSystem=\"NCES Pilot SNCCS course code\" AssigningOrganizationCode=\"ELU\">"
               +            "<Id>23</Id>"
               +       "</StaffIdentificationCode>"
                   + "</StaffIdentity>"
                + "</TeacherReference>"
                + "<SectionReference>"
                   + "<SectionIdentity>"
                      + "<UniqueSectionCode>123456111</UniqueSectionCode>"
                      + "<LocalCourseCode>MATH1</LocalCourseCode>"
                      + "<SchoolYear>2010-2011</SchoolYear>"
                      + "<Term>Summer Semester</Term>"
                      + "<ClassPeriodName>A03</ClassPeriodName>"
                      + "<Location>CC100</Location>"
                      +       "<CourseCode IdentificationSystem=\"NCES Pilot SNCCS course code\" AssigningOrganizationCode=\"ELU\">"
                      +            "<Id>23</Id>"
                      +       "</CourseCode>"
                   + "</SectionIdentity>"
                + "</SectionReference>"
                + "<ClassroomPosition>Teacher of Record</ClassroomPosition>"
                + "<BeginDate>1998-01-01</BeginDate>"
                + "<EndDate>2008-01-01</EndDate>"
                + "<HighlyQualifiedTeacher>true</HighlyQualifiedTeacher>"
          + "</TeacherSectionAssociation>"
          + "</InterchangeStaffAssociation>";

    @Test
    public void testValidatorTeacherSectionAssociation() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSectionAssociation";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, xmlTestData);

        // mock repository will simulate "finding" the references

        Mockito.when(mockRepository.exists("staff", "333333332")).thenReturn(true);
        Mockito.when(mockRepository.exists("section", "123456111")).thenReturn(true);

        EntityTestUtils.mapValidation(record.getAttributes(), "teacherSectionAssociation", validator);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidTeacherSectionAssociationMissingTeacherReference() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSectionAssociation";

    String invalidXmlMissingTeacherReference = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">"

                + "<TeacherSectionAssociation>"
                + "<SectionReference>"
                   + "<SectionIdentity>"
                      + "<UniqueSectionCode>123456111</UniqueSectionCode>"
                   + "</SectionIdentity>"
                + "</SectionReference>"
                + "<ClassroomPosition>Teacher of Record</ClassroomPosition>"
          + "</TeacherSectionAssociation>"
          + "</InterchangeStaffAssociation>";

    NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, invalidXmlMissingTeacherReference);

    Entity e = mock(Entity.class);
    when(e.getBody()).thenReturn(neutralRecord.getAttributes());
    when(e.getType()).thenReturn("teacherSectionAssociation");

    validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidTeacherSectionAssociationMissingSectionReference() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSectionAssociation";

    String invalidXmlMissingSectionReference = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">"

                + "<TeacherSectionAssociation>"
                + "<TeacherReference>"
                   + "<StaffIdentity>"
                      + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
                   + "</StaffIdentity>"
                + "</TeacherReference>"
                + "<ClassroomPosition>Teacher of Record</ClassroomPosition>"
          + "</TeacherSectionAssociation>"
          + "</InterchangeStaffAssociation>";

    NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, invalidXmlMissingSectionReference);

    Entity e = mock(Entity.class);
    when(e.getBody()).thenReturn(neutralRecord.getAttributes());
    when(e.getType()).thenReturn("teacherSectionAssociation");

    validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidTeacherSectionAssociationMissingClassroomPosition() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSectionAssociation";

    String invalidXmlMissingClassroomPosition = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">"

                + "<TeacherSectionAssociation>"
                + "<TeacherReference>"
                   + "<StaffIdentity>"
                      + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
                   + "</StaffIdentity>"
                + "</TeacherReference>"
                + "<SectionReference>"
                   + "<SectionIdentity>"
                      + "<UniqueSectionCode>123456111</UniqueSectionCode>"
                   + "</SectionIdentity>"
                + "</SectionReference>"
          + "</TeacherSectionAssociation>"
          + "</InterchangeStaffAssociation>";

    NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, invalidXmlMissingClassroomPosition);

    Entity e = mock(Entity.class);
    when(e.getBody()).thenReturn(neutralRecord.getAttributes());
    when(e.getType()).thenReturn("teacherSectionAssociation");

    validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidTeacherSectionAssociationIncorrectEnum() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSectionAssociation";

    String invalidXmlIncorrectEnum = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">"

                + "<TeacherSectionAssociation>"
                + "<TeacherReference>"
                   + "<StaffIdentity>"
                      + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
                   + "</StaffIdentity>"
                + "</TeacherReference>"
                + "<SectionReference>"
                   + "<SectionIdentity>"
                      + "<UniqueSectionCode>123456111</UniqueSectionCode>"
                   + "</SectionIdentity>"
                + "</SectionReference>"
                + "<ClassroomPosition>Teacher of Records</ClassroomPosition>"
          + "</TeacherSectionAssociation>"
          + "</InterchangeStaffAssociation>";

    NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, invalidXmlIncorrectEnum);

    Entity e = mock(Entity.class);
    when(e.getBody()).thenReturn(neutralRecord.getAttributes());
    when(e.getType()).thenReturn("teacherSectionAssociation");

    validator.validate(e);

    }

    @Ignore
    @Test
    public void testValidTeacherSectionAssociationCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-teacherSectionAssociation-csv.xml";
        String targetSelector = "csv-record";

        String csvTestData = "333333332,NCES Pilot SNCCS course code,ELU,23,,,Jane,Sarah,Smith,Ms,III,Jimenez,Alias,Ms,Jo,Gannon,Grant,II,Female,1999-07-12,true,White,Mobile,410-555-0248,true,Home/Personal,sjsmith@email.com,123456111,Summer Semester,2010-2011,A03,CC100,MATH1,NCES Pilot SNCCS course code,ELU,23,1998-01-01,2008-01-01,true,Teacher of Record";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                csvTestData);

        checkValidTeacherSectionAssociationNeutralRecord(neutralRecord);

    }

    @Test
    public void testValidTeacherSectionAssociationXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSectionAssociation";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig,
                targetSelector, xmlTestData);

        checkValidTeacherSectionAssociationNeutralRecord(neutralRecord);
   }

   private void checkValidTeacherSectionAssociationNeutralRecord(NeutralRecord record) {
       Map<String, Object> entity = record.getAttributes();

       Assert.assertEquals("333333332", entity.get("teacherId"));

       Assert.assertEquals("123456111", entity.get("sectionId"));

       Assert.assertEquals("Teacher of Record", entity.get("classroomPosition"));
       Assert.assertEquals("1998-01-01", entity.get("beginDate"));
       Assert.assertEquals("2008-01-01", entity.get("endDate"));
       Assert.assertEquals("true", entity.get("highlyQualifiedTeacher").toString());

   }
}
