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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.validation.EntityValidator;

/**
 *
 * @author mpatel
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/entity-mapping.xml" })
public class StudentObjectiveAssessmentTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLevelDeltaEnabledEntityNames;

    @Autowired
    private EntityValidator validator;

    String xmlTestData = "<InterchangeStudentAssessment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentAssessment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<StudentObjectiveAssessment>"
            + "<ScoreResults AssessmentReportingMethod=\"Raw score\">"
            + "<Result>12</Result>"
            + "</ScoreResults>"
            + "<StudentAssessmentReference></StudentAssessmentReference>"
            + "<ObjectiveAssessmentReference>"
            + "<ObjectiveAssessmentIdentity>"
            + "<ObjectiveAssessmentIdentificationCode>TAKSReading8-1</ObjectiveAssessmentIdentificationCode>"
            + "</ObjectiveAssessmentIdentity>"
            + "</ObjectiveAssessmentReference>"
            + "</StudentObjectiveAssessment>"
            + "</InterchangeStudentAssessment>";

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;
    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidatorSection() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentAssessment/StudentObjectiveAssessment";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                xmlTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);
        EntityTestUtils.mapValidation(record.getAttributes(), "student.objective.assessment", validator);
    }

    @Test
    public void testValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentAssessment/StudentObjectiveAssessment";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                xmlTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);
        checkValidSectionNeutralRecord(record);
    }

    private void checkValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> scoreResultList = (List<Map<String, Object>>) entity.get("scoreResults");

        Assert.assertTrue(scoreResultList != null);
        Map<String, Object> scoreResult = scoreResultList.get(0);
        Assert.assertTrue(scoreResult != null);
        Assert.assertEquals("12", scoreResult.get("result").toString());
        Assert.assertEquals("Raw score", scoreResult.get("assessmentReportingMethod"));
        Assert.assertEquals("TAKSReading8-1", entity.get("objectiveAssessmentRef"));
    }
}
