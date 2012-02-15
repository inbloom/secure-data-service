package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import org.slc.sli.validation.EntityValidator;

/**
 * Test the smooks mappings for School entity
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SchoolEntityTest {

    @Autowired
    EntityValidator validator;

    @Test
    public void testValidSchool() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrganization/School";

        String testData = "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<School>"
                + "    <StateOrganizationId>152901001</StateOrganizationId>"
                + "    <EducationOrgIdentificationCode IdentificationSystem=\"identification system\">"
                + "        <Id>9777</Id>"
                + "    </EducationOrgIdentificationCode>"
                + "    <NameOfInstitution>Apple Alternative Elementary School</NameOfInstitution>"
                + "    <ShortNameOfInstitution>Apple</ShortNameOfInstitution>"
                + "    <OrganizationCategories>"
                + "        <OrganizationCategory>School</OrganizationCategory>"
                + "    </OrganizationCategories>"
                + "    <Address AddressType=\"Physical\">"
                + "        <StreetNumberName>123 Main Street</StreetNumberName>"
                + "        <ApartmentRoomSuiteNumber>1A</ApartmentRoomSuiteNumber>"
                + "        <BuildingSiteNumber>building site number</BuildingSiteNumber>"
                + "        <City>Lebanon</City>"
                + "        <StateAbbreviation>KS</StateAbbreviation>"
                + "        <PostalCode>66952</PostalCode>"
                + "        <NameOfCounty>Smith County</NameOfCounty>"
                + "        <CountyFIPSCode>USA123</CountyFIPSCode>"
                + "        <CountryCode>USA</CountryCode>"
                + "        <Latitude>245</Latitude>"
                + "        <Longitude>432</Longitude>"
                + "        <BeginDate>01-01-1969</BeginDate>"
                + "        <EndDate>12-12-2012</EndDate>"
                + "    </Address>"
                + "    <Telephone InstitutionTelephoneNumberType=\"Main\">"
                + "        <TelephoneNumber>(785) 667-6006</TelephoneNumber>"
                + "    </Telephone>"
                + "    <WebSite>www.a.com</WebSite>"
                + "    <OperationalStatus>running</OperationalStatus>"
                + "    <AccountabilityRatings>"
                + "        <RatingTitle>first rating</RatingTitle>"
                + "        <Rating>A</Rating>"
                + "        <RatingDate>01-01-2012</RatingDate>"
                + "        <RatingOrganization>rating org</RatingOrganization>"
                + "        <RatingProgram>rating program</RatingProgram>"
                + "    </AccountabilityRatings>"
                + "    <ProgramReference>program reference</ProgramReference>"
                + "    <GradesOffered>"
                + "        <GradeLevel>Third grade</GradeLevel>"
                + "        <GradeLevel>Fourth grade</GradeLevel>"
                + "    </GradesOffered>"
                + "    <SchoolCategories>"
                + "        <SchoolCategory>Elementary School</SchoolCategory>"
                + "    </SchoolCategories>"
                + "    <SchoolType>school type</SchoolType>"
                + "    <CharterStatus>charter status</CharterStatus>"
                + "    <TitleIPartASchoolDesignation>title i</TitleIPartASchoolDesignation>"
                + "    <MagnetSpecialProgramEmphasisSchool>magnet</MagnetSpecialProgramEmphasisSchool>"
                + "    <AdministrativeFundingControl>admin funding</AdministrativeFundingControl>"
                + "</School>"
                + "</InterchangeEducationOrganization>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, testData);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("school");

        Assert.assertTrue(validator.validate(e));
    }

    @Test
    public void csvSchoolTest() throws Exception {

        String smooksConfig = "smooks_conf/smooks-school-csv.xml";

        String targetSelector = "csv-record";

        String schoolCsv = "152901001,identification system,9777,Apple Alternative Elementary School,Apple,School,Physical,123 Main Street,1A,"
                + "building site number,Lebanon,KS,66952,Smith County,USA123,USA,245,432,01-01-1969,12-12-2012,Main,(785) 667-6006,www.a.com,running,"
                + "first rating,A,01-01-2012,rating org,rating program,program reference,Third grade,Elementary School,school type,charter status,title i,magnet,admin funding";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                schoolCsv);

        checkValidSchoolNeutralRecord(neutralRecord);
    }

    @Test
    public void edfiXmlSchoolTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeEducationOrganization/School";

        String edfiSchoolXml = "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<School>"
                + "    <StateOrganizationId>152901001</StateOrganizationId>"
                + "    <EducationOrgIdentificationCode IdentificationSystem=\"identification system\">"
                + "        <Id>9777</Id>"
                + "    </EducationOrgIdentificationCode>"
                + "    <NameOfInstitution>Apple Alternative Elementary School</NameOfInstitution>"
                + "    <ShortNameOfInstitution>Apple</ShortNameOfInstitution>"
                + "    <OrganizationCategories>"
                + "        <OrganizationCategory>School</OrganizationCategory>"
                + "    </OrganizationCategories>"
                + "    <Address AddressType=\"Physical\">"
                + "        <StreetNumberName>123 Main Street</StreetNumberName>"
                + "        <ApartmentRoomSuiteNumber>1A</ApartmentRoomSuiteNumber>"
                + "        <BuildingSiteNumber>building site number</BuildingSiteNumber>"
                + "        <City>Lebanon</City>"
                + "        <StateAbbreviation>KS</StateAbbreviation>"
                + "        <PostalCode>66952</PostalCode>"
                + "        <NameOfCounty>Smith County</NameOfCounty>"
                + "        <CountyFIPSCode>USA123</CountyFIPSCode>"
                + "        <CountryCode>USA</CountryCode>"
                + "        <Latitude>245</Latitude>"
                + "        <Longitude>432</Longitude>"
                + "        <BeginDate>01-01-1969</BeginDate>"
                + "        <EndDate>12-12-2012</EndDate>"
                + "    </Address>"
                + "    <Telephone InstitutionTelephoneNumberType=\"Main\">"
                + "        <TelephoneNumber>(785) 667-6006</TelephoneNumber>"
                + "    </Telephone>"
                + "    <WebSite>www.a.com</WebSite>"
                + "    <OperationalStatus>running</OperationalStatus>"
                + "    <AccountabilityRatings>"
                + "        <RatingTitle>first rating</RatingTitle>"
                + "        <Rating>A</Rating>"
                + "        <RatingDate>01-01-2012</RatingDate>"
                + "        <RatingOrganization>rating org</RatingOrganization>"
                + "        <RatingProgram>rating program</RatingProgram>"
                + "    </AccountabilityRatings>"
                + "    <ProgramReference>program reference</ProgramReference>"
                + "    <GradesOffered>"
                + "        <GradeLevel>Third grade</GradeLevel>"
                + "        <GradeLevel>Fourth grade</GradeLevel>"
                + "    </GradesOffered>"
                + "    <SchoolCategories>"
                + "        <SchoolCategory>Elementary School</SchoolCategory>"
                + "    </SchoolCategories>"
                + "    <SchoolType>school type</SchoolType>"
                + "    <CharterStatus>charter status</CharterStatus>"
                + "    <TitleIPartASchoolDesignation>title i</TitleIPartASchoolDesignation>"
                + "    <MagnetSpecialProgramEmphasisSchool>magnet</MagnetSpecialProgramEmphasisSchool>"
                + "    <AdministrativeFundingControl>admin funding</AdministrativeFundingControl>"
                + "</School>"
                + "</InterchangeEducationOrganization>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiSchoolXml);

        checkValidSchoolNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidSchoolNeutralRecord(NeutralRecord neutralRecord) {

        assertEquals("152901001", neutralRecord.getLocalId());
        assertEquals("152901001", neutralRecord.getAttributes().get("stateOrganizationId"));

        List educationOrgIdentificationCodeList = (List) neutralRecord.getAttributes().get(
                "educationOrgIdentificationCode");
        Map educationOrgIdentificationMap = (Map) educationOrgIdentificationCodeList.get(0);
        EntityTestUtils.assertObjectInMapEquals(educationOrgIdentificationMap, "identificationSystem",
                "identification system");
        EntityTestUtils.assertObjectInMapEquals(educationOrgIdentificationMap, "ID", "9777");

        assertEquals("Apple Alternative Elementary School", neutralRecord.getAttributes().get("nameOfInstitution"));
        assertEquals("Apple", neutralRecord.getAttributes().get("shortNameOfInstitution"));

        List organizationCategoriesList = (List) neutralRecord.getAttributes().get("organizationCategories");
        assertEquals("School", organizationCategoriesList.get(0));

        List addressList = (List) neutralRecord.getAttributes().get("address");
        Map addressMap = (Map) addressList.get(0);
        EntityTestUtils.assertObjectInMapEquals(addressMap, "addressType", "Physical");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "streetNumberName", "123 Main Street");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "apartmentRoomSuiteNumber", "1A");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "buildingSiteNumber", "building site number");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "city", "Lebanon");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "stateAbbreviation", "KS");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "postalCode", "66952");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "nameOfCounty", "Smith County");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "countyFIPSCode", "USA123");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "countryCode", "USA");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "latitude", "245");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "longitude", "432");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "openDate", "01-01-1969");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "closeDate", "12-12-2012");

        List telephoneList = (List) neutralRecord.getAttributes().get("telephone");
        Map telephoneMap = (Map) telephoneList.get(0);
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "institutionTelephoneNumberType", "Main");
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "telephoneNumber", "(785) 667-6006");

        assertEquals("www.a.com", neutralRecord.getAttributes().get("webSite"));
        assertEquals("running", neutralRecord.getAttributes().get("operationalStatus"));

        List accountabilityRatingsList = (List) neutralRecord.getAttributes().get("accountabilityRatings");
        Map accountabilityRatingsMap = (Map) accountabilityRatingsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(accountabilityRatingsMap, "ratingTitle", "first rating");
        EntityTestUtils.assertObjectInMapEquals(accountabilityRatingsMap, "rating", "A");
        EntityTestUtils.assertObjectInMapEquals(accountabilityRatingsMap, "ratingDate", "01-01-2012");
        EntityTestUtils.assertObjectInMapEquals(accountabilityRatingsMap, "ratingOrganization", "rating org");
        EntityTestUtils.assertObjectInMapEquals(accountabilityRatingsMap, "ratingProgram", "rating program");

        List programReferenceList = (List) neutralRecord.getAttributes().get("programReference");
        assertEquals("program reference", programReferenceList.get(0));

        List gradesOfferedList = (List) neutralRecord.getAttributes().get("gradesOffered");
        assertEquals("Third grade", gradesOfferedList.get(0));
        if (gradesOfferedList.size() > 1) {
            // TODO: remove if block when we support csv collections
            assertEquals("Fourth grade", gradesOfferedList.get(1));
        }

        List schoolCategoriesList = (List) neutralRecord.getAttributes().get("schoolCategories");
        assertEquals("Elementary School", schoolCategoriesList.get(0));

        assertEquals("school type", neutralRecord.getAttributes().get("schoolType"));
        assertEquals("charter status", neutralRecord.getAttributes().get("charterStatus"));
        assertEquals("title i", neutralRecord.getAttributes().get("titleIPartASchoolDesignation"));
        assertEquals("magnet", neutralRecord.getAttributes().get("magnetSpecialProgramEmphasisSchool"));
        assertEquals("admin funding", neutralRecord.getAttributes().get("administrativeFundingControl"));

    }

}
