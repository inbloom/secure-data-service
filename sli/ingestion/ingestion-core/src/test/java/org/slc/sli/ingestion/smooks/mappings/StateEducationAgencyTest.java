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

import org.junit.Assert;
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
 * Test the smooks mappings for StateEducationAgency entity
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StateEducationAgencyTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLevelDeltaEnabledEntityNames;

    @Autowired
    private EntityValidator validator;

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;
    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Ignore
    @Test
    public void testValidStateEducationAgency() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrganization/StateEducationAgency";

        String testData = "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<StateEducationAgency>"
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
                + "</StateEducationAgency>"
                + "</InterchangeEducationOrganization>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                testData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("stateEducationAgency");

        Assert.assertTrue(validator.validate(e));
    }

    @Test
    public void edfiXmlStateEducationAgencyTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeEducationOrganization/StateEducationAgency";

        String edfiXml = "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<StateEducationAgency>"
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
                + "</StateEducationAgency>"
                + "</InterchangeEducationOrganization>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiXml, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidSEANeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidSEANeutralRecord(NeutralRecord neutralRecord) {

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

/*        @SuppressWarnings("unchecked")
        List<Map<String, Object>> programReferenceList = (List<Map<String, Object>>) neutralRecord.getAttributes().get("programReference");
        assertEquals("ACC-TEST-PROG-1", programReferenceList.get(0).get("programId"));
        assertEquals("ACC-TEST-PROG-2", programReferenceList.get(1).get("programId"));
*/    }
}
