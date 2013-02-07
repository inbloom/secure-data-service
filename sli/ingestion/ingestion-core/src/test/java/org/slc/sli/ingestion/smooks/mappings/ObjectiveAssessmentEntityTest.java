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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
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
import org.slc.sli.ingestion.transformation.assessment.ObjectiveAssessmentBuilder;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/recordLvlHash-context.xml" })
public class ObjectiveAssessmentEntityTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLevelDeltaEnabledEntityNames;

    private String validXmlTestData = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
            + "<ObjectiveAssessment>"
            + "  <IdentificationCode>TAKSReading3-4</IdentificationCode>"
            + "  <MaxRawScore>8</MaxRawScore>"
            + "  <PercentOfAssessment>50</PercentOfAssessment>"
            + "  <Nomenclature>nomenclature</Nomenclature>"
            + "  <AssessmentPerformanceLevel>"
            + "    <AssessmentReportingMethod>ACT score</AssessmentReportingMethod>"
            + "    <MinimumScore>1</MinimumScore>"
            + "    <MaximumScore>20</MaximumScore>"
            + "    <PerformanceLevel>"
            + "      <Description>description</Description>"
            + "      <CodeValue>codevalue</CodeValue>"
            + "    </PerformanceLevel>"
            + "  </AssessmentPerformanceLevel>"
            + "  <AssessmentItemReference>"
            + "  <AssessmentItemIdentity>"
            + "  <AssessmentItemIdentificationCode>EOA12</AssessmentItemIdentificationCode>"
            + "  </AssessmentItemIdentity>"
            + "  </AssessmentItemReference>"
            + "  <LearningObjectiveReference>"
            + "    <LearningObjectiveIdentity>"
            + "       <Objective>Expository Writing</Objective>"
            + "       <AcademicSubject>Writing</AcademicSubject>"
            + "       <ObjectiveGradeLevel>Second grade</ObjectiveGradeLevel>"
            + "    </LearningObjectiveIdentity>"
            + "  </LearningObjectiveReference>"
            + "  <LearningStandardReference>"
            + "      <LearningStandardIdentity>"
            + "        <IdentificationCode>Reading3-4</IdentificationCode>"
            + "    </LearningStandardIdentity>"
            + "  </LearningStandardReference>"
            + "  <ObjectiveAssessmentReference>"
            + "    <ObjectiveAssessmentIdentity>"
            + "       <ObjectiveAssessmentIdentificationCode>sub</ObjectiveAssessmentIdentificationCode>"
            + "     </ObjectiveAssessmentIdentity>"
            + "  </ObjectiveAssessmentReference>"
            + "</ObjectiveAssessment>"
            + "<ObjectiveAssessment>"
            + "  <IdentificationCode>sub</IdentificationCode>"
            + "</ObjectiveAssessment>"
            + "</InterchangeAssessmentMetadata>";

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;
    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidObjectiveAssessmentXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/ObjectiveAssessment";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidObjectiveAssessmentNeutralRecord(neutralRecord);
    }

    @Test
    public void testInvalidObjectiveAssessmentXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/ObjectiveAssessment";

        String invalidXmlTestData = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
                + "<ObjectiveAssessment>"
                + "<IdentificationCode>TAKSReading3-4</IdentificationCode>"
                + "<MaxRawScore>8</MaxRawScore>"
                + "<PercentOfAssessment>50</PercentOfAssessment>"
                + "<Nomenclature>nomenclature</Nomenclature>"
                + "</ObjectiveAssessment>" + "</InterchangeAssessmentMetadata>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkInvalidObjectiveAssessmentNeutralRecord(neutralRecord);

    }

    @SuppressWarnings("unchecked")
    private void checkValidObjectiveAssessmentNeutralRecord(NeutralRecord neutralRecord) {
        Map<String, Object> entity = neutralRecord.getAttributes();

        Assert.assertEquals("TAKSReading3-4", entity.get("id"));
        Assert.assertEquals("TAKSReading3-4", entity.get("identificationCode"));
        Assert.assertEquals("8", entity.get("maxRawScore").toString());
        Assert.assertEquals("50", entity.get("percentOfAssessment").toString());
        Assert.assertEquals("nomenclature", entity.get("nomenclature"));

        List<?> subObjectiveAssessments = (List<?>) entity.get(ObjectiveAssessmentBuilder.SUB_OBJECTIVE_REFS);
        String subObjectiveAssessment = (String) subObjectiveAssessments.get(0);
        Assert.assertEquals("sub", subObjectiveAssessment);

        List<Map<String, Object>> learningObjectives = (List<Map<String, Object>>) entity.get("learningObjectives");
        Assert.assertNotNull(learningObjectives);
        Assert.assertEquals(1, learningObjectives.size());

        Map<String, Object> loIdentity = (Map<String, Object>) learningObjectives.get(0).get("LearningObjectiveIdentity");
        Assert.assertNotNull(loIdentity);
        Assert.assertEquals("Expository Writing", loIdentity.get("Objective"));
        Assert.assertEquals("Writing", loIdentity.get("AcademicSubject"));
        Assert.assertEquals("Second grade", loIdentity.get("ObjectiveGradeLevel"));
    }

    private void checkInvalidObjectiveAssessmentNeutralRecord(NeutralRecord neutralRecord) {
        Map<String, Object> entity = neutralRecord.getAttributes();

        Assert.assertEquals("TAKSReading3-4", entity.get("id"));

        Assert.assertEquals("8", entity.get("maxRawScore").toString());
        Assert.assertEquals("50", entity.get("percentOfAssessment").toString());
        Assert.assertEquals("nomenclature", entity.get("nomenclature"));
    }

}
