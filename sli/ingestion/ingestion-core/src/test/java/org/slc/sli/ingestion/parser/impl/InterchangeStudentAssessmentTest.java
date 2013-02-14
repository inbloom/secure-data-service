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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author tshewchuk
 *
 */
public class InterchangeStudentAssessmentTest {

    @Test
    public void testStudentAssessment() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentAssessment.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentAssessment/StudentAssessment.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentAssessment/StudentAssessment.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testStudentObjectiveAssessment() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentAssessment.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentAssessment/StudentObjectiveAssessment.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentAssessment/StudentObjectiveAssessment.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testStudentAssessmentItem() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentAssessment.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentAssessment/StudentAssessmentItem.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentAssessment/StudentAssessmentItem.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

}
