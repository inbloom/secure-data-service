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
 * Test the smooks mappings for StaffProgramAssociation entity.
 *
 * @author vmcglaughlin
 *
 */
public class StaffProgramAssociationEntityTest {

    /**
     * Test that Ed-Fi staffProgramAssociation is correctly mapped to a NeutralRecord.
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStaffAssociation/StaffProgramAssociation";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/StaffProgramAssociationEntity.xml");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        NeutralRecord neutralRecord = EntityTestUtils
                .smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                        targetSelector, edfiXml);

        checkValidNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("unchecked")
    private void checkValidNeutralRecord(NeutralRecord neutralRecord) {
        assertEquals("Expecting different record type", "staffProgramAssociation", neutralRecord.getRecordType());

        assertEquals("Expected 0 local parent ids", 0, neutralRecord.getLocalParentIds().size());

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different number of attributes", 7, attributes.size());

        List<Map<String, Object>> staffReferences = (List<Map<String, Object>>) attributes.get("staffReference");
        assertNotNull("Expected non-null list of staff references", staffReferences);
        assertEquals("Expected 2 staff references", 2, staffReferences.size());

        Map<String, Object> staffOuterMap1 = staffReferences.get(0);
        Map<String, Object> staffInnerMap1 = (Map<String, Object>) staffOuterMap1.get("staffIdentity");
        assertEquals("Expected different staff unique state id", "linda.kim", staffInnerMap1.get("staffUniqueStateId"));

        Map<String, Object> staffOuterMap2 = staffReferences.get(1);
        Map<String, Object> staffInnerMap2 = (Map<String, Object>) staffOuterMap2.get("staffIdentity");
        assertEquals("Expected different staff unique state id", "rbraverman", staffInnerMap2.get("staffUniqueStateId"));

        List<Map<String, Object>> programReferences = (List<Map<String, Object>>) attributes.get("programReference");
        assertNotNull("Expected non-null list of program references", programReferences);
        assertEquals("Expected 2 program references", 2, programReferences.size());

        Map<String, Object> programOuterMap1 = programReferences.get(0);
        Map<String, Object> programInnerMap1 = (Map<String, Object>) programOuterMap1.get("programIdentity");
        assertEquals("Expected different program id", "ACC-TEST-PROG-1", programInnerMap1.get("programId"));

        Map<String, Object> programOuterMap2 = programReferences.get(1);
        Map<String, Object> programInnerMap2 = (Map<String, Object>) programOuterMap2.get("programIdentity");
        assertEquals("Expected different program id", "ACC-TEST-PROG-2", programInnerMap2.get("programId"));

        assertEquals("Expected different begin date", "2011-01-01", attributes.get("beginDate"));
        assertEquals("Expected different end date", "2012-02-15", attributes.get("endDate"));
        assertEquals("Expected different student record access", true, attributes.get("studentRecordAccess"));
    }

}
