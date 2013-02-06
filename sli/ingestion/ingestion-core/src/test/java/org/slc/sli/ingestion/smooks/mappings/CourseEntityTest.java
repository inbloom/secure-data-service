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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Test the smooks mappings for Course entity
 *
 * @author jtully
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/recordLvlHash-context.xml" })
public class CourseEntityTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLevelDeltaEnabledEntityNames;

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;
    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void edfiXmlCourseTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeEducationOrganization/Course";

        String edfiCourseXml = "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Course>"
                + "    <CourseTitle>Science7</CourseTitle>"
                + "    <NumberOfParts>7</NumberOfParts>"
                + "    <CourseCode IdentificationSystem=\"LEA course code\""
                + "        AssigningOrganizationCode=\"orgCode\">"
                + "        <ID>science7</ID>"
                + "    </CourseCode>"
                + "    <CourseCode IdentificationSystem=\"LEA course code\""
                + "        AssigningOrganizationCode=\"orgCode2\">"
                + "        <ID>science72</ID>"
                + "    </CourseCode>"
                + "    <CourseLevel>Honors</CourseLevel>"
                + "    <CourseLevelCharacteristics>"
                + "        <CourseLevelCharacteristic>Advanced</CourseLevelCharacteristic>"
                + "    </CourseLevelCharacteristics>"
                + "    <GradesOffered>"
                + "        <GradeLevel>Seventh grade</GradeLevel>"
                + "        <GradeLevel>Eighth grade</GradeLevel>"
                + "    </GradesOffered>"
                + "    <SubjectArea>Science</SubjectArea>"
                + "    <CourseDescription>A seventh grade science course</CourseDescription>"
                + "    <DateCourseAdopted>2012-02-01</DateCourseAdopted>"
                + "    <HighSchoolCourseRequirement>true</HighSchoolCourseRequirement>"
                + "    <CourseGPAApplicability>Applicable</CourseGPAApplicability>"
                + "    <CourseDefinedBy>LEA</CourseDefinedBy>"
                + "    <MinimumAvailableCredit CreditType=\"Carnegie unit\""
                + "        CreditConversion=\"1.0\">"
                + "        <Credit>1.0</Credit>"
                + "    </MinimumAvailableCredit>"
                + "    <MaximumAvailableCredit CreditType=\"Carnegie unit\""
                + "        CreditConversion=\"1.0\">"
                + "        <Credit>2.0</Credit>"
                + "    </MaximumAvailableCredit>"
                + "    <CareerPathway>Science Technology Engineering and Mathematics</CareerPathway>"
                + "    <EducationOrganizationReference>"
                + "        <EducationalOrgIdentity>"
                + "            <StateOrganizationId>ID1</StateOrganizationId>"
                + "        </EducationalOrgIdentity>"
                + "    </EducationOrganizationReference>"
                + "    <UniqueCourseId>000001</UniqueCourseId>"
                + "</Course>"
                + "</InterchangeEducationOrganization>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiCourseXml, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidCourseNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidCourseNeutralRecord(NeutralRecord neutralRecord) {

        assertEquals("course", neutralRecord.getRecordType());

        assertEquals("Science7", neutralRecord.getAttributes().get("courseTitle"));

        List courseCodeList = (List) neutralRecord.getAttributes().get("courseCode");
        Map courseCodeMap = (Map) courseCodeList.get(0);
        EntityTestUtils.assertObjectInMapEquals(courseCodeMap, "identificationSystem", "LEA course code");
        EntityTestUtils.assertObjectInMapEquals(courseCodeMap, "assigningOrganizationCode", "orgCode");
        EntityTestUtils.assertObjectInMapEquals(courseCodeMap, "ID", "science7");
        if (courseCodeList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            Map courseCodeMap2 = (Map) courseCodeList.get(1);
            EntityTestUtils.assertObjectInMapEquals(courseCodeMap2, "identificationSystem", "LEA course code");
            EntityTestUtils.assertObjectInMapEquals(courseCodeMap2, "assigningOrganizationCode", "orgCode2");
            EntityTestUtils.assertObjectInMapEquals(courseCodeMap2, "ID", "science72");
        }

        assertEquals("Honors", neutralRecord.getAttributes().get("courseLevel"));

        List courseLevelCharacteristicList = (List) neutralRecord.getAttributes().get("courseLevelCharacteristics");
        assertEquals(1, courseLevelCharacteristicList.size());
        assertEquals("Advanced", courseLevelCharacteristicList.get(0));

        //check that all grades offered are reflected in entity
        List gradesOfferedList = (List) neutralRecord.getAttributes().get("gradesOffered");
        assertEquals(gradesOfferedList.size(), 2);
        assertEquals("Seventh grade", gradesOfferedList.get(0));
        assertEquals("Eighth grade", gradesOfferedList.get(1));


        assertEquals("Science", neutralRecord.getAttributes().get("subjectArea"));

        assertEquals("A seventh grade science course", neutralRecord.getAttributes().get("courseDescription"));

        assertEquals("2012-02-01", neutralRecord.getAttributes().get("dateCourseAdopted"));

        assertEquals(true, neutralRecord.getAttributes().get("highSchoolCourseRequirement"));

        assertEquals("Applicable", neutralRecord.getAttributes().get("courseGPAApplicability"));

        assertEquals("LEA", neutralRecord.getAttributes().get("courseDefinedBy"));

        Map minCreditMap = (Map) neutralRecord.getAttributes().get("minimumAvailableCredit");
        EntityTestUtils.assertObjectInMapEquals(minCreditMap, "creditType", "Carnegie unit");
        EntityTestUtils.assertObjectInMapEquals(minCreditMap, "creditConversion", 1.0);
        EntityTestUtils.assertObjectInMapEquals(minCreditMap, "credit", 1.0);

        Map maxCreditMap = (Map) neutralRecord.getAttributes().get("maximumAvailableCredit");
        EntityTestUtils.assertObjectInMapEquals(maxCreditMap, "creditType", "Carnegie unit");
        EntityTestUtils.assertObjectInMapEquals(maxCreditMap, "creditConversion", 1.0);
        EntityTestUtils.assertObjectInMapEquals(maxCreditMap, "credit", 2.0);

        assertEquals("Science Technology Engineering and Mathematics",
                neutralRecord.getAttributes().get("careerPathway"));

        Map<String, Object> EducationOrganizationReference = (Map<String, Object>)neutralRecord.getAttributes().get("EducationOrganizationReference");
        assertNotNull("Could not find EducationOrganizationReference in Course", EducationOrganizationReference);
        Map<String, Object> EducationalOrgIdentity  = (Map<String, Object>)EducationOrganizationReference.get("EducationalOrgIdentity");
        assertNotNull("Could not find EducationOrganizationReference.EducationalOrgIdentity in Course", EducationOrganizationReference);
        assertEquals("EducationOrganizationReference.EducationalOrgIdentity.StateOrganizationId is not 'ID1' in Course", "ID1", EducationalOrgIdentity.get("StateOrganizationId"));
    }

}
