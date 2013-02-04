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
 * Test the smooks mappings for StaffCohortAssociation entity.
 *
 * @author slee
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class DisciplineActionEntityTest {

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
     * Test that Ed-Fi staffCohortAssociation is correctly mapped to a NeutralRecord.
     *
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentDiscipline/DisciplineAction";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/DisciplineActionEntity.xml");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiXml, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("unchecked")
    private void checkValidNeutralRecord(NeutralRecord neutralRecord) {
        assertEquals("Expecting different record type", "disciplineAction", neutralRecord.getRecordType());

        assertEquals("Expected 0 local parent ids", 0, neutralRecord.getLocalParentIds().size());

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different number of attributes", 13, attributes.size());

        assertEquals("Expected different disciplineActionIdentifier", "cap0-lea0-sch1-da0", attributes.get("disciplineActionIdentifier"));
        assertEquals("Expected different disciplineDate", "2011-03-04", attributes.get("disciplineDate"));
        assertEquals("Expected different disciplineActionLength", 74, attributes.get("disciplineActionLength"));
        assertEquals("Expected different actualDisciplineActionLength", 64, attributes.get("actualDisciplineActionLength"));
        assertEquals("Expected different disciplineActionLengthDifferenceReason", "Term Modified By Mutual Agreement", attributes.get("disciplineActionLengthDifferenceReason"));

        List<Map<String, Object>> studentReferences = (List<Map<String, Object>>) attributes.get("StudentReference");
        assertNotNull("Expected non-null list of studentReferences", studentReferences);
        assertEquals("Expected 2 student references", 2, studentReferences.size());

        Map<String, Object> studentOuterMap1 = studentReferences.get(0);
        Map<String, Object> studentInnerMap1 = (Map<String, Object>) studentOuterMap1.get("StudentIdentity");
        assertEquals("Expected different studentUniqueStateId", "900000016", studentInnerMap1.get("StudentUniqueStateId"));

        Map<String, Object> studentOuterMap2 = studentReferences.get(1);
        Map<String, Object> studentInnerMap2 = (Map<String, Object>) studentOuterMap2.get("StudentIdentity");
        assertEquals("Expected different studentUniqueStateId", "100000017", studentInnerMap2.get("StudentUniqueStateId"));

        List<Map<String, Object>> staffReferences = (List<Map<String, Object>>) attributes.get("StaffReference");
        assertNotNull("Expected non-null list of staffReferences", staffReferences);
        assertEquals("Expected 2 staff references", 2, staffReferences.size());

        Map<String, Object> staffOuterMap1 = staffReferences.get(0);
        Map<String, Object> staffInnerMap1 = (Map<String, Object>) staffOuterMap1.get("StaffIdentity");
        assertEquals("Expected different staffUniqueStateId", "cgray", staffInnerMap1.get("StaffUniqueStateId"));

        Map<String, Object> staffOuterMap2 = staffReferences.get(1);
        Map<String, Object> staffInnerMap2 = (Map<String, Object>) staffOuterMap2.get("StaffIdentity");
        assertEquals("Expected different staffUniqueStateId", "linda.kim", staffInnerMap2.get("StaffUniqueStateId"));

        List<Map<String, Object>> disciplineIncidentReferences = (List<Map<String, Object>>) attributes.get("DisciplineIncidentReference");
        assertNotNull("Expected non-null list of disciplineIncidentReferences", disciplineIncidentReferences);
        assertEquals("Expected 1 disciplineIncidentReferences", 1, disciplineIncidentReferences.size());

        Map<String, Object> disciplineOuterMap1 = disciplineIncidentReferences.get(0);
        Map<String, Object> disciplineInnerMap1 = (Map<String, Object>) disciplineOuterMap1.get("DisciplineIncidentIdentity");
        assertEquals("Expected different discipline incidentIdentifier", "di-10001", disciplineInnerMap1.get("IncidentIdentifier"));

        Map<String, Object> responsibilitySchoolReference = (Map<String, Object>) attributes.get("ResponsibilitySchoolReference");
        Map<String, Object> responsibilitySchooIdentity = (Map<String, Object>) responsibilitySchoolReference.get("EducationalOrgIdentity");
        String responsibilitySchool = (String) responsibilitySchooIdentity.get("StateOrganizationId");
        assertEquals("Expected different responsibilitySchool stateOrganizationId", "Daybreak Central High", responsibilitySchool);

        Map<String, Object> assignmentSchoolReference = (Map<String, Object>) attributes.get("AssignmentSchoolReference");
        Map<String, Object> assignmentSchooIdentity = (Map<String, Object>) assignmentSchoolReference.get("EducationalOrgIdentity");
        String assignmentSchool = (String) assignmentSchooIdentity.get("StateOrganizationId");
        assertEquals("Expected different assignmentSchool stateOrganizationId", "Daybreak Central High", assignmentSchool);

        List<List<Map<String, Object>>> disciplines = (List<List<Map<String, Object>>>) attributes.get("disciplines");
        assertNotNull("Expected non-null list of disciplines", disciplines);
        assertEquals("Expected 2 discipliness", 2, disciplines.size());

        assertEquals("Expected different codeValue for discipline1", "DISCIPLINE 001", disciplines.get(0).get(0).get("codeValue"));
        assertEquals("Expected different shortDescription for discipline1", "Discipline 001 description", disciplines.get(0).get(1).get("shortDescription"));
        assertEquals("Expected different description for discipline1", "Suspension from school for a week", disciplines.get(0).get(2).get("description"));

        assertEquals("Expected different codeValue for discipline2", "DISCIPLINE 002", disciplines.get(1).get(0).get("codeValue"));
        assertEquals("Expected different shortDescription for discipline2", "Discipline 002 description", disciplines.get(1).get(1).get("shortDescription"));
        assertEquals("Expected different description for discipline2", "Suspension from school for 5 days", disciplines.get(1).get(2).get("description"));
    }

}
