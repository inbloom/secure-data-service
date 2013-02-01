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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Tests for smooks mappings of AssessmentFamily
 *
 * @author jtully
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AssessmentFamilyTest {

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
    public void edfiXmlAssessmentFamilyTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeAssessmentMetadata/AssessmentFamily";

        String edfiAssessmentFamilyXml = "<InterchangeAssessmentMetadata xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<AssessmentFamily>"
                + "<AssessmentFamilyTitle>familyTitle</AssessmentFamilyTitle>"
                + "<AssessmentFamilyIdentificationCode IdentificationSystem=\"firstIdentificationSystem\" AssigningOrganizationCode=\"firstAssigningOrganizationCode\" >"
                + "  <ID>firstId</ID>"
                + "</AssessmentFamilyIdentificationCode>"
                + "<AssessmentCategory>State summative assessment 3-8 general</AssessmentCategory>"
                + "<AcademicSubject>Reading</AcademicSubject>"
                + "<GradeLevelAssessed>Third grade</GradeLevelAssessed>"
                + "<LowestGradeLevelAssessed>Fourth grade</LowestGradeLevelAssessed>"
                + "<ContentStandard>State Standard</ContentStandard>"
                + "<Version>2002</Version>"
                + "<RevisionDate>2002-09-01</RevisionDate>"
                + "<Nomenclature>the nomenclature</Nomenclature>"
                + "<AssessmentPeriods>"
                + "  <CodeValue>code value</CodeValue>"
                + "  <ShortDescription>short desc</ShortDescription>"
                + "  <Description>descript</Description>"
                + "</AssessmentPeriods>"
                + "<AssessmentPeriods>"
                + "  <CodeValue>code value2</CodeValue>"
                + "  <ShortDescription>short desc2</ShortDescription>"
                + "  <Description>descript2</Description>"
                + "</AssessmentPeriods>"
                + "<AssessmentFamilyIdentificationCode IdentificationSystem=\"secondIdentificationSystem\" AssigningOrganizationCode=\"secondAssigningOrganizationCode\" >"
                + "  <ID>secondId</ID>"
                + "</AssessmentFamilyIdentificationCode>"
                + "<AssessmentFamilyReference>"
                + "  <AssessmentFamilyIdentity>"
                + "    <AssessmentFamilyIdentificationCode IdentificationSystem=\"firstRefIdentificationSystem\" AssigningOrganizationCode=\"firstRefAssigningOrganizationCode\" >"
                + "      <ID>firstRefId</ID>"
                + "    </AssessmentFamilyIdentificationCode>"
                + "    <AssessmentFamilyIdentificationCode IdentificationSystem=\"secondRefIdentificationSystem\" AssigningOrganizationCode=\"secondRefAssigningOrganizationCode\" >"
                + "      <ID>secondRefId</ID>"
                + "    </AssessmentFamilyIdentificationCode>"
                + "    <AssessmentFamilyTitle>refFamilyTitle</AssessmentFamilyTitle>"
                + "    <Version>1</Version>"
                + "  </AssessmentFamilyIdentity>"
                + "</AssessmentFamilyReference>"
                + "</AssessmentFamily>"
                + "</InterchangeAssessmentMetadata>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiAssessmentFamilyXml, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidAssessmentFamilyNeutralRecord(neutralRecord);
    }

    @Ignore
    @Test
    public void csvAssessmentFamilyTest() throws Exception {

        String smooksConfig = "smooks_conf/smooks-assessmentFamily-csv.xml";
        String targetSelector = "csv-record";

        String assessmentFamilyCsv = "firstIdentificationSystem,firstAssigningOrganizationCode,firstId,State summative assessment 3-8 general,"
                + "Reading,Third grade,Fourth grade,State Standard,2002,2002-09-01,the nomenclature,theid,theref,code value,short desc,descript,"
                + "tk31,TAKSReading3-1,firstRefIdentificationSystem,firstRefAssigningOrganizationCode,firstRefId,refFamilyTitle,1";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                assessmentFamilyCsv, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidAssessmentFamilyNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidAssessmentFamilyNeutralRecord(NeutralRecord neutralRecord) {

        assertEquals("record type was not AssessmentFamily", "assessmentFamily", neutralRecord.getRecordType());

        assertEquals("AssessmentFamilyTitle does not match", "familyTitle",
                neutralRecord.getAttributes().get("AssessmentFamilyTitle"));

        List identificationCodeList = (List) neutralRecord.getAttributes().get("AssessmentFamilyIdentificationCode");
        assertNotNull("AssessmentFamilyIdentificationCode list is null", identificationCodeList);
        assertFalse("empty AssessmentFamilyIdentificationCode list", identificationCodeList.isEmpty());

        Map firstIdCodeMap = (Map) identificationCodeList.get(0);
        assertNotNull("first AssessmentFamilyIdentificationCode map is null", firstIdCodeMap);
        EntityTestUtils.assertObjectInMapEquals(firstIdCodeMap, "ID", "firstId");
        EntityTestUtils.assertObjectInMapEquals(firstIdCodeMap, "IdentificationSystem", "firstIdentificationSystem");
        EntityTestUtils.assertObjectInMapEquals(firstIdCodeMap, "AssigningOrganizationCode",
                "firstAssigningOrganizationCode");

        if (identificationCodeList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            Map secondIdCodeMap = (Map) identificationCodeList.get(1);
            assertNotNull("second AssessmentFamilyIdentificationCode map is null", secondIdCodeMap);
            EntityTestUtils.assertObjectInMapEquals(secondIdCodeMap, "ID", "secondId");
            EntityTestUtils.assertObjectInMapEquals(secondIdCodeMap, "IdentificationSystem",
                    "secondIdentificationSystem");
            EntityTestUtils.assertObjectInMapEquals(secondIdCodeMap, "AssigningOrganizationCode",
                    "secondAssigningOrganizationCode");
        }

        assertEquals("AssessmentCategory does not match", "State summative assessment 3-8 general", neutralRecord
                .getAttributes().get("AssessmentCategory"));
        assertEquals("AcademicSubject does not match", "Reading", neutralRecord.getAttributes().get("AcademicSubject"));
        assertEquals("GradeLevelAssessed does not match", "Third grade",
                neutralRecord.getAttributes().get("GradeLevelAssessed"));

        assertEquals("LowestGradeLevelAssessed does not match", "Fourth grade",
                neutralRecord.getAttributes().get("LowestGradeLevelAssessed"));

        assertEquals("ContentStandard does not match", "State Standard",
                neutralRecord.getAttributes().get("ContentStandard"));
        assertEquals("Version does not match", "2002", neutralRecord.getAttributes().get("Version"));
        assertEquals("Nomenclature does not match", "the nomenclature",
                neutralRecord.getAttributes().get("Nomenclature"));

        List assessmentPeriodsList = (List) neutralRecord.getAttributes().get("AssessmentPeriods");
        assertNotNull("AssessmentPeriods list is null", assessmentPeriodsList);
        assertFalse("empty AssessmentPeriods list", assessmentPeriodsList.isEmpty());
        Map firstAssesmentPeriod = (Map) assessmentPeriodsList.get(0);
        List firstCodeValueChoiceList = (List) firstAssesmentPeriod.get("CodeValues");
        if (!firstCodeValueChoiceList.isEmpty()) {
            assertEquals("code value", firstCodeValueChoiceList.get(0));
        }
        List firstShortDescriptionChoiceList = (List) firstAssesmentPeriod.get("ShortDescriptions");
        if (!firstShortDescriptionChoiceList.isEmpty()) {
            assertEquals("short desc", firstShortDescriptionChoiceList.get(0));
        }
        List firstDescriptionChoiceList = (List) firstAssesmentPeriod.get("Descriptions");
        if (!firstDescriptionChoiceList.isEmpty()) {
            assertEquals("descript", firstDescriptionChoiceList.get(0));
        }

        if (assessmentPeriodsList.size() > 1) {
            Map secondAssesmentPeriod = (Map) assessmentPeriodsList.get(1);
            List secondCodeValueChoiceList = (List) secondAssesmentPeriod.get("CodeValues");
            if (!secondCodeValueChoiceList.isEmpty()) {
                assertEquals("code value2", secondCodeValueChoiceList.get(0));
            }
            List secondShortDescriptionChoiceList = (List) secondAssesmentPeriod.get("ShortDescriptions");
            if (!secondShortDescriptionChoiceList.isEmpty()) {
                assertEquals("short desc2", secondShortDescriptionChoiceList.get(0));
            }
            List secondDescriptionChoiceList = (List) secondAssesmentPeriod.get("Descriptions");
            if (!secondDescriptionChoiceList.isEmpty()) {
                assertEquals("descript2", secondDescriptionChoiceList.get(0));
            }
        }
    }
}
