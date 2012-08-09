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
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Test the smooks mappings for StaffCohortAssociation entity.
 *
 * @author slee
 *
 */
public class StaffCohortAssociationEntityTest {

    /**
     * Test that Ed-Fi staffCohortAssociation is correctly mapped to a NeutralRecord.
     *
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentCohort/StaffCohortAssociation";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/StaffCohortAssociationEntity.xml");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiXml);

        checkValidNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("unchecked")
    private void checkValidNeutralRecord(NeutralRecord neutralRecord) {
        assertEquals("Expecting different record type", "staffCohortAssociation", neutralRecord.getRecordType());

        assertEquals("Expected 0 local parent ids", 0, neutralRecord.getLocalParentIds().size());

        Map<String, Object> attributes = neutralRecord.getAttributes();
        assertEquals("Expected different number of attributes", 7, attributes.size());
        
        assertEquals("Expected different beginDate", "2011-01-01", attributes.get("beginDate"));
        assertEquals("Expected different endDate", "2011-01-01", attributes.get("endDate"));
        assertEquals("Expected different studentRecordAccess", Boolean.TRUE, attributes.get("studentRecordAccess"));
        
        List<Map<String, Object>> staffReferences = (List<Map<String, Object>>) attributes.get("staffReference");
        assertNotNull("Expected non-null list of staffReferences", staffReferences);
        assertEquals("Expected 2 staff references", 2, staffReferences.size());

        Map<String, Object> staffOuterMap1 = staffReferences.get(0);
        Map<String, Object> staffInnerMap1 = (Map<String, Object>) staffOuterMap1.get("staffIdentity");
        assertEquals("Expected different staffUniqueStateId", "linda.kim", staffInnerMap1.get("staffUniqueStateId"));

        Map<String, Object> staffOuterMap2 = staffReferences.get(1);
        Map<String, Object> staffInnerMap2 = (Map<String, Object>) staffOuterMap2.get("staffIdentity");
        assertEquals("Expected different staffUniqueStateId", "cgray", staffInnerMap2.get("staffUniqueStateId"));

        List<Map<String, Object>> cohortReferences = (List<Map<String, Object>>) attributes.get("cohortReference");
        assertNotNull("Expected non-null list of cohortReferences", cohortReferences);
        assertEquals("Expected 2 cohort references", 2, cohortReferences.size());

        Map<String, Object> cohortOuterMap1 = cohortReferences.get(0);
        Map<String, Object> cohortInnerMap1 = (Map<String, Object>) cohortOuterMap1.get("cohortIdentity");
        assertEquals("Expected different cohortIdentifier", "ACC-TEST-COH-1", cohortInnerMap1.get("cohortIdentifier"));

        Map<String, Object> cohortOuterMap2 = cohortReferences.get(1);
        Map<String, Object> cohortInnerMap2 = (Map<String, Object>) cohortOuterMap2.get("cohortIdentity");
        assertEquals("Expected different cohortIdentifier", "ACC-TEST-COH-2", cohortInnerMap2.get("cohortIdentifier"));
        
    }

}