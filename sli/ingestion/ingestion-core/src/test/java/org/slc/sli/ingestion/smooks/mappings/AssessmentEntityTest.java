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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
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
 * Test the smooks mappings for Assessment entity
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/recordLvlHash-context.xml" })
public class AssessmentEntityTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLvlHashNeutralRecordTypes;

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;
    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void edfiXmlAssessmentTest() throws Exception {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeAssessmentMetadata/Assessment";

        String edfiAssessmentXml = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
                + "<Assessment>"
                + "  <AssessmentTitle>TAKS</AssessmentTitle>"
                + "  <AssessmentIdentificationCode IdentificationSystem=\"Test Contractor \" AssigningOrganizationCode=\"AssigningOrg\">"
                + "    <ID>TAKS 3rd Grade Reading</ID>"
                + "  </AssessmentIdentificationCode>"
                + "  <AssessmentCategory>State summative assessment 3-8 general</AssessmentCategory>"
                + "  <AcademicSubject>Reading</AcademicSubject>"
                + "  <GradeLevelAssessed>Third grade</GradeLevelAssessed>"
                + "  <LowestGradeLevelAssessed>Fourth grade</LowestGradeLevelAssessed>"
                + "  <AssessmentPerformanceLevel>"
                + "    <PerformanceLevel>"
                + "      <CodeValue>the code value</CodeValue>"
                + "      <Description>TAKSMetStandard</Description>"
                + "    </PerformanceLevel>"
                + "    <AssessmentReportingMethod>Scale score</AssessmentReportingMethod>"
                + "    <MinimumScore>2100</MinimumScore>"
                + "    <MaximumScore>3500</MaximumScore>"
                + "  </AssessmentPerformanceLevel>"
                + "  <AssessmentPerformanceLevel>"
                + "    <PerformanceLevel>"
                + "      <CodeValue>the code value2</CodeValue>"
                + "      <Description>TAKSCommendedPerformance</Description>"
                + "    </PerformanceLevel>"
                + "    <AssessmentReportingMethod>Scale score2</AssessmentReportingMethod>"
                + "    <MaximumScore>3600</MaximumScore>"
                + "  </AssessmentPerformanceLevel>"
                + "  <ContentStandard>State Standard</ContentStandard>"
                + "  <AssessmentForm>Assessment Form</AssessmentForm>"
                + "  <Version>2002</Version>"
                + "  <RevisionDate>2002-09-01</RevisionDate>"
                + "  <MaxRawScore>36</MaxRawScore>"
                + "  <Nomenclature>the nomenclature</Nomenclature>"
                + "  <AssessmentPeriod>"
                + "    <AssessmentPeriodDescriptorIdentity>"
                + "      <AssessmentPeriodDescriptorCodeValue>code value</AssessmentPeriodDescriptorCodeValue>"
                + "    </AssessmentPeriodDescriptorIdentity>"
                + "  </AssessmentPeriod>"
                + "  <AssessmentItemReference>"
                + "    <AssessmentItemIdentity>"
                + "      <AssessmentItemIdentificationCode>TAKSReading3-1</AssessmentItemIdentificationCode>"
                + "    </AssessmentItemIdentity>"
                + "  </AssessmentItemReference>"
                + "  <AssessmentItemReference>"
                + "    <AssessmentItemIdentity>"
                + "      <AssessmentItemIdentificationCode>TAKSReading3-2</AssessmentItemIdentificationCode>"
                + "    </AssessmentItemIdentity>"
                + "  </AssessmentItemReference>"
                + "  <ObjectiveAssessmentReference>"
                + "    <ObjectiveAssessmentIdentity>"
                + "       <ObjectiveAssessmentIdentificationCode>TAKSReading2-1</ObjectiveAssessmentIdentificationCode>"
                + "     </ObjectiveAssessmentIdentity>"
                + "  </ObjectiveAssessmentReference>"
                + "  <ObjectiveAssessmentReference>"
                + "    <ObjectiveAssessmentIdentity>"
                + "       <ObjectiveAssessmentIdentificationCode>TAKSReading2-2</ObjectiveAssessmentIdentificationCode>"
                + "     </ObjectiveAssessmentIdentity>"
                + "  </ObjectiveAssessmentReference>"
                + "  <AssessmentFamilyReference>"
                + "    <AssessmentFamilyIdentity>"
                + "      <AssessmentFamilyIdentificationCode IdentificationSystem=\"idsys\" AssigningOrganizationCode=\"orgcode\">"
                + "        <ID>1234</ID>"
                + "      </AssessmentFamilyIdentificationCode>"
                + "      <AssessmentFamilyIdentificationCode IdentificationSystem=\"idsys2\" AssigningOrganizationCode=\"orgcode2\">"
                + "        <ID>1235</ID>"
                + "      </AssessmentFamilyIdentificationCode>"
                + "      <AssessmentFamilyTitle>family title</AssessmentFamilyTitle>"
                + "      <Version>first</Version>"
                + "    </AssessmentFamilyIdentity>"
                + "  </AssessmentFamilyReference>"
                + "  <SectionReference>"
                + "    <SectionIdentity>"
                + "      <StateOrganizationId>6789</StateOrganizationId>"
                + "      <EducationOrgIdentificationCode IdentificationSystem=\"edorgsystem\">"
                + "        <ID>edorgid</ID>"
                + "      </EducationOrgIdentificationCode>"
                + "      <EducationOrgIdentificationCode IdentificationSystem=\"edorgsystem2\">"
                + "        <ID>edorgid2</ID>"
                + "      </EducationOrgIdentificationCode>"
                + "      <UniqueSectionCode>uniquesectioncode</UniqueSectionCode>"
                + "      <CourseCode IdentificationSystem=\"courseIdentificationSystem\" AssigningOrganizationCode=\"assigncode\">"
                + "        <ID>courseId</ID>"
                + "      </CourseCode>"
                + "      <LocalCourseCode>localcoursecode</LocalCourseCode>"
                + "      <SchoolYear>2012</SchoolYear>"
                + "      <Term>myterm</Term>"
                + "      <ClassPeriodName>classperiodname</ClassPeriodName>"
                + "      <Location>here</Location>"
                + "    </SectionIdentity>"
                + "  </SectionReference>"
                + "</Assessment></InterchangeAssessmentMetadata>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiAssessmentXml, recordLvlHashNeutralRecordTypes, mockDIdStrategy, mockDIdResolver);

        checkValidAssessmentNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidAssessmentNeutralRecord(NeutralRecord assessmentNeutralRecord) throws Exception {

        assertEquals("assessment", assessmentNeutralRecord.getRecordType());
        assertEquals("TAKS 3rd Grade Reading", assessmentNeutralRecord.getLocalId());
        assertEquals("TAKS", assessmentNeutralRecord.getAttributes().get("assessmentTitle"));

        List assessmentIdentificationCodeList = (List) assessmentNeutralRecord.getAttributes().get(
                "assessmentIdentificationCode");
        Map assessmentIdentificationCodeMap = (Map) assessmentIdentificationCodeList.get(0);
        EntityTestUtils.assertObjectInMapEquals(assessmentIdentificationCodeMap, "identificationSystem",
                "Test Contractor");
        EntityTestUtils.assertObjectInMapEquals(assessmentIdentificationCodeMap, "assigningOrganizationCode",
                "AssigningOrg");
        EntityTestUtils.assertObjectInMapEquals(assessmentIdentificationCodeMap, "ID", "TAKS 3rd Grade Reading");

        assertEquals("State summative assessment 3-8 general",
                assessmentNeutralRecord.getAttributes().get("assessmentCategory"));
        assertEquals("Reading", assessmentNeutralRecord.getAttributes().get("academicSubject"));
        assertEquals("Third grade", assessmentNeutralRecord.getAttributes().get("gradeLevelAssessed"));
        assertEquals("Fourth grade", assessmentNeutralRecord.getAttributes().get("lowestGradeLevelAssessed"));

        List assessmentPerformanceLevelList = (List) assessmentNeutralRecord.getAttributes().get(
                "assessmentPerformanceLevel");

        Map assessmentPerformanceLevelMap = (Map) assessmentPerformanceLevelList.get(0);
        List performanceLevel = (List) assessmentPerformanceLevelMap.get("performanceLevelDescriptor");
        EntityTestUtils.assertObjectInMapEquals((Map) performanceLevel.get(0), "codeValue", "the code value");
        EntityTestUtils.assertObjectInMapEquals((Map) performanceLevel.get(1), "description", "TAKSMetStandard");

        EntityTestUtils.assertObjectInMapEquals(assessmentPerformanceLevelMap, "assessmentReportingMethod",
                "Scale score");
        EntityTestUtils.assertObjectInMapEquals(assessmentPerformanceLevelMap, "minimumScore", 2100);
        EntityTestUtils.assertObjectInMapEquals(assessmentPerformanceLevelMap, "maximumScore", 3500);
        if (assessmentPerformanceLevelList.size() > 1) {
            // TODO: remove this if block when we support csv lists
            Map assessmentPerformanceLevelMap2 = (Map) assessmentPerformanceLevelList.get(1);
            List performanceLevel2 = (List) assessmentPerformanceLevelMap2.get("performanceLevelDescriptor");
            EntityTestUtils.assertObjectInMapEquals((Map) performanceLevel2.get(0), "codeValue", "the code value2");
            EntityTestUtils.assertObjectInMapEquals((Map) performanceLevel2.get(1), "description", "TAKSCommendedPerformance");

            EntityTestUtils.assertObjectInMapEquals(assessmentPerformanceLevelMap2, "assessmentReportingMethod",
                    "Scale score2");
            EntityTestUtils.assertObjectInMapEquals(assessmentPerformanceLevelMap2, "minimumScore", null);
            EntityTestUtils.assertObjectInMapEquals(assessmentPerformanceLevelMap2, "maximumScore", 3600);
        }

        assertEquals("State Standard", assessmentNeutralRecord.getAttributes().get("contentStandard"));
        assertEquals("Assessment Form", assessmentNeutralRecord.getAttributes().get("assessmentForm"));
        assertEquals(2002, assessmentNeutralRecord.getAttributes().get("version"));
        assertEquals("2002-09-01", assessmentNeutralRecord.getAttributes().get("revisionDate"));
        assertEquals(36, assessmentNeutralRecord.getAttributes().get("maxRawScore"));
        assertEquals("the nomenclature", assessmentNeutralRecord.getAttributes().get("nomenclature"));

        assertEquals("code value", PropertyUtils.getProperty(assessmentNeutralRecord.getAttributes(), "assessmentPeriodDescriptorId.AssessmentPeriodDescriptorIdentity.AssessmentPeriodDescriptorCodeValue"));
    }
}
