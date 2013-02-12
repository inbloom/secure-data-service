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
package org.slc.sli.ingestion.parser.impl;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * 
 * @author slee
 *
 */
public class InterchangeAssessmentMetadataTest {

    public static final Logger LOG = LoggerFactory.getLogger(InterchangeAssessmentMetadataTest.class);

    @Test
    public void testAssessment() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-AssessmentMetadata.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeAssessmentMetadata/Assessment.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeAssessmentMetadata/Assessment.expected.json");

        new EntityTestHelper().parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testAssessmentFamily() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-AssessmentMetadata.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeAssessmentMetadata/AssessmentFamily.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeAssessmentMetadata/AssessmentFamily.expected.json");

        new EntityTestHelper().parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testAssessmentItem() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-AssessmentMetadata.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeAssessmentMetadata/AssessmentItem.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeAssessmentMetadata/AssessmentItem.expected.json");

        new EntityTestHelper().parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testAssessmentPeriodDescriptor() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-AssessmentMetadata.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeAssessmentMetadata/AssessmentPeriodDescriptor.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeAssessmentMetadata/AssessmentPeriodDescriptor.expected.json");

        new EntityTestHelper().parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testLearningObjective() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-AssessmentMetadata.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeAssessmentMetadata/LearningObjective.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeAssessmentMetadata/LearningObjective.expected.json");

        new EntityTestHelper().parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testLearningStandard() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-AssessmentMetadata.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeAssessmentMetadata/LearningStandard.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeAssessmentMetadata/LearningStandard.expected.json");

        new EntityTestHelper().parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testObjectiveAssessment() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-AssessmentMetadata.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeAssessmentMetadata/ObjectiveAssessment.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeAssessmentMetadata/ObjectiveAssessment.expected.json");

        new EntityTestHelper().parseAndVerify(schema, inputXml, expectedJson);
    }

}
