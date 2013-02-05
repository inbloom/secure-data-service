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

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

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
 * Smooks test for AssessmentItem
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AssessmentItemTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLevelDeltaEnabledEntityNames;

    private String validXmlTestData = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
            + "<AssessmentItem>"
            + "  <IdentificationCode>test-code</IdentificationCode>"
            + "  <ItemCategory>List Question</ItemCategory>"
            + "  <MaxRawScore>100</MaxRawScore>"
            + "  <CorrectResponse>Hello World!</CorrectResponse>"
            + "  <LearningStandardReference>"
            + "    <LearningStandardIdentity>"
            + "        <IdentificationCode>id-code-1</IdentificationCode>"
            + "    </LearningStandardIdentity>"
            + "  </LearningStandardReference>"
            + "  <LearningStandardReference>"
            + "    <LearningStandardIdentity>"
            + "        <IdentificationCode>id-code-2</IdentificationCode>"
            + "    </LearningStandardIdentity>"
            + "  </LearningStandardReference>"
            + "  <Nomenclature>nomen</Nomenclature>"
            + "</AssessmentItem>" + "</InterchangeAssessmentMetadata>";

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;
    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLearningObjectiveXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/AssessmentItem";

        NeutralRecord nr = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, validXmlTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        Map<String, Object> m = nr.getAttributes();
        Assert.assertEquals("test-code", m.get("identificationCode"));
        Assert.assertEquals("List Question", m.get("itemCategory"));
        Assert.assertEquals(100, m.get("maxRawScore"));
        Assert.assertEquals("Hello World!", m.get("correctResponse"));

        List<Map<String, Object>> refs = (List<Map<String, Object>>) nr.getAttributes().get("learningStandards");
        Assert.assertNotNull(refs);
        Assert.assertEquals(2, refs.size());

        //put the ids in a set - we don't care about the order
        Set<String> lsIdSet = new HashSet<String>();

        for (Map<String, Object> lsRef : refs) {
            Map<String, Object> identityType = (Map<String, Object>) lsRef.get("LearningStandardIdentity");
            Assert.assertNotNull(identityType);
            Assert.assertTrue(identityType.containsKey("IdentificationCode"));
            lsIdSet.add((String) identityType.get("IdentificationCode"));
        }

        Assert.assertTrue(lsIdSet.contains("id-code-1"));
        Assert.assertTrue(lsIdSet.contains("id-code-2"));

        Assert.assertEquals("nomen", m.get("nomenclature"));
    }
}
