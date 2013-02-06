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

import java.io.FileNotFoundException;
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
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Test the smooks mappings for Program entity.
 *
 * @author vmcglaughlin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/recordLvlHash-context.xml" })
public class StudentDisciplineIncidentAssociationEntityTest {

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

    /**
     * Test that Ed-Fi program is correctly mapped to a NeutralRecord.
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentDiscipline/StudentDisciplineIncidentAssociation";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/StudentDisciplineIncidentAssociationEntity.xml");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        NeutralRecord neutralRecord = EntityTestUtils
                .smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                        targetSelector, edfiXml, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidNeutralRecord(neutralRecord);
    }

    private void checkValidNeutralRecord(NeutralRecord neutralRecord) {
        assertEquals("Expecting different record type", "studentDisciplineIncidentAssociation", neutralRecord.getRecordType());

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different number of attributes", 5, attributes.size());

        assertEquals("Expected different StudentParticipationCode", "Perpetrator", attributes.get("StudentParticipationCode"));

        Map<String, Object> disciplineIncidentReference = (Map<String, Object>) attributes
                .get("DisciplineIncidentReference");
        assertNotNull("Expected non-null DisciplineIncidentReference", disciplineIncidentReference);

        //behaviors
        @SuppressWarnings("unchecked")
        List<List<Map<String, Object>>> behaviors = (List<List<Map<String, Object>>>) attributes.get("Behaviors");
        assertNotNull("Expected non-null list of behaviors", behaviors);
        assertEquals("Expected two behaviors", 2, behaviors.size());

        List<Map<String, Object>> behavior = behaviors.get(0);
        assertNotNull("Expected non-null behavior", behavior);
        assertEquals("Expected three choices", 3, behavior.size());

        Map<String, Object> choice = behavior.get(0);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different behavior code value", "Code Value 1", choice.get("CodeValue"));

        choice = behavior.get(1);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different short description of behavior", "Short Description 1", choice.get("ShortDescription"));

        choice = behavior.get(2);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different description of behavior", "Description 1", choice.get("Description"));

        behavior = behaviors.get(1);
        assertNotNull("Expected non-null behavior", behavior);
        assertEquals("Expected three choices", 3, behavior.size());

        choice = behavior.get(0);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different behavior code value", "Code Value 2", choice.get("CodeValue"));

        choice = behavior.get(1);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different short description of behavior", "Short Description 2", choice.get("ShortDescription"));

        choice = behavior.get(2);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different description of behavior", "Description 2", choice.get("Description"));

        //secondary behaviors
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> secondaryBehaviors = (List<Map<String, Object>>) attributes.get("SecondaryBehaviors");
        assertNotNull("Expected non-null list of secondaryBehaviors", secondaryBehaviors);
        assertEquals("Expected two secondaryBehaviors", 2, secondaryBehaviors.size());

        Map<String, Object> secondaryBehavior = secondaryBehaviors.get(0);
        assertNotNull("Expected non-null secondaryBehavior", secondaryBehavior);
        assertEquals("Expected different secondaryBehavior category", "School Code of Conduct", secondaryBehavior.get("BehaviorCategory"));
        assertEquals("Expected different secondaryBehavior", "Mischief", secondaryBehavior.get("SecondaryBehavior"));

        secondaryBehavior = secondaryBehaviors.get(1);
        assertNotNull("Expected non-null secondaryBehavior", secondaryBehavior);
        assertEquals("Expected different secondaryBehavior category", "State Law Crime", secondaryBehavior.get("BehaviorCategory"));
        assertEquals("Expected different secondaryBehavior", "Hair Pulling", secondaryBehavior.get("SecondaryBehavior"));

        //student
        @SuppressWarnings("unchecked")
        Map<String, Object> studentReference = (Map<String, Object>) attributes.get("StudentReference");
        assertNotNull("Expected non-null student reference", studentReference);
        @SuppressWarnings("unchecked")
        Map<String, Object> studentIdentity = (Map<String, Object>) studentReference.get("StudentIdentity");
        assertNotNull("Expected non-null student identity", studentIdentity);
        assertEquals("Expected different student id", "Student Unique State Id", studentIdentity.get("StudentUniqueStateId"));
    }
}
