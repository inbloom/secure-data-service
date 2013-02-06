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
 * Test the smooks mappings for Student entity
 *
 * @author bsuzuki
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/entity-mapping.xml" })
public class StudentEntityTest {

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

    @Test
    public void testValidStudent() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentParent/Student";

        String testData = "<InterchangeStudentParent xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentParent.xsd\">"
                + "<Student>"
                + "<StudentUniqueStateId>231101422</StudentUniqueStateId>"
                + "<StudentIdentificationCode IdentificationSystem=\"District\" AssigningOrganizationCode=\"OrgCode\">"
                + "    <IdentificationCode>231101422</IdentificationCode>"
                + "</StudentIdentificationCode>"
                + "<Name Verification=\"Birth certificate\">"
                + "    <PersonalTitlePrefix>Mr</PersonalTitlePrefix>"
                + "    <FirstName>Alfonso</FirstName>"
                + "    <MiddleName>Ora</MiddleName>"
                + "    <LastSurname>Steele</LastSurname>"
                + "    <GenerationCodeSuffix>Jr</GenerationCodeSuffix>"
                + "    <MaidenName>Jimenez</MaidenName>"
                + "</Name>"
                + "<OtherName OtherNameType=\"Alias\">"
                + "    <PersonalTitlePrefix>Mr</PersonalTitlePrefix>"
                + "    <FirstName>Alden</FirstName>"
                + "    <MiddleName>Gannon</MiddleName>"
                + "    <LastSurname>Horne</LastSurname>"
                + "    <GenerationCodeSuffix>II</GenerationCodeSuffix>"
                + "</OtherName>"
                + "<Sex>Male</Sex>"
                + "<BirthData>"
                + "     <BirthDate>1999-07-12</BirthDate>"
                + "     <CityOfBirth>Baltimore</CityOfBirth>"
                + "     <StateOfBirthAbbreviation>MD</StateOfBirthAbbreviation>"
                + "     <CountryOfBirthCode>US</CountryOfBirthCode>"
                + "     <DateEnteredUS>2001-03-23</DateEnteredUS>"
                + "     <MultipleBirthStatus>false</MultipleBirthStatus>"
                + "</BirthData>"
                + "<Address AddressType=\"Home\">"
                + "    <StreetNumberName>555 Main Street</StreetNumberName>"
                + "    <ApartmentRoomSuiteNumber>1A</ApartmentRoomSuiteNumber>"
                + "    <BuildingSiteNumber>building site number</BuildingSiteNumber>"
                + "    <City>Baltimore</City>"
                + "    <StateAbbreviation>MD</StateAbbreviation>"
                + "    <PostalCode>21218</PostalCode>"
                + "    <NameOfCounty>Baltimore</NameOfCounty>"
                + "    <CountyFIPSCode>12345</CountyFIPSCode>"
                + "    <CountryCode>US</CountryCode>"
                + "    <Latitude>245</Latitude>"
                + "    <Longitude>432</Longitude>"
                + "    <OpenDate>1969-01-01</OpenDate>"
                + "    <CloseDate>2012-12-12</CloseDate>"
                + "</Address>"
                + "<Telephone TelephoneNumberType=\"Mobile\" PrimaryTelephoneNumberIndicator=\"true\">"
                + "    <TelephoneNumber>410-555-0248</TelephoneNumber>"
                + "</Telephone>"
                + "<ElectronicMail EmailAddressType=\"Home/Personal\">"
                + "    <EmailAddress>asteele@email.com</EmailAddress>"
                + "</ElectronicMail>"
                + "<ProfileThumbnail>slcedu.org/somePhoto.jpg</ProfileThumbnail>"
                + "<HispanicLatinoEthnicity>true</HispanicLatinoEthnicity>"
                + "<OldEthnicity>Hispanic</OldEthnicity>"
                + "<Race>"
                + "    <RacialCategory>White</RacialCategory>"
                + "</Race>"
                + "<EconomicDisadvantaged>true</EconomicDisadvantaged>"
                + "<SchoolFoodServicesEligibility>Free</SchoolFoodServicesEligibility>"
                + "<StudentCharacteristics>"
                + "    <Characteristic>Immigrant</Characteristic>"
                + "    <BeginDate>2005-01-11</BeginDate>"
                + "    <EndDate>2005-01-30</EndDate>"
                + "    <DesignatedBy>School</DesignatedBy>"
                + "</StudentCharacteristics>"
                + "<LimitedEnglishProficiency>NotLimited</LimitedEnglishProficiency>"
                + "<Languages>"
                + "    <Language>English</Language>"
                + "</Languages>"
                + "<HomeLanguages>"
                + "    <Language>English</Language>"
                + "</HomeLanguages>"
                + "<Disabilities>"
                + "    <Disability>Deafness</Disability>"
                + "    <DisabilityDiagnosis>Deaf</DisabilityDiagnosis>"
                + "    <OrderOfDisability>3</OrderOfDisability>"
                + "</Disabilities>"
                + "<Section504Disabilities>"
                + "    <Section504Disability>Other</Section504Disability>"
                + "</Section504Disabilities>"
                + "<DisplacementStatus>Other</DisplacementStatus>"
                + "<ProgramParticipations>"
                + "    <Program>Bilingual</Program>"
                + "    <BeginDate>2011-01-11</BeginDate>"
                + "    <EndDate>2011-01-11</EndDate>"
                + "    <DesignatedBy>School</DesignatedBy>"
                + "</ProgramParticipations>"
                + "<LearningStyles>"
                + "    <VisualLearning>1</VisualLearning>"
                + "    <AuditoryLearning>1</AuditoryLearning>"
                + "    <TactileLearning>1</TactileLearning>"
                + "</LearningStyles>"
                + "<CohortYears CohortYearType=\"Ninth grade\">"
                + "    <SchoolYear>2010-2011</SchoolYear>"
                + "</CohortYears>"
                + "<StudentIndicators>"
                + "    <IndicatorGroup>student</IndicatorGroup>"
                + "    <IndicatorName>student</IndicatorName>"
                + "    <Indicator>student</Indicator>"
                + "    <BeginDate>2011-01-11</BeginDate>"
                + "    <EndDate>2011-01-12</EndDate>"
                + "    <DesignatedBy>School</DesignatedBy>"
                + "</StudentIndicators>"
                + "<LoginId>aStudent</LoginId>"
                + "</Student></InterchangeStudentParent>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                testData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        Entity e = mock(Entity.class);

        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("student");


        EntityTestUtils.mapValidation(neutralRecord.getAttributes(), "student", validator);

    }

    @Ignore
    @Test
    public void csvStudentTest() throws Exception {

        String smooksConfig = "smooks_conf/smooks-student-csv.xml";

        String targetSelector = "csv-record";

        String studentCsv = "231101422,"
                + "District,OrgCode,231101422,"
                + "Birth certificate,Mr,Alfonso,Ora,Steele,Jr,Jimenez,"
                + "Alias,Mr,Alden,Gannon,Horne,II,"
                + "Male,"
                + "1999-07-12,Baltimore,MD,US,2001-03-23,false,"
                + "Home,555 Main Street,1A,building site number,Baltimore,MD,21218,Baltimore,12345,US,245,432,1969-01-01,2012-12-12,"
                + "Mobile,410-555-0248,true,"
                + "Home/Personal,asteele@email.com,"
                + "slcedu.org/somePhoto.jpg,"
                + "true,"
                + "Hispanic,"
                + "White,"
                + "true,"
                + "Free,"
                + "Immigrant,2005-01-11,2005-01-30,School,"
                + "NotLimited,"
                + "English,"
                + "English,"
                + "Deafness,Deaf,3,"
                + "Other,"
                + "Other,"
                + "Bilingual,2011-01-11,2011-01-11,School,"
                + "1,1,1,"
                + "Ninth grade,2010-2011,"
                + "student,student,student,2011-01-11,2011-01-12,School,aStudent";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                studentCsv, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidStudentNeutralRecord(neutralRecord);
    }

    @Test
    public void edfiXmlTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentParent/Student";

        String edfiStudentXml = "<InterchangeStudentParent xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentParent.xsd\">"
                + "<Student>"
                + "<StudentUniqueStateId>231101422</StudentUniqueStateId>"
                + "<StudentIdentificationCode IdentificationSystem=\"District\" AssigningOrganizationCode=\"OrgCode\">"
                + "    <IdentificationCode>231101422</IdentificationCode>"
                + "</StudentIdentificationCode>"
                + "<Name Verification=\"Birth certificate\">"
                + "    <PersonalTitlePrefix>Mr</PersonalTitlePrefix>"
                + "    <FirstName>Alfonso</FirstName>"
                + "    <MiddleName>Ora</MiddleName>"
                + "    <LastSurname>Steele</LastSurname>"
                + "    <GenerationCodeSuffix>Jr</GenerationCodeSuffix>"
                + "    <MaidenName>Jimenez</MaidenName>"
                + "</Name>"
                + "<OtherName OtherNameType=\"Alias\">"
                + "    <PersonalTitlePrefix>Mr</PersonalTitlePrefix>"
                + "    <FirstName>Alden</FirstName>"
                + "    <MiddleName>Gannon</MiddleName>"
                + "    <LastSurname>Horne</LastSurname>"
                + "    <GenerationCodeSuffix>II</GenerationCodeSuffix>"
                + "</OtherName>"
                + "<Sex>Male</Sex>"
                + "<BirthData>"
                + "     <BirthDate>1999-07-12</BirthDate>"
                + "     <CityOfBirth>Baltimore</CityOfBirth>"
                + "     <StateOfBirthAbbreviation>MD</StateOfBirthAbbreviation>"
                + "     <CountryOfBirthCode>US</CountryOfBirthCode>"
                + "     <DateEnteredUS>2001-03-23</DateEnteredUS>"
                + "     <MultipleBirthStatus>false</MultipleBirthStatus>"
                + "</BirthData>"
                + "<Address AddressType=\"Home\">"
                + "    <StreetNumberName>555 Main Street</StreetNumberName>"
                + "    <ApartmentRoomSuiteNumber>1A</ApartmentRoomSuiteNumber>"
                + "    <BuildingSiteNumber>building site number</BuildingSiteNumber>"
                + "    <City>Baltimore</City>"
                + "    <StateAbbreviation>MD</StateAbbreviation>"
                + "    <PostalCode>21218</PostalCode>"
                + "    <NameOfCounty>Baltimore</NameOfCounty>"
                + "    <CountyFIPSCode>12345</CountyFIPSCode>"
                + "    <CountryCode>US</CountryCode>"
                + "    <Latitude>245</Latitude>"
                + "    <Longitude>432</Longitude>"
                + "    <OpenDate>1969-01-01</OpenDate>"
                + "    <CloseDate>2012-12-12</CloseDate>"
                + "</Address>"
                + "<Telephone TelephoneNumberType=\"Mobile\" PrimaryTelephoneNumberIndicator=\"true\">"
                + "    <TelephoneNumber>410-555-0248</TelephoneNumber>"
                + "</Telephone>"
                + "<ElectronicMail EmailAddressType=\"Home/Personal\">"
                + "    <EmailAddress>asteele@email.com</EmailAddress>"
                + "</ElectronicMail>"
                + "<ProfileThumbnail>slcedu.org/somePhoto.jpg</ProfileThumbnail>"
                + "<HispanicLatinoEthnicity>true</HispanicLatinoEthnicity>"
                + "<OldEthnicity>Hispanic</OldEthnicity>"
                + "<Race>"
                + "    <RacialCategory>White</RacialCategory>"
                + "</Race>"
                + "<EconomicDisadvantaged>true</EconomicDisadvantaged>"
                + "<SchoolFoodServicesEligibility>Free</SchoolFoodServicesEligibility>"
                + "<StudentCharacteristics>"
                + "    <Characteristic>Immigrant</Characteristic>"
                + "    <BeginDate>2005-01-11</BeginDate>"
                + "    <EndDate>2005-01-30</EndDate>"
                + "    <DesignatedBy>School</DesignatedBy>"
                + "</StudentCharacteristics>"
                + "<LimitedEnglishProficiency>NotLimited</LimitedEnglishProficiency>"
                + "<Languages>"
                + "    <Language>English</Language>"
                + "</Languages>"
                + "<HomeLanguages>"
                + "    <Language>English</Language>"
                + "</HomeLanguages>"
                + "<Disabilities>"
                + "    <Disability>Deafness</Disability>"
                + "    <DisabilityDiagnosis>Deaf</DisabilityDiagnosis>"
                + "    <OrderOfDisability>3</OrderOfDisability>"
                + "</Disabilities>"
                + "<Section504Disabilities>"
                + "    <Section504Disability>Other</Section504Disability>"
                + "</Section504Disabilities>"
                + "<DisplacementStatus>Other</DisplacementStatus>"
                + "<ProgramParticipations>"
                + "    <Program>Bilingual</Program>"
                + "    <BeginDate>2011-01-11</BeginDate>"
                + "    <EndDate>2011-01-11</EndDate>"
                + "    <DesignatedBy>School</DesignatedBy>"
                + "</ProgramParticipations>"
                + "<LearningStyles>"
                + "    <VisualLearning>1</VisualLearning>"
                + "    <AuditoryLearning>1</AuditoryLearning>"
                + "    <TactileLearning>1</TactileLearning>"
                + "</LearningStyles>"
                + "<CohortYears CohortYearType=\"Ninth grade\">"
                + "    <SchoolYear>2010-2011</SchoolYear>"
                + "</CohortYears>"
                + "<StudentIndicators>"
                + "    <IndicatorGroup>student</IndicatorGroup>"
                + "    <IndicatorName>student</IndicatorName>"
                + "    <Indicator>student</Indicator>"
                + "    <BeginDate>2011-01-11</BeginDate>"
                + "    <EndDate>2011-01-12</EndDate>"
                + "    <DesignatedBy>School</DesignatedBy>"
                + "</StudentIndicators>"
                + "<LoginId>aStudent</LoginId>"
                + "</Student></InterchangeStudentParent>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiStudentXml, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidStudentNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidStudentNeutralRecord(NeutralRecord studentNeutralRecord) {

        assertEquals("231101422", studentNeutralRecord.getAttributes().get("studentUniqueStateId"));

        List studentIdentificationCodeList = (List) studentNeutralRecord.getAttributes().get("studentIdentificationCode");
        Map studentIdentificationCodeMap = (Map) studentIdentificationCodeList.get(0);
        EntityTestUtils.assertObjectInMapEquals(studentIdentificationCodeMap, "identificationSystem", "District");
        EntityTestUtils.assertObjectInMapEquals(studentIdentificationCodeMap, "identificationCode", "231101422");
        EntityTestUtils.assertObjectInMapEquals(studentIdentificationCodeMap, "assigningOrganizationCode", "OrgCode");

        Map nameMap = (Map) studentNeutralRecord.getAttributes().get("name");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "verification", "Birth certificate");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "firstName", "Alfonso");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "lastSurname", "Steele");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "personalTitlePrefix", "Mr");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "middleName", "Ora");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "generationCodeSuffix", "Jr");
        EntityTestUtils.assertObjectInMapEquals(nameMap, "maidenName", "Jimenez");

        List otherNameList = (List) studentNeutralRecord.getAttributes().get("otherName");
        Map otherNameMap = (Map) otherNameList.get(0);
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "otherNameType", "Alias");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "personalTitlePrefix", "Mr");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "firstName", "Alden");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "middleName", "Gannon");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "lastSurname", "Horne");
        EntityTestUtils.assertObjectInMapEquals(otherNameMap, "generationCodeSuffix", "II");

        assertEquals("Male", studentNeutralRecord.getAttributes().get("sex"));

        Map birthDataMap = (Map) studentNeutralRecord.getAttributes().get("birthData");
        EntityTestUtils.assertObjectInMapEquals(birthDataMap, "birthDate", "1999-07-12");
        EntityTestUtils.assertObjectInMapEquals(birthDataMap, "cityOfBirth", "Baltimore");
        EntityTestUtils.assertObjectInMapEquals(birthDataMap, "stateOfBirthAbbreviation", "MD");
        EntityTestUtils.assertObjectInMapEquals(birthDataMap, "countryOfBirthCode", "US");
        EntityTestUtils.assertObjectInMapEquals(birthDataMap, "dateEnteredUS", "2001-03-23");
        EntityTestUtils.assertObjectInMapEquals(birthDataMap, "multipleBirthStatus", false);

        List addressList = (List) studentNeutralRecord.getAttributes().get("address");
        Map addressMap = (Map) addressList.get(0);
        EntityTestUtils.assertObjectInMapEquals(addressMap, "addressType", "Home");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "streetNumberName", "555 Main Street");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "apartmentRoomSuiteNumber", "1A");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "buildingSiteNumber", "building site number");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "city", "Baltimore");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "stateAbbreviation", "MD");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "postalCode", "21218");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "nameOfCounty", "Baltimore");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "countyFIPSCode", "12345");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "countryCode", "US");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "latitude", "245");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "longitude", "432");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "openDate", "1969-01-01");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "closeDate", "2012-12-12");

        List telephoneList = (List) studentNeutralRecord.getAttributes().get("telephone");
        Map telephoneMap = (Map) telephoneList.get(0);
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "telephoneNumberType", "Mobile");
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "primaryTelephoneNumberIndicator", true);
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "telephoneNumber", "410-555-0248");

        List emailAddressList = (List) studentNeutralRecord.getAttributes().get("electronicMail");
        Map emailAddressMap = (Map) emailAddressList.get(0);
        EntityTestUtils.assertObjectInMapEquals(emailAddressMap, "emailAddressType", "Home/Personal");
        EntityTestUtils.assertObjectInMapEquals(emailAddressMap, "emailAddress", "asteele@email.com");

        assertEquals("slcedu.org/somePhoto.jpg", studentNeutralRecord.getAttributes().get("profileThumbnail"));
        assertEquals("Hispanic", studentNeutralRecord.getAttributes().get("oldEthnicity"));
        assertEquals(true, studentNeutralRecord.getAttributes().get("hispanicLatinoEthnicity"));

        List raceList = (List) studentNeutralRecord.getAttributes().get("race");
        assertEquals("White", raceList.get(0));
        if (raceList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            assertEquals("second racial category", raceList.get(1));
        }

        assertEquals(true, studentNeutralRecord.getAttributes().get("economicDisadvantaged"));
        assertEquals("Free", studentNeutralRecord.getAttributes().get("schoolFoodServicesEligibility"));

        List studentCharacteristicsList = (List) studentNeutralRecord.getAttributes().get("studentCharacteristics");
        Map studentCharacteristicsMap = (Map) studentCharacteristicsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(studentCharacteristicsMap, "characteristic", "Immigrant");
        EntityTestUtils.assertObjectInMapEquals(studentCharacteristicsMap, "beginDate", "2005-01-11");
        EntityTestUtils.assertObjectInMapEquals(studentCharacteristicsMap, "endDate", "2005-01-30");
        EntityTestUtils.assertObjectInMapEquals(studentCharacteristicsMap, "designatedBy", "School");

        assertEquals("NotLimited", studentNeutralRecord.getAttributes().get("limitedEnglishProficiency"));

        List languageList = (List) studentNeutralRecord.getAttributes().get("languages");
        assertEquals("English", languageList.get(0));
        if (languageList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            assertEquals("second language", languageList.get(1));
        }

        List homeLanguageList = (List) studentNeutralRecord.getAttributes().get("homeLanguages");
        assertEquals("English", homeLanguageList.get(0));
        if (homeLanguageList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            assertEquals("second home language", homeLanguageList.get(1));
        }

        List disablitiesList = (List) studentNeutralRecord.getAttributes().get("disabilities");
        Map disablityMap = (Map) disablitiesList.get(0);
        EntityTestUtils.assertObjectInMapEquals(disablityMap, "disability", "Deafness");
        EntityTestUtils.assertObjectInMapEquals(disablityMap, "disabilityDiagnosis", "Deaf");
        EntityTestUtils.assertObjectInMapEquals(disablityMap, "orderOfDisability", 3);

        List section504DisabilityList = (List) studentNeutralRecord.getAttributes().get("section504Disabilities");
        assertEquals("Other", section504DisabilityList.get(0));
        if (section504DisabilityList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            assertEquals("second disability", section504DisabilityList.get(1));
        }

        assertEquals("Other", studentNeutralRecord.getAttributes().get("displacementStatus"));

        List programParticipationsList = (List) studentNeutralRecord.getAttributes().get("programParticipations");
        Map programParticipationMap = (Map) programParticipationsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(programParticipationMap, "program", "Bilingual");
        EntityTestUtils.assertObjectInMapEquals(programParticipationMap, "beginDate", "2011-01-11");
        EntityTestUtils.assertObjectInMapEquals(programParticipationMap, "endDate", "2011-01-11");
        EntityTestUtils.assertObjectInMapEquals(programParticipationMap, "designatedBy", "School");

        Map learningStylesMap = (Map) studentNeutralRecord.getAttributes().get("learningStyles");
        EntityTestUtils.assertObjectInMapEquals(learningStylesMap, "visualLearning", 1);
        EntityTestUtils.assertObjectInMapEquals(learningStylesMap, "auditoryLearning", 1);
        EntityTestUtils.assertObjectInMapEquals(learningStylesMap, "tactileLearning", 1);

        List cohortYearsList = (List) studentNeutralRecord.getAttributes().get("cohortYears");
        Map cohortYearsMap = (Map) cohortYearsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(cohortYearsMap, "cohortYearType", "Ninth grade");
        EntityTestUtils.assertObjectInMapEquals(cohortYearsMap, "schoolYear", "2010-2011");

        List studentIndicatorsList = (List) studentNeutralRecord.getAttributes().get("studentIndicators");
        Map studentIndicatorMap = (Map) studentIndicatorsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(studentIndicatorMap, "indicatorGroup", "student");
        EntityTestUtils.assertObjectInMapEquals(studentIndicatorMap, "indicatorName", "student");
        EntityTestUtils.assertObjectInMapEquals(studentIndicatorMap, "indicator", "student");
        EntityTestUtils.assertObjectInMapEquals(studentIndicatorMap, "beginDate", "2011-01-11");
        EntityTestUtils.assertObjectInMapEquals(studentIndicatorMap, "endDate", "2011-01-12");
        EntityTestUtils.assertObjectInMapEquals(studentIndicatorMap, "designatedBy", "School");

        assertEquals("aStudent", studentNeutralRecord.getAttributes().get("loginId"));

    }

}
