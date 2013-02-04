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

import junit.framework.Assert;

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
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LearningObjectiveTest {

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

    private String validXmlTestData = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
            + "<LearningObjective>"
            + "  <Objective>objective text</Objective>"
            + "  <Description>description</Description>"
            + "  <AcademicSubject>ELA</AcademicSubject>"
            + "  <ObjectiveGradeLevel>Twelfth grade</ObjectiveGradeLevel>"
            + "  <LearningObjectiveReference>"
            + "    <LearningObjectiveIdentity>"
            + "       <Objective>Expository Writing</Objective>"
            + "       <AcademicSubject>Writing</AcademicSubject>"
            + "       <ObjectiveGradeLevel>Second grade</ObjectiveGradeLevel>"
            + "    </LearningObjectiveIdentity>"
            + "  </LearningObjectiveReference>"
            + "  <LearningStandardReference>"
            + "    <LearningStandardIdentity>"
            + "       <IdentificationCode>standard_id</IdentificationCode>"
            + "    </LearningStandardIdentity>"
            + "  </LearningStandardReference>"
            + "</LearningObjective>" + "</InterchangeAssessmentMetadata>";

    private String validXmlTestData_SG = "<InterchangeStudentGrade xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
            + "<LearningObjective>"
            + "  <Objective>objective text</Objective>"
            + "  <Description>description</Description>"
            + "  <AcademicSubject>ELA</AcademicSubject>"
            + "  <ObjectiveGradeLevel>Twelfth grade</ObjectiveGradeLevel>"
            + "  <LearningObjectiveReference>"
            + "    <LearningObjectiveIdentity>"
            + "       <Objective>Expository Writing</Objective>"
            + "       <AcademicSubject>Writing</AcademicSubject>"
            + "       <ObjectiveGradeLevel>Second grade</ObjectiveGradeLevel>"
            + "    </LearningObjectiveIdentity>"
            + "  </LearningObjectiveReference>"
            + "  <LearningStandardReference>"
            + "    <LearningStandardIdentity>"
            + "       <IdentificationCode>standard_id</IdentificationCode>"
            + "    </LearningStandardIdentity>"
            + "  </LearningStandardReference>"
            + "</LearningObjective>" + "</InterchangeStudentGrade>";

    @Test
    public void testLearningObjectiveXMLInAssessments() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/LearningObjective";

        NeutralRecord nr = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidLO(nr);
    }

    @Test
    public void testLearningObjectiveXMLInStudentGrade() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentGrade/LearningObjective";

        NeutralRecord nr = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData_SG, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidLO(nr);
    }

    @SuppressWarnings("unchecked")
    private void checkValidLO(NeutralRecord nr) {
        Map<String, Object> m = nr.getAttributes();
        Assert.assertEquals("objective text", m.get("objective"));
        Assert.assertEquals("description", m.get("description"));
        Assert.assertEquals("ELA", m.get("academicSubject"));
        Assert.assertEquals("Twelfth grade", m.get("objectiveGradeLevel"));

        List<Map<String, Object>> learningObjectives = (List<Map<String, Object>>) m.get("learningObjectiveRefs");
        Assert.assertEquals(1, learningObjectives.size());
        Map<String, Object> learningObjectiveRef = learningObjectives.get(0);
        Assert.assertNotNull(learningObjectiveRef);
        Map<String, Object> learningObjectiveIdentity = (Map<String, Object>) learningObjectiveRef.get("LearningObjectiveIdentity");
        Assert.assertNotNull(learningObjectiveIdentity);

        Assert.assertEquals("Expository Writing", learningObjectiveIdentity.get("Objective"));
        Assert.assertEquals("Writing", learningObjectiveIdentity.get("AcademicSubject"));
        Assert.assertEquals("Second grade", learningObjectiveIdentity.get("ObjectiveGradeLevel"));

        List<Map<String, Object>> learningStandards = (List<Map<String, Object>>) m.get("LearningStandardReference");
        Assert.assertEquals(1, learningStandards.size());
        Map<String, Object> learningStandardRef = learningStandards.get(0);

        Assert.assertNotNull(learningStandardRef);
        Map<String, Object> learningStandardIdentity = (Map<String, Object>) learningStandardRef.get("LearningStandardIdentity");
        Assert.assertNotNull(learningStandardIdentity);

        Assert.assertEquals("standard_id", learningStandardIdentity.get("IdentificationCode"));
    }
}
