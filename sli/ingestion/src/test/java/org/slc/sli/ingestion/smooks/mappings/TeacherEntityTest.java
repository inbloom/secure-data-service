package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Test the smooks mappings for Teacher entity
 *
 * @author dduran
 *
 */
public class TeacherEntityTest {

    @Test
    public void csvTeacherTest() throws Exception {

        String smooksConfig = "smooks_conf/smooks-teacher-csv.xml";

        String targetSelector = "csv-record";

        String teacherCsv = "111111111,District,OrgCode,111111111,verificationString,Dr.,Teacher,Jose,NotStaff,III,maiden name,"
                + "other name type,Mr.,shady,guy,alias,Jr.,Male,01-01-1971,home address,100 10th street,1A,building site number,"
                + "New York,NY,10021,New York,USA123,USA,245,432,01-01-1969,12-12-2012,cell,123-123-1234,true,primary,teacher@school.edu,"
                + "false,old ethnicity,first racial category,Bachelors,12,13,Certification,credential id,code value 123,Computer Science certificate,"
                + "Computer Science,ed org reference,Junior High (Grade Level 6-8),One Year,2005-09-25,2013-09-25,Doctoral degree,aTeacher,teacher123,true";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                teacherCsv);

        checkValidTeacherNeutralRecord(neutralRecord);
    }

    @Test
    public void edfiXmlTeacherTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStaffAssociation/Teacher";

        String edfiTeacherXml = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StaffAssociation.xsd\">"
                + "<Teacher>"
                + "<StaffUniqueStateId>111111111</StaffUniqueStateId>"
                + "<StaffIdentificationCode IdentificationSystem=\"District\" AssigningOrganizationCode=\"OrgCode\">"
                + "    <ID>111111111</ID>"
                + "</StaffIdentificationCode>"
                + "<Name Verification=\"verificationString\">"
                + "    <PersonalTitlePrefix>Dr.</PersonalTitlePrefix>"
                + "    <FirstName>Teacher</FirstName>"
                + "    <MiddleName>Jose</MiddleName>"
                + "    <LastSurname>NotStaff</LastSurname>"
                + "    <MaidenName>maiden name</MaidenName>"
                + "    <GenerationCodeSuffix>III</GenerationCodeSuffix>"
                + "</Name>"
                + "<OtherName OtherNameType=\"other name type\">"
                + "    <PersonalTitlePrefix>Mr.</PersonalTitlePrefix>"
                + "    <FirstName>shady</FirstName>"
                + "    <MiddleName>guy</MiddleName>"
                + "    <LastSurname>alias</LastSurname>"
                + "    <GenerationCodeSuffix>Jr.</GenerationCodeSuffix>"
                + "</OtherName>"
                + "<Sex>Male</Sex>"
                + "<BirthDate>01-01-1971</BirthDate>"
                + "<Address AddressType=\"home address\">"
                + "    <StreetNumberName>100 10th street</StreetNumberName>"
                + "    <ApartmentRoomSuiteNumber>1A</ApartmentRoomSuiteNumber>"
                + "    <BuildingSiteNumber>building site number</BuildingSiteNumber>"
                + "    <City>New York</City>"
                + "    <StateAbbreviation>NY</StateAbbreviation>"
                + "    <PostalCode>10021</PostalCode>"
                + "    <NameOfCounty>New York</NameOfCounty>"
                + "    <CountyFIPSCode>USA123</CountyFIPSCode>"
                + "    <CountryCode>USA</CountryCode>"
                + "    <Latitude>245</Latitude>"
                + "    <Longitude>432</Longitude>"
                + "    <OpenDate>01-01-1969</OpenDate>"
                + "    <CloseDate>12-12-2012</CloseDate>"
                + "</Address>"
                + "<Telephone PrimaryTelephoneNumberIndicator=\"true\" TelephoneNumberType=\"cell\">"
                + "    <TelephoneNumber>123-123-1234</TelephoneNumber>"
                + "</Telephone>"
                + "<ElectronicMail EmailAddressType=\"primary\">"
                + "    <EmailAddress>teacher@school.edu</EmailAddress>"
                + "</ElectronicMail>"
                + "<HispanicLatinoEthnicity>false</HispanicLatinoEthnicity>"
                + "<OldEthnicity>old ethnicity</OldEthnicity>"
                + "<Race>"
                + "    <RacialCategory>first racial category</RacialCategory>"
                + "    <RacialCategory>second racial category</RacialCategory>"
                + "</Race>"
                + "<HighestLevelOfEducationCompleted>Bachelors</HighestLevelOfEducationCompleted>"
                + "<YearsOfPriorProfessionalExperience>12</YearsOfPriorProfessionalExperience>"
                + "<YearsOfPriorTeachingExperience>13</YearsOfPriorTeachingExperience>"
                + "<Credentials>"
                + "    <CredentialType>Certification</CredentialType>"
                + "    <CredentialField id=\"credential id\">"
                + "        <CodeValue>code value 123</CodeValue>"
                + "        <Description>Computer Science certificate</Description>"
                + "        <AcademicSubject>Computer Science</AcademicSubject>"
                + "        <EducationOrganizationReference>ed org reference</EducationOrganizationReference>"
                + "    </CredentialField>"
                + "    <Level>Junior High (Grade Level 6-8)</Level>"
                + "    <TeachingCredentialType>One Year</TeachingCredentialType>"
                + "    <CredentialIssuanceDate>2005-09-25</CredentialIssuanceDate>"
                + "    <CredentialExpirationDate>2013-09-25</CredentialExpirationDate>"
                + "    <TeachingCredentialBasis>Doctoral degree</TeachingCredentialBasis>"
                + "</Credentials>"
                + "<LoginId>aTeacher</LoginId>"
                + "<TeacherUniqueStateId>teacher123</TeacherUniqueStateId>"
                + "<HighlyQualifiedTeacher>true</HighlyQualifiedTeacher>" + "</Teacher></InterchangeStaffAssociation>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiTeacherXml);

        checkValidTeacherNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidTeacherNeutralRecord(NeutralRecord teacherNeutralRecord) {

        assertEquals("111111111", teacherNeutralRecord.getLocalId());
        assertEquals("111111111", teacherNeutralRecord.getAttributes().get("staffUniqueStateId"));

        List staffIdentificationCodeList = (List) teacherNeutralRecord.getAttributes().get("staffIdentificationCode");
        Map staffIdentificationCodeMap = (Map) staffIdentificationCodeList.get(0);
        EntityTestUtils.assertObjectInMapEquals(staffIdentificationCodeMap, "identificationSystem", "District");
        EntityTestUtils.assertObjectInMapEquals(staffIdentificationCodeMap, "id", "111111111");
        EntityTestUtils.assertObjectInMapEquals(staffIdentificationCodeMap, "assigningOrganizationCode", "OrgCode");

        Map nameMap = (Map) teacherNeutralRecord.getAttributes().get("name");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "verification", "verificationString");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "firstName", "Teacher");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "lastSurname", "NotStaff");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "personalTitlePrefix", "Dr.");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "middleName", "Jose");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "generationCodeSuffix", "III");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "maidenName", "maiden name");

        List otherNameList = (List) teacherNeutralRecord.getAttributes().get("otherName");
        Map otherNameMap = (Map) otherNameList.get(0);
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "otherNameType", "other name type");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "personalTitlePrefix", "Mr.");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "firstName", "shady");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "middleName", "guy");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "lastSurname", "alias");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "generationCodeSuffix", "Jr.");

        assertEquals("Male", teacherNeutralRecord.getAttributes().get("sex"));
        assertEquals("01-01-1971", teacherNeutralRecord.getAttributes().get("birthDate"));

        List addressList = (List) teacherNeutralRecord.getAttributes().get("address");
        Map addressMap = (Map) addressList.get(0);
        EntityTestUtils.assertObjectInMapEquals(addressMap, "addressType", "home address");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "streetNumberName", "100 10th street");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "apartmentRoomSuiteNumber", "1A");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "buildingSiteNumber", "building site number");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "city", "New York");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "stateAbbreviation", "NY");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "postalCode", "10021");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "nameOfCounty", "New York");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "countyFIPSCode", "USA123");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "countryCode", "USA");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "latitude", "245");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "longitude", "432");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "openDate", "01-01-1969");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "closeDate", "12-12-2012");

        List telephoneList = (List) teacherNeutralRecord.getAttributes().get("telephone");
        Map telephoneMap = (Map) telephoneList.get(0);
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "telephoneNumberType", "cell");
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "primaryTelephoneNumberIndicator", true);
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "telephoneNumber", "123-123-1234");

        List emailAddressList = (List) teacherNeutralRecord.getAttributes().get("electronicMail");
        Map emailAddressMap = (Map) emailAddressList.get(0);
        EntityTestUtils.assertObjectInMapEquals(emailAddressMap, "emailAddressType", "primary");
        EntityTestUtils.assertObjectInMapEquals(emailAddressMap, "emailAddress", "teacher@school.edu");

        assertEquals("old ethnicity", teacherNeutralRecord.getAttributes().get("oldEthnicity"));
        assertEquals(false, teacherNeutralRecord.getAttributes().get("hispanicLatinoEthnicity"));

        List raceList = (List) teacherNeutralRecord.getAttributes().get("race");
        assertEquals("first racial category", raceList.get(0));
        if (raceList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            assertEquals("second racial category", raceList.get(1));
        }

        assertEquals("Bachelors", teacherNeutralRecord.getAttributes().get("highestLevelOfEducationCompleted"));
        assertEquals(12, teacherNeutralRecord.getAttributes().get("yearsOfPriorProfessionalExperience"));
        assertEquals(13, teacherNeutralRecord.getAttributes().get("yearsOfPriorTeachingExperience"));

        List credentialsList = (List) teacherNeutralRecord.getAttributes().get("credentials");
        Map credentialsMap = (Map) credentialsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "credentialType", "Certification");
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "level", "Junior High (Grade Level 6-8)");
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "teachingCredentialType", "One Year");
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "credentialIssuanceDate", "2005-09-25");
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "credentialExpirationDate", "2013-09-25");
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "teachingCredentialBasis", "Doctoral degree");
        Map credentialFieldMap = (Map) credentialsMap.get("credentialField");
        EntityTestUtils.assertObjectInMapEquals(credentialFieldMap, "id", "credential id");
        EntityTestUtils.assertObjectInMapEquals(credentialFieldMap, "codeValue", "code value 123");
        EntityTestUtils.assertObjectInMapEquals(credentialFieldMap, "description", "Computer Science certificate");
        EntityTestUtils.assertObjectInMapEquals(credentialFieldMap, "academicSubject", "Computer Science");
        EntityTestUtils.assertObjectInMapEquals(credentialFieldMap, "educationOrganizationReference",
                "ed org reference");

        assertEquals("aTeacher", teacherNeutralRecord.getAttributes().get("loginId"));
        assertEquals("teacher123", teacherNeutralRecord.getAttributes().get("teacherUniqueStateId"));
        assertEquals(true, teacherNeutralRecord.getAttributes().get("highlyQualifiedTeacher"));

    }

}
