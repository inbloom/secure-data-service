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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.xml.sax.SAXException;

/**
 * Test the smooks mappings for Cohort entity.
 *
 * @author syau
 *
 */
public class CohortEntityTest {

    /**
     * Test that Ed-Fi program is correctly mapped to a NeutralRecord.
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentCohort/Cohort";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/CohortEntity.xml");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        NeutralRecord neutralRecord = EntityTestUtils
                .smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                        targetSelector, edfiXml);

        checkValidNeutralRecord(neutralRecord);
    }

    private void checkValidNeutralRecord(NeutralRecord neutralRecord) {
        assertEquals("Expecting different record type", "cohort", neutralRecord.getRecordType());

        assertEquals("Expected no local parent ids", 0, neutralRecord.getLocalParentIds().size());

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different number of attributes", 8, attributes.size());
        assertEquals("Expected different entity id", "ACC-TEST-COH-1", attributes.get("cohortIdentifier"));
        assertEquals("Expected different description", "Statewide academic intervention cohort for English", attributes.get("cohortDescription"));
        assertEquals("Expected different scope", "Statewide", attributes.get("cohortScope"));
        assertEquals("Expected different cohort type", "Academic Intervention", attributes.get("cohortType"));
        assertEquals("Expected different academic subject", "English", attributes.get("academicSubject"));
        assertEquals("Expected different entity id", "ACC-TEST-COH-1", attributes.get("cohortIdentifier"));

        @SuppressWarnings("unchecked")
        List<Map<String, Map<String, Object>>> programRefs = (List<Map<String, Map<String, Object>>>) attributes.get("programReferences");

        assertNotNull("Expected non-null list of program references", programRefs);
        assertEquals("Expected two program references", 2, programRefs.size());

        // Check program references
        Map<String, Map<String, Object>> programRef1 = programRefs.get(0);
        assertNotNull("Expected non-null program reference", programRef1);

        Map<String, Object> program1 = programRef1.get("ProgramIdentity");
        String progId = (String) program1.get("ProgramId");
        assertEquals("Expected different program id", "ACC-TEST-PROG-1", progId);

        Map<String, Map<String, Object>> programRef2 = programRefs.get(1);
        assertNotNull("Expected non-null program reference", programRef2);

        Map<String, Object> program2 = programRef2.get("ProgramIdentity");
        assertNotNull("Expected non-null service choice", program2);
        assertEquals("Expected different program type", "Alternative Education", program2.get("ProgramType"));
        @SuppressWarnings("unchecked")
        List<String> stateOrgIdList = (List<String>) program2.get("StateOrganizationId");
        assertEquals("Expected number of state org ids", 1, stateOrgIdList.size());
        assertEquals("Expected different state org id", "IL", stateOrgIdList.get(0));

        // Check edOrg references
        @SuppressWarnings("unchecked")
        Map<String, Map<String, List<Object>>> edOrgReference = (Map<String, Map<String, List<Object>>>) attributes.get("educationOrgReference");
        assertNotNull("Exepected non-null education organization reference", edOrgReference);
        Map<String, List<Object>> edOrgIdentity = edOrgReference.get("EducationalOrgIdentity");
        assertNotNull("Exepected non-null education organization identigy", edOrgIdentity);
        List<Object> stateOrgIds = edOrgIdentity.get("StateOrganizationId");
        assertEquals("Expected one state org id", 1, stateOrgIds.size());
        assertEquals("Expected difference state org id", "IL", stateOrgIds.get(0));

    }

}