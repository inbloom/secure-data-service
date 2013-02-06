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
public class ProgramEntityTest {

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

        String targetSelector = "InterchangeEducationOrganization/Program";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/ProgramEntity.xml");
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
        assertEquals("Expecting different record type", "program", neutralRecord.getRecordType());

        assertEquals("Expected no local parent ids", 0, neutralRecord.getLocalParentIds().size());

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different number of attributes", 4, attributes.size());
        assertEquals("Expected different entity id", "ACC-TEST-PROG-1", attributes.get("programId"));
        assertEquals("Expected different program sponsor", "State Education Agency", attributes.get("programSponsor"));
        assertEquals("Expected different program type", "Regular Education", attributes.get("programType"));

        @SuppressWarnings("unchecked")
        List<List<Map<String, Object>>> services = (List<List<Map<String, Object>>>) attributes.get("services");

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
        assertEquals("Expected different short description of service", "Short description for acceptance test program service 1", choice.get("ShortDescription"));

        choice = service1.get(2);
        assertNotNull("Expected non-null service choice", choice);
        assertEquals("Expected different description of service", "This is a longer description of the services provided by acceptance test program service 1. More detail could be provided here.", choice.get("Description"));

        List<Map<String, Object>> service2 = services.get(1);
        assertNotNull("Expected non-null service", service2);
        assertEquals("Expected three choices", 3, service2.size());

        choice = service2.get(0);
        assertNotNull("Expected non-null service choice", choice);
        assertEquals("Expected different service code value", "Test service 2", choice.get("CodeValue"));

        choice = service2.get(1);
        assertNotNull("Expected non-null service choice", choice);
        assertEquals("Expected different short description of service", "Short description for acceptance test program service 2", choice.get("ShortDescription"));

        choice = service2.get(2);
        assertNotNull("Expected non-null service choice", choice);
        assertEquals("Expected different description of service", "This is a longer description of the services provided by acceptance test program service 2. More detail could be provided here.", choice.get("Description"));
    }

}
