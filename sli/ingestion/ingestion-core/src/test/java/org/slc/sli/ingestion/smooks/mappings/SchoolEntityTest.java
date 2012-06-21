package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import org.slc.sli.validation.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

/**
 * Test the smooks mappings for School entity
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SchoolEntityTest {

    @InjectMocks
    @Autowired
    private EntityValidator validator;

    @Mock
    private Repository<Entity> mockRepository;

    String edfiSchoolXml = "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<School>"
            + "    <StateOrganizationId>152901001</StateOrganizationId>"
            + "    <EducationOrgIdentificationCode IdentificationSystem=\"School\">"
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
            + "        <CountyFIPSCode>US123</CountyFIPSCode>"
            + "        <CountryCode>US</CountryCode>"
            + "        <Latitude>245</Latitude>"
            + "        <Longitude>432</Longitude>"
            + "        <BeginDate>1969-01-01</BeginDate>"
            + "        <EndDate>2012-12-12</EndDate>"
            + "    </Address>"
            + "    <Telephone InstitutionTelephoneNumberType=\"Main\">"
            + "        <TelephoneNumber>(785) 667-6006</TelephoneNumber>"
            + "    </Telephone>"
            + "    <WebSite>www.a.com</WebSite>"
            + "    <OperationalStatus>Active</OperationalStatus>"
            + "    <AccountabilityRatings>"
            + "        <RatingTitle>first rating</RatingTitle>"
            + "        <Rating>A</Rating>"
            + "        <RatingDate>2012-01-01</RatingDate>"
            + "        <RatingOrganization>rating org</RatingOrganization>"
            + "        <RatingProgram>rating program</RatingProgram>"
            + "    </AccountabilityRatings>"
            + "    <GradesOffered>"
            + "        <GradeLevel>Third grade</GradeLevel>"
            + "        <GradeLevel>Fourth grade</GradeLevel>"
            + "    </GradesOffered>"
            + "    <SchoolCategories>"
            + "        <SchoolCategory>Elementary School</SchoolCategory>"
            + "    </SchoolCategories>"
            + "    <SchoolType>Alternative</SchoolType>"
            + "    <CharterStatus>School Charter</CharterStatus>"
            + "    <TitleIPartASchoolDesignation>Not designated as a Title I Part A school</TitleIPartASchoolDesignation>"
            + "    <MagnetSpecialProgramEmphasisSchool>All students participate</MagnetSpecialProgramEmphasisSchool>"
            + "    <AdministrativeFundingControl>Public School</AdministrativeFundingControl>"
            + "    <LocalEducationAgencyReference id=\"ID053\" ref=\"ID044\"> "
            + "      <EducationalOrgIdentity>"
            + "        <StateOrganizationId>LEA123</StateOrganizationId>"
            + "      </EducationalOrgIdentity>"
            + "    </LocalEducationAgencyReference>"
            + "    <ProgramReference>"
            + "      <ProgramIdentity>"
            + "        <ProgramId>ACC-TEST-PROG-1</ProgramId>"
            + "      </ProgramIdentity>"
            + "    </ProgramReference>"
            + "    <ProgramReference>"
            + "      <ProgramIdentity>"
            + "        <ProgramId>ACC-TEST-PROG-2</ProgramId>"
            + "      </ProgramIdentity>"
            + "    </ProgramReference>"
            + "  </School>"
            + "</InterchangeEducationOrganization>";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Ignore
    @Test
    public void testValidSchool() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrganization/School";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                edfiSchoolXml);

        // mock repository will simulate "finding" the referenced educationOrganization
        Mockito.when(mockRepository.exists("educationOrganization", "LEA123")).thenReturn(true);
        Map<String, Object> attributes = neutralRecord.getAttributes();
        attributes.remove("collectionName");
        EntityTestUtils.mapValidation(attributes, "school", validator);
    }

    @Test
    public void edfiXmlSchoolTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeEducationOrganization/School";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiSchoolXml);

        checkValidSchoolNeutralRecord(neutralRecord, true);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidSchoolNeutralRecord(NeutralRecord neutralRecord, boolean isXML) {

        assertEquals("152901001", neutralRecord.getLocalId());
        assertEquals("152901001", neutralRecord.getAttributes().get("stateOrganizationId"));

        List educationOrgIdentificationCodeList = (List) neutralRecord.getAttributes().get(
                "educationOrgIdentificationCode");
        Map educationOrgIdentificationMap = (Map) educationOrgIdentificationCodeList.get(0);
        EntityTestUtils.assertObjectInMapEquals(educationOrgIdentificationMap, "identificationSystem", "School");
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
        EntityTestUtils.assertObjectInMapEquals(addressMap, "countyFIPSCode", "US123");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "countryCode", "US");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "latitude", "245");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "longitude", "432");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "openDate", "1969-01-01");
        EntityTestUtils.assertObjectInMapEquals(addressMap, "closeDate", "2012-12-12");

        List telephoneList = (List) neutralRecord.getAttributes().get("telephone");
        Map telephoneMap = (Map) telephoneList.get(0);
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "institutionTelephoneNumberType", "Main");
        EntityTestUtils.assertObjectInMapEquals(telephoneMap, "telephoneNumber", "(785) 667-6006");

        assertEquals("www.a.com", neutralRecord.getAttributes().get("webSite"));
        assertEquals("Active", neutralRecord.getAttributes().get("operationalStatus"));

        List accountabilityRatingsList = (List) neutralRecord.getAttributes().get("accountabilityRatings");
        Map accountabilityRatingsMap = (Map) accountabilityRatingsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(accountabilityRatingsMap, "ratingTitle", "first rating");
        EntityTestUtils.assertObjectInMapEquals(accountabilityRatingsMap, "rating", "A");
        EntityTestUtils.assertObjectInMapEquals(accountabilityRatingsMap, "ratingDate", "2012-01-01");
        EntityTestUtils.assertObjectInMapEquals(accountabilityRatingsMap, "ratingOrganization", "rating org");
        EntityTestUtils.assertObjectInMapEquals(accountabilityRatingsMap, "ratingProgram", "rating program");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> programReferenceList = (List<Map<String, Object>>) neutralRecord.getAttributes().get("programReference");
        assertEquals("ACC-TEST-PROG-1", programReferenceList.get(0).get("programId"));
        assertEquals("ACC-TEST-PROG-2", programReferenceList.get(1).get("programId"));

        List gradesOfferedList = (List) neutralRecord.getAttributes().get("gradesOffered");
        assertEquals("Third grade", gradesOfferedList.get(0));
        if (isXML) {
            // TODO: remove if block when we support csv collections
            assertEquals("Fourth grade", gradesOfferedList.get(1));
        }

        List schoolCategoriesList = (List) neutralRecord.getAttributes().get("schoolCategories");
        assertEquals("Elementary School", schoolCategoriesList.get(0));

        assertEquals("Alternative", neutralRecord.getAttributes().get("schoolType"));
        assertEquals("School Charter", neutralRecord.getAttributes().get("charterStatus"));
        assertEquals("Not designated as a Title I Part A school",
                neutralRecord.getAttributes().get("titleIPartASchoolDesignation"));
        assertEquals("All students participate", neutralRecord.getAttributes()
                .get("magnetSpecialProgramEmphasisSchool"));
        assertEquals("Public School", neutralRecord.getAttributes().get("administrativeFundingControl"));

        assertEquals("LEA123", neutralRecord.getAttributes().get("parentEducationAgencyReference"));

    }

}
