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
public class TeacherSectionAssociationEntityTest {

    @Test
    public void testValidTeacherSectionAssociationCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-teacherSectionAssociation-csv.xml";
        String targetSelector = "csv-record";

        String testData = "333333332,NCES Pilot SNCCS course code,ELU,23,,,Jane,Sarah,Smith,Ms,III,Jimenez,Alias,Ms,Jo,Gannon,Grant,II,Female,1999-07-12,true,White,Mobile,410-555-0248,true,Home/Personal,sjsmith@email.com,123456111,Summer Semester,2010-2011,A03,CC100,MATH1,NCES Pilot SNCCS course code,ELU,23,1998-01-01,2008-01-01,true,Teacher of Record";

        ByteArrayInputStream testInput = new ByteArrayInputStream(testData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            //Tests that the NeutralRecord was created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();
            checkValidTeacherSectionAssociationNeutralRecord(record);

        } finally {
            nrfr.close();
        }

    }

    @Test
    public void testValidTeacherSectionAssociationXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSectionAssociation";

        String testData ="<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">"

        		+"<TeacherSectionAssociation>"
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

        ByteArrayInputStream testInput = new ByteArrayInputStream(testData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            //Tests that the NeutralRecords were created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();

            checkValidTeacherSectionAssociationNeutralRecord(record);

        } finally {
           nrfr.close();
        }
   }

   private void checkValidTeacherSectionAssociationNeutralRecord(NeutralRecord record) {
       Map<String, Object> entity = record.getAttributes();

       Map<String, Object> teacherReference = (Map<String, Object>) entity.get("teacherReference");
       Assert.assertTrue(teacherReference != null);
       Assert.assertEquals("333333332", teacherReference.get("staffUniqueStateId"));
       Assert.assertEquals("Female", teacherReference.get("sex"));
       Assert.assertEquals("true", teacherReference.get("hispanicLatinoEthnicity").toString());
       Assert.assertEquals("White", teacherReference.get("race"));

       Map<String, Object> name = (Map<String, Object>) teacherReference.get("name");
       Assert.assertTrue(name != null);
       Assert.assertEquals("Ms", name.get("personalTitlePrefix"));
       Assert.assertEquals("Jane", name.get("firstName"));
       Assert.assertEquals("Sarah", name.get("middleName"));
       Assert.assertEquals("Smith", name.get("lastSurname"));
       Assert.assertEquals("III", name.get("generationCodeSuffix"));
       Assert.assertEquals("Jimenez", name.get("maidenName"));

       List<Map<String, Object>> otherNameList = (List<Map<String, Object>>) teacherReference.get("otherName");
       Assert.assertTrue(otherNameList != null);
       Map<String, Object> otherName = (Map<String, Object>) otherNameList.get(0);
       Assert.assertTrue(otherName != null);
       Assert.assertEquals("Ms", otherName.get("personalTitlePrefix"));
       Assert.assertEquals("Jo", otherName.get("firstName"));
       Assert.assertEquals("Gannon", otherName.get("middleName"));
       Assert.assertEquals("Grant", otherName.get("lastSurname"));
       Assert.assertEquals("II", otherName.get("generationCodeSuffix"));

       List<Map<String, Object>> telephoneList = (List<Map<String, Object>>) teacherReference.get("telephone");
       Assert.assertTrue(telephoneList != null);
       Map<String, Object> telephone = (Map<String, Object>) telephoneList.get(0);
       Assert.assertTrue(telephone != null);
       Assert.assertEquals("410-555-0248", telephone.get("telephoneNumber"));
       Assert.assertEquals("true", telephone.get("primaryTelephoneNumberIndicator").toString());
       Assert.assertEquals("Mobile", telephone.get("telephoneNumberType"));

       List<Map<String, Object>> electronicMailList = (List<Map<String, Object>>) teacherReference.get("electronicMail");
       Assert.assertTrue(electronicMailList != null);
       Map<String, Object> electronicMail = (Map<String, Object>) electronicMailList.get(0);
       Assert.assertTrue(electronicMail != null);
       Assert.assertEquals("Home/Personal", electronicMail.get("emailAddressType"));
       Assert.assertEquals("sjsmith@email.com", electronicMail.get("emailAddress"));

       List<Map<String, Object>> StaffIdentificationCodeList = (List<Map<String, Object>>) teacherReference.get("staffIdentificationCode");
       Assert.assertTrue(StaffIdentificationCodeList != null);
       Map<String, Object> StaffIdentificationCode = StaffIdentificationCodeList.get(0);
       Assert.assertTrue(StaffIdentificationCode != null);
       Assert.assertEquals("NCES Pilot SNCCS course code", StaffIdentificationCode.get("identificationSystem"));
       Assert.assertEquals("ELU", StaffIdentificationCode.get("assigningOrganizationCode"));
       Assert.assertEquals("23", StaffIdentificationCode.get("iD"));

       Map<String, Object> sectionReference = (Map<String, Object>) entity.get("sectionReference");
       Assert.assertTrue(sectionReference != null);
       Assert.assertEquals("123456111", sectionReference.get("uniqueSectionCode"));
       Assert.assertEquals("MATH1", sectionReference.get("localCourseCode"));
       Assert.assertEquals("2010-2011", sectionReference.get("schoolYear"));
       Assert.assertEquals("Summer Semester", sectionReference.get("term"));
       Assert.assertEquals("A03", sectionReference.get("classPeriodName"));
       Assert.assertEquals("CC100", sectionReference.get("location"));

       Map<String, Object> courseCode = (Map<String, Object>) sectionReference.get("courseCode");
       Assert.assertTrue(courseCode != null);
       Assert.assertEquals("NCES Pilot SNCCS course code", courseCode.get("identificationSystem"));
       Assert.assertEquals("ELU", courseCode.get("assigningOrganizationCode"));
       Assert.assertEquals("23", courseCode.get("iD"));

       Assert.assertEquals("Teacher of Record", entity.get("classroomPosition"));
       Assert.assertEquals("1998-01-01", entity.get("beginDate"));
       Assert.assertEquals("2008-01-01", entity.get("endDate"));
       Assert.assertEquals("true", entity.get("highlyQualifiedTeacher").toString());

   }
}
