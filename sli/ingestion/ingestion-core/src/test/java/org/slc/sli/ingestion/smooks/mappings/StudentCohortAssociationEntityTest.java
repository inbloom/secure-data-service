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
 * Test the smooks mappings for StudentCohortAssociation entity.
 *
 * @author vmcglaughlin
 *
 */
public class StudentCohortAssociationEntityTest {

    /**
     * Test that Ed-Fi studentCohortAssociation is correctly mapped to a NeutralRecord.
     *
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentCohort/StudentCohortAssociation";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/StudentCohortAssociationEntity.xml");
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
        assertEquals("Expecting different record type", "studentCohortAssociation", neutralRecord.getRecordType());

        assertEquals("Expected 0 local parent ids", 0, neutralRecord.getLocalParentIds().size());

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different number of attributes", 4, attributes.size());

        Map<String, Object> studentReference = (Map<String, Object>) attributes.get("studentId");
        assertNotNull("Expected non-null student reference", studentReference);
        Map<String, Object> studentIdentity = (Map<String, Object>) studentReference.get("StudentIdentity");
        assertNotNull("Expected non-null student identity", studentIdentity);
        assertEquals("Expected different student id", "100000000", studentIdentity.get("StudentUniqueStateId"));

        Map<String, Object> cohortReference = (Map<String, Object>) attributes.get("cohortId");
        assertNotNull("Expected non-null student reference", studentReference);
        Map<String, Object> cohortIdentity = (Map<String, Object>) cohortReference.get("CohortIdentity");
        assertNotNull("Expected non-null Cohort identity", cohortIdentity);
        assertEquals("Expected different Cohort id", "ACC-TEST-COH-1", cohortIdentity.get("CohortIdentifier"));
        List<Object> stateOrgIds = (List<Object>) cohortIdentity.get("StateOrganizationId");
        assertEquals("Exepcted different number of state org ids", 1, stateOrgIds.size());
        assertEquals("Expected different state org id", "IL", stateOrgIds.get(0));


    }

}