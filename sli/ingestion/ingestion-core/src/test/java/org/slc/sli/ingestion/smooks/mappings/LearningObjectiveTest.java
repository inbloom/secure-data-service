/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecord;
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

    private String validXmlTestData = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
            + "<LearningObjective>"
            + "  <Objective>objective text</Objective>"
            + "  <Description>description</Description>"
            + "  <AcademicSubject>ELA</AcademicSubject>"
            + "  <ObjectiveGradeLevel>Twelfth grade</ObjectiveGradeLevel>"
            + "  <LearningObjectiveReference>"
            + "    <LearningObjectiveIdentity>"
            + "      <Objective>objective</Objective>"
            + "    </LearningObjectiveIdentity>"
            + "  </LearningObjectiveReference>"
            + "  <LearningObjectiveReference></LearningObjectiveReference>"
            + "  <LearningObjectiveReference>"
            + "    <LearningObjectiveIdentity>"
            + "      <LearningObjectiveId ContentStandardName=\"standard_name\">"
            + "        <IdentificationCode>objective_id</IdentificationCode>"
            + "      </LearningObjectiveId>"
            + "    </LearningObjectiveIdentity>"
            + "  </LearningObjectiveReference>"
            + "  <LearningStandardReference>"
            + "    <LearningStandardIdentity>"
            + "      <LearningStandardId ContentStandardName=\"standard_name\">"
            + "        <IdentificationCode>standard_id</IdentificationCode>"
            + "      </LearningStandardId>"
            + "    </LearningStandardIdentity>"
            + "  </LearningStandardReference>"
            + "</LearningObjective>" + "</InterchangeAssessmentMetadata>";

    @SuppressWarnings("unchecked")
    @Test
    public void testLearningObjectiveXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/LearningObjective";

        NeutralRecord nr = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData, recordLevelDeltaEnabledEntityNames);
        Map<String, Object> m = nr.getAttributes();
        Assert.assertEquals("objective text", m.get("objective"));
        Assert.assertEquals("description", m.get("description"));
        Assert.assertEquals("ELA", m.get("academicSubject"));
        Assert.assertEquals("Twelfth grade", m.get("objectiveGradeLevel"));
        List<Map<String, Object>> learningObjectives = (List<Map<String, Object>>) m.get("learningObjectiveRefs");
        Assert.assertNotNull(learningObjectives);
        Assert.assertEquals(3, learningObjectives.size());

        assertInList(learningObjectives, "objective", "objective");
        assertInList(learningObjectives, "learningObjectiveId.identificationCode", "objective_id");
        assertInList(learningObjectives, "learningObjectiveId.contentStandardName", "standard_name");

        List<Map<String, Object>> learningStandards = (List<Map<String, Object>>) m.get("learningStandardRefs");
        assertInList(learningStandards, "learningStandardId.identificationCode", "standard_id");
        assertInList(learningStandards, "learningStandardId.contentStandardName", "standard_name");
    }


    private static void assertInList(List<Map<String, Object>> maps, String field, String value) {
        for (Map<String, Object> m : maps) {
            if (field.indexOf(".") > -1) {
                String v = getByPath(field, m);
                if (v != null && v.equals(value)) {
                    return;
                }
            } else if (value.equals(m.get(field))) {
                return;
            }
        }
        Assert.fail("Did not find " + field + "=" + value + " in list");
    }

    @SuppressWarnings("unchecked")
    private static String getByPath(String name, Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        String[] path = name.split("\\.");
        for (int i = 0; i < path.length; i++) {
            Object obj = map.get(path[i]);
            if (obj == null) {
                return null;
            } else if (i == path.length - 1 && obj instanceof String) {
                return (String) obj;
            } else if (obj instanceof Map) {
                map = (Map<String, Object>) obj;
            } else {
                return null;
            }
        }
        return null;
    }
}
