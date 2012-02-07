package org.slc.sli.ingestion.smooks;

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
* @author ablum
*
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TeacherSectionAssociationEntityTest {

    @Autowired
    EntityValidator validator;

    String csvTestData = "333333332,NCES Pilot SNCCS course code,ELU,23,,,Jane,Sarah,Smith,Ms,III,Jimenez,Alias,Ms,Jo,Gannon,Grant,II,Female,1999-07-12,true,White,Mobile,410-555-0248,true,Home/Personal,sjsmith@email.com,123456111,Summer Semester,2010-2011,A03,CC100,MATH1,NCES Pilot SNCCS course code,ELU,23,1998-01-01,2008-01-01,true,Teacher of Record";

    String xmlTestData = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">"

                + "<TeacherSectionAssociation>"
                + "<StaffReference>"
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
                + "</StaffReference>"
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

        ByteArrayInputStream testInput = new ByteArrayInputStream(xmlTestData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            //Tests that the NeutralRecord was created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();
            EntityTestUtils.mapValidation(record.getAttributes(), "teacherSectionAssociation", validator);

        } finally {
            if (nrfr != null) {
                nrfr.close();
            }
        }

    }

    @Test
    public void testValidTeacherSectionAssociationCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-teacherSectionAssociation-csv.xml";
        String targetSelector = "csv-record";

        ByteArrayInputStream testInput = new ByteArrayInputStream(csvTestData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            //Tests that the NeutralRecord was created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();
            checkValidTeacherSectionAssociationNeutralRecord(record);

        } finally {
            if (nrfr != null) {
                nrfr.close();
            }
        }

    }

    @Test
    public void testValidTeacherSectionAssociationXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSectionAssociation";

        ByteArrayInputStream testInput = new ByteArrayInputStream(xmlTestData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            //Tests that the NeutralRecords were created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();

            checkValidTeacherSectionAssociationNeutralRecord(record);

        } finally {
            if (nrfr != null) {
                nrfr.close();
            }
        }
   }

   private void checkValidTeacherSectionAssociationNeutralRecord(NeutralRecord record) {
       Map<String, Object> entity = record.getAttributes();

       Assert.assertEquals("333333332", entity.get("teacherReference"));

       Assert.assertEquals("123456111", entity.get("sectionReference"));

       Assert.assertEquals("Teacher of Record", entity.get("classroomPosition"));
       Assert.assertEquals("1998-01-01", entity.get("beginDate"));
       Assert.assertEquals("2008-01-01", entity.get("endDate"));
       Assert.assertEquals("true", entity.get("highlyQualifiedTeacher").toString());

   }
}
