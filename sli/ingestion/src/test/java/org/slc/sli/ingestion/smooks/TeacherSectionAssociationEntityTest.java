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
    public void testValidTeacherSectionAssociationXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSectionAssociation";

        String testData ="<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">"

        		+"<TeacherSectionAssociation>"
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
                   + "<BirthDate>1999-07-12</BirthDate>"
                   + "<Sex>Female</Sex>"
               + "<Telephone TelephoneNumberType=\"Mobile\" PrimaryTelephoneNumberIndicator=\"true\">"
                   + "<TelephoneNumber>410-555-0248</TelephoneNumber>"
               + "</Telephone>"
               + "<ElectronicMail EmailAddressType=\"Home/Personal\">"
                   + "<EmailAddress>sjsmith@email.com</EmailAddress>"
               + "</ElectronicMail>"
               + "<HispanicLatinoEthnicity>true</HispanicLatinoEthnicity>"
               + "<Race>"
                    + "<RacialCategory>White</RacialCategory>"
               + "</Race>"
               +       "<StaffIdentificationCode IdentificationSystem=\"NCES Pilot SNCCS course code\" AssigningOrganizationCode=\"ELU\">"
               +            "<Id>23</Id>"
               +       "</StaffIdentificationCode>"
                   + "</StaffIdentity>"
                + "</TeacherReference>"
                + "<SectionReference>"
                   + "<SectionIdentity>"
                      + "<StateOrganizationId>123456111</StateOrganizationId>"
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

       List<Map<String, Object>> otherNameList = (List<Map<String, Object>>) teacherReference.get("educationalOrgIdentificationCode");
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
       Assert.assertEquals("410-555-0248", otherName.get("telephoneNumber"));
       Assert.assertEquals("true", otherName.get("primaryTelephoneNumberIndicator").toString());
       Assert.assertEquals("Mobile", otherName.get("telephoneNumberType"));

       List<Map<String, Object>> electronicMailList = (List<Map<String, Object>>) teacherReference.get("electronicMail");
       Assert.assertTrue(electronicMailList != null);
       Map<String, Object> electronicMail = (Map<String, Object>) electronicMailList.get(0);
       Assert.assertTrue(electronicMail != null);
       Assert.assertEquals("Home/Personal", otherName.get("emailAddressType"));
       Assert.assertEquals("sjsmith@email.com", otherName.get("emailAddress"));

       List<Map<String, Object>> StaffIdentificationCodeList = (List<Map<String, Object>>) teacherReference.get("StaffIdentificationCode");
       Assert.assertTrue(StaffIdentificationCodeList != null);
       Map<String, Object> StaffIdentificationCode = StaffIdentificationCodeList.get(0);
       Assert.assertTrue(StaffIdentificationCode != null);
       Assert.assertEquals("NCES Pilot SNCCS course code", StaffIdentificationCode.get("identificationSystem"));
       Assert.assertEquals("ELU", StaffIdentificationCode.get("assigningOrganizationCode"));
       Assert.assertEquals("23", StaffIdentificationCode.get("iD"));

       Map<String, Object> sectionReference = (Map<String, Object>) entity.get("sectionReference");
       Assert.assertTrue(sectionReference != null);
       Assert.assertEquals("123456111", sectionReference.get("stateOrganizationId"));
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

       Assert.assertEquals("Teacher of Record", sectionReference.get("classroomPosition"));
       Assert.assertEquals("1998-01-01", sectionReference.get("beginDate"));
       Assert.assertEquals("2008-01-01", sectionReference.get("endDate"));
       Assert.assertEquals("true", sectionReference.get("highlyQualifiedTeacher").toString());

   }
}
