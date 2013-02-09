/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.validation.EntityValidator;

/**
 * Test the smooks mappings for Teacher entity
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/entity-mapping.xml" })
public class TeacherEntityTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLevelDeltaEnabledEntityNames;

    @Autowired
    EntityValidator validator;

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;
    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    String testData = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StaffAssociation.xsd\">"
            + "<Teacher>"
            + "<StaffUniqueStateId>111111111</StaffUniqueStateId>"
            + "<StaffIdentificationCode IdentificationSystem=\"District\" AssigningOrganizationCode=\"OrgCode\">"
            + "    <ID>111111111</ID>"
            + "</StaffIdentificationCode>"
            + "<StaffIdentificationCode IdentificationSystem=\"Drivers License\" AssigningOrganizationCode=\"OrgCode2\">"
            + "    <ID>1111111112</ID>"
            + "</StaffIdentificationCode>"
            + "<Name Verification=\"Birth certificate\">"
            + "    <PersonalTitlePrefix>Dr</PersonalTitlePrefix>"
            + "    <FirstName>Teacher</FirstName>"
            + "    <MiddleName>Jose</MiddleName>"
            + "    <LastSurname>NotStaff</LastSurname>"
            + "    <MaidenName>maiden name</MaidenName>"
            + "    <GenerationCodeSuffix>III</GenerationCodeSuffix>"
            + "</Name>"
            + "<OtherName OtherNameType=\"Alias\">"
            + "    <PersonalTitlePrefix>Mr</PersonalTitlePrefix>"
            + "    <FirstName>shady</FirstName>"
            + "    <MiddleName>guy</MiddleName>"
            + "    <LastSurname>alias</LastSurname>"
            + "    <GenerationCodeSuffix>Jr</GenerationCodeSuffix>"
            + "</OtherName>"
            + "<OtherName OtherNameType=\"Alias\">"
            + "    <PersonalTitlePrefix>Mr</PersonalTitlePrefix>"
            + "    <FirstName>shady2</FirstName>"
            + "    <MiddleName>guy2</MiddleName>"
            + "    <LastSurname>alias2</LastSurname>"
            + "    <GenerationCodeSuffix>Jr</GenerationCodeSuffix>"
            + "</OtherName>"
            + "<Sex>Male</Sex>"
            + "<BirthDate>1971-01-01</BirthDate>"
            + "<Address AddressType=\"Home\">"
            + "    <StreetNumberName>100 10th street</StreetNumberName>"
            + "    <ApartmentRoomSuiteNumber>1A</ApartmentRoomSuiteNumber>"
            + "    <BuildingSiteNumber>building site number</BuildingSiteNumber>"
            + "    <City>New York</City>"
            + "    <StateAbbreviation>NY</StateAbbreviation>"
            + "    <PostalCode>10021</PostalCode>"
            + "    <NameOfCounty>New York</NameOfCounty>"
            + "    <CountyFIPSCode>12345</CountyFIPSCode>"
            + "    <CountryCode>US</CountryCode>"
            + "    <Latitude>245</Latitude>"
            + "    <Longitude>432</Longitude>"
            + "    <OpenDate>1969-01-01</OpenDate>"
            + "    <CloseDate>2012-12-12</CloseDate>"
            + "</Address>"
            + "<Telephone PrimaryTelephoneNumberIndicator=\"true\" TelephoneNumberType=\"Mobile\">"
            + "    <TelephoneNumber>123-123-1234</TelephoneNumber>"
            + "</Telephone>"
            + "<ElectronicMail EmailAddressType=\"Work\">"
            + "    <EmailAddress>teacher@school.edu</EmailAddress>"
            + "</ElectronicMail>"
            + "<ElectronicMail EmailAddressType=\"Work\">"
            + "    <EmailAddress>teacher@home.com</EmailAddress>"
            + "</ElectronicMail>"
            + "<HispanicLatinoEthnicity>false</HispanicLatinoEthnicity>"
            + "<OldEthnicity>Hispanic</OldEthnicity>"
            + "<Race>"
            + "    <RacialCategory>White</RacialCategory>"
            + "</Race>"
            + "<HighestLevelOfEducationCompleted>Bachelor&apos;s</HighestLevelOfEducationCompleted>"
            + "<YearsOfPriorProfessionalExperience>12</YearsOfPriorProfessionalExperience>"
            + "<YearsOfPriorTeachingExperience>13</YearsOfPriorTeachingExperience>"
            + "<Credentials>"
            + "    <CredentialType>Certification</CredentialType>"
            + "    <CredentialField>"
            + "        <Description>Computer Science certificate</Description>"
            + "        <CodeValue>C110AW</CodeValue>"
            + "    </CredentialField>"
            + "    <Level>Junior High (Grade Level 6-8)</Level>"
            + "    <TeachingCredentialType>One Year</TeachingCredentialType>"
            + "    <CredentialIssuanceDate>2005-09-25</CredentialIssuanceDate>"
            + "    <CredentialExpirationDate>2013-09-25</CredentialExpirationDate>"
            + "    <TeachingCredentialBasis>Master's degree</TeachingCredentialBasis>"
            + "</Credentials>"
            + "<LoginId>aTeacher</LoginId>"
            + "<TeacherUniqueStateId>teacher123</TeacherUniqueStateId>"
            + "<HighlyQualifiedTeacher>true</HighlyQualifiedTeacher>" + "</Teacher></InterchangeStaffAssociation>";

    @Test
    public void testValidTeacher() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStaffAssociation/Teacher";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                testData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        Entity e = mock(Entity.class);

        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("teacher");

        Map<String, Object> attributes = neutralRecord.getAttributes();
        EntityTestUtils.mapValidation(attributes, "teacher", validator);
    }

    @Ignore
    @Test
    public void csvTeacherTest() throws Exception {

        String smooksConfig = "smooks_conf/smooks-teacher-csv.xml";

        String targetSelector = "csv-record";

        String teacherCsv = "111111111,District,OrgCode,111111111,Birth certificate,Dr,Teacher,Jose,NotStaff,III,maiden name,"
                + "Alias,Mr,shady,guy,alias,Jr,Male,1971-01-01,Home,100 10th street,1A,building site number,"
                + "New York,NY,10021,New York,12345,US,245,432,1969-01-01,2012-12-12,Mobile,123-123-1234,true,Work,teacher@school.edu,"
                + "false,Hispanic,White,Bachelor's,12,13,Certification,C110AW,Computer Science certificate,"
                + "Junior High (Grade Level 6-8),One Year,2005-09-25,2013-09-25,Doctoral degree,aTeacher,teacher123,true";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                teacherCsv, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidTeacherNeutralRecord(neutralRecord);
    }

    @Test
    public void edfiXmlTeacherTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStaffAssociation/Teacher";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, testData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidTeacherNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidTeacherNeutralRecord(NeutralRecord teacherNeutralRecord) {

        assertEquals("111111111", teacherNeutralRecord.getAttributes().get("staffUniqueStateId"));

        List staffIdentificationCodeList = (List) teacherNeutralRecord.getAttributes().get("staffIdentificationCode");
        Map staffIdentificationCodeMap = (Map) staffIdentificationCodeList.get(0);
        EntityTestUtils.assertObjectInMapEquals(staffIdentificationCodeMap, "identificationSystem", "District");
        EntityTestUtils.assertObjectInMapEquals(staffIdentificationCodeMap, "ID", "111111111");
        EntityTestUtils.assertObjectInMapEquals(staffIdentificationCodeMap, "assigningOrganizationCode", "OrgCode");
        if (staffIdentificationCodeList.size() > 1) {
            // TODO: remove when we support csv lists
            Map staffIdentificationCodeMap2 = (Map) staffIdentificationCodeList.get(1);
            EntityTestUtils.assertObjectInMapEquals(staffIdentificationCodeMap2, "identificationSystem", "Drivers License");
            EntityTestUtils.assertObjectInMapEquals(staffIdentificationCodeMap2, "ID", "1111111112");
            EntityTestUtils.assertObjectInMapEquals(staffIdentificationCodeMap2, "assigningOrganizationCode",
                    "OrgCode2");
        }

        Map nameMap = (Map) teacherNeutralRecord.getAttributes().get("name");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "verification", "Birth certificate");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "firstName", "Teacher");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "lastSurname", "NotStaff");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "personalTitlePrefix", "Dr");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "middleName", "Jose");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "generationCodeSuffix", "III");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "maidenName", "maiden name");

        List otherNameList = (List) teacherNeutralRecord.getAttributes().get("otherName");
        Map otherNameMap = (Map) otherNameList.get(0);
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "otherNameType", "Alias");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "personalTitlePrefix", "Mr");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "firstName", "shady");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "middleName", "guy");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "lastSurname", "alias");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "generationCodeSuffix", "Jr");
        if (otherNameList.size() > 1) {
            // TODO: remove if block when we support CSV lists
            Map otherNameMap2 = (Map) otherNameList.get(1);
            EntityTestUtils.assertObjectInMapEquals(otherNameMap2, "otherNameType", "Alias");
            EntityTestUtils.assertObjectInMapEquals(otherNameMap2, "personalTitlePrefix", "Mr");
            EntityTestUtils.assertObjectInMapEquals(otherNameMap2, "firstName", "shady2");
            EntityTestUtils.assertObjectInMapEquals(otherNameMap2, "middleName", "guy2");
            EntityTestUtils.assertObjectInMapEquals(otherNameMap2, "lastSurname", "alias2");
            EntityTestUtils.assertObjectInMapEquals(otherNameMap2, "generationCodeSuffix", "Jr");

        }

        assertEquals("Male", teacherNeutralRecord.getAttributes().get("sex"));
        assertEquals("1971-01-01", teacherNeutralRecord.getAttributes().get("birthDate"));

        List addressList = (List) teacherNeutralRecord.getAttributes().get("address");
        Map addressMap = (Map) addressList.get(0);
        EntityTestUtils.assertObjectInMapEquals(addressMap, "addressType", "Home");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "streetNumberName", "100 10th street");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "apartmentRoomSuiteNumber", "1A");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "buildingSiteNumber", "building site number");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "city", "New York");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "stateAbbreviation", "NY");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "postalCode", "10021");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "nameOfCounty", "New York");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "countyFIPSCode", "12345");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "countryCode", "US");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "latitude", "245");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "longitude", "432");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "openDate", "1969-01-01");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "closeDate", "2012-12-12");

        List telephoneList = (List) teacherNeutralRecord.getAttributes().get("telephone");
        Map telephoneMap = (Map) telephoneList.get(0);
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "telephoneNumberType", "Mobile");
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "primaryTelephoneNumberIndicator", true);
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "telephoneNumber", "123-123-1234");

        List emailAddressList = (List) teacherNeutralRecord.getAttributes().get("electronicMail");
        Map emailAddressMap = (Map) emailAddressList.get(0);
        EntityTestUtils.assertObjectInMapEquals(emailAddressMap, "emailAddressType", "Work");
        EntityTestUtils.assertObjectInMapEquals(emailAddressMap, "emailAddress", "teacher@school.edu");
        if (emailAddressList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            Map emailAddressMap2 = (Map) emailAddressList.get(1);
            EntityTestUtils.assertObjectInMapEquals(emailAddressMap2, "emailAddressType", "Work");
            EntityTestUtils.assertObjectInMapEquals(emailAddressMap2, "emailAddress", "teacher@home.com");
        }

        assertEquals("Hispanic", teacherNeutralRecord.getAttributes().get("oldEthnicity"));
        assertEquals(false, teacherNeutralRecord.getAttributes().get("hispanicLatinoEthnicity"));

        List raceList = (List) teacherNeutralRecord.getAttributes().get("race");
        assertNotNull(raceList);
        assertTrue(raceList.size() > 0);
        assertTrue(raceList.get(0) instanceof String);
        String raceCategory = (String) raceList.get(0);
        assertEquals("White", raceCategory);

        assertEquals("Bachelor's", teacherNeutralRecord.getAttributes().get("highestLevelOfEducationCompleted"));
        assertEquals(12, teacherNeutralRecord.getAttributes().get("yearsOfPriorProfessionalExperience"));
        assertEquals(13, teacherNeutralRecord.getAttributes().get("yearsOfPriorTeachingExperience"));

        List credentialsList = (List) teacherNeutralRecord.getAttributes().get("credentials");
        Map credentialsMap = (Map) credentialsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "credentialType", "Certification");
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "level", "Junior High (Grade Level 6-8)");
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "teachingCredentialType", "One Year");
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "credentialIssuanceDate", "2005-09-25");
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "credentialExpirationDate", "2013-09-25");
        EntityTestUtils.assertObjectInMapEquals(credentialsMap, "teachingCredentialBasis", "Master's degree");
        List credentialFieldList = (List) credentialsMap.get("credentialField");
        assertNotNull(credentialFieldList);
        assertEquals(2, credentialFieldList.size());
        Map credentialFieldMapDescription = (Map) credentialFieldList.get(0);
        assertNotNull(credentialFieldMapDescription);
        EntityTestUtils.assertObjectInMapEquals(credentialFieldMapDescription, "description", "Computer Science certificate");
        Map credentialFieldMapCodeValue = (Map) credentialFieldList.get(1);
        assertNotNull(credentialFieldMapCodeValue);
        EntityTestUtils.assertObjectInMapEquals(credentialFieldMapCodeValue, "codeValue", "C110AW");

        assertEquals("aTeacher", teacherNeutralRecord.getAttributes().get("loginId"));
        assertEquals("teacher123", teacherNeutralRecord.getAttributes().get("teacherUniqueStateId"));
        assertEquals(true, teacherNeutralRecord.getAttributes().get("highlyQualifiedTeacher"));

    }

}
