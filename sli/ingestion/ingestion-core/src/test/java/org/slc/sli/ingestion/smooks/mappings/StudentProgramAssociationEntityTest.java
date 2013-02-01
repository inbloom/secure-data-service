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
 * Test the smooks mappings for StudentProgramAssociation entity.
 *
 * @author vmcglaughlin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentProgramAssociationEntityTest {

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
     * Test that Ed-Fi studentProgramAssociation is correctly mapped to a NeutralRecord.
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentProgram/StudentProgramAssociation";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/StudentProgramAssociationEntity.xml");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        NeutralRecord neutralRecord = EntityTestUtils
                .smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                        targetSelector, edfiXml, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("unchecked")
    private void checkValidNeutralRecord(NeutralRecord neutralRecord) {
        assertEquals("Expecting different record type", "studentProgramAssociation", neutralRecord.getRecordType());

        assertEquals("Expected 0 local parent ids", 0, neutralRecord.getLocalParentIds().size());

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different number of attributes", 7, attributes.size());

        Map<String, Object> studentReference = (Map<String, Object>) attributes.get("StudentReference");
        assertNotNull("Expected non-null student reference", studentReference);
        Map<String, Object> studentIdentity = (Map<String, Object>) studentReference.get("StudentIdentity");
        assertNotNull("Expected non-null student identity", studentIdentity);
        assertEquals("Expected different student id", "100000000", studentIdentity.get("StudentUniqueStateId"));

        Map<String, Object> programReference = (Map<String, Object>) attributes.get("ProgramReference");
        assertNotNull("Expected non-null student reference", studentReference);
        Map<String, Object> programIdentity = (Map<String, Object>) programReference.get("ProgramIdentity");
        assertNotNull("Expected non-null program identity", programIdentity);
        assertEquals("Expected different program id", "ACC-TEST-PROG-1", programIdentity.get("ProgramId"));

        Map<String, Object> educationOrganizationReference =
                (Map<String, Object>) attributes.get("EducationOrganizationReference");
        assertNotNull("Expected non-null education organization reference", educationOrganizationReference);
        Map<String, Object> educationalOrgIdentity =
                (Map<String, Object>) educationOrganizationReference.get("EducationalOrgIdentity");
        assertNotNull("Expected non-null educational org identity", educationalOrgIdentity);
        String stateOrganizationIds = (String) educationalOrgIdentity.get("StateOrganizationId");
        assertNotNull("Expected non-null list of state organization ids", stateOrganizationIds);
        assertEquals("Expected different education organization id", "IL", stateOrganizationIds);

        assertEquals("Expected different begin date", "2011-01-01", attributes.get("BeginDate"));
        assertEquals("Expected different end date", "2011-12-31", attributes.get("EndDate"));
        assertEquals("Expected different reason exited", "Program completion", attributes.get("ReasonExited"));

        List<List<Map<String, Object>>> services = (List<List<Map<String, Object>>>) attributes.get("Services");

        assertNotNull("Expected non-null list of services", services);

        assertEquals("Expected two services", 2, services.size());

        List<Map<String, Object>> service1 = services.get(0);
        assertNotNull("Expected non-null service", service1);
        assertEquals("Expected three choices", 3, service1.size());

        Map<String, Object> choice = service1.get(0);
        assertNotNull("Expected non-null service choice", choice);
        assertEquals("Expected different service code value", "Test service 1", choice.get("CodeValue"));

        choice = service1.get(1);
        assertNotNull("Expected non-null service choice", choice);
        assertEquals("Expected different short description of service",
                "Short description for acceptance test studentProgramAssociation service 1",
                choice.get("ShortDescription"));

        choice = service1.get(2);
        assertNotNull("Expected non-null service choice", choice);
        assertEquals("Expected different description of service",
                "This is a longer description of the services provided by acceptance test studentProgramAssociation service 1. More detail could be provided here.",
                choice.get("Description"));

        List<Map<String, Object>> service2 = services.get(1);
        assertNotNull("Expected non-null service", service2);
        assertEquals("Expected three choices", 3, service1.size());

        choice = service2.get(0);
        assertNotNull("Expected non-null service choice", choice);
        assertEquals("Expected different service code value", "Test service 2", choice.get("CodeValue"));

        choice = service2.get(1);
        assertNotNull("Expected non-null service choice", choice);
        assertEquals("Expected different short description of service",
                "Short description for acceptance test studentProgramAssociation service 2",
                choice.get("ShortDescription"));

        choice = service2.get(2);
        assertNotNull("Expected non-null service choice", choice);
        assertEquals("Expected different description of service",
                "This is a longer description of the services provided by acceptance test studentProgramAssociation service 2. More detail could be provided here.",
                choice.get("Description"));
    }

}
