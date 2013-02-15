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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author tshewchuk
 *
 */
public class InterchangeStudentCohortTest {

    public static final Logger LOG = LoggerFactory.getLogger(InterchangeStudentCohortTest.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    Resource schemaDir = new ClassPathResource("edfiXsd-SLI");

    @Test
    public void testCohort() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentCohort.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentCohort/Cohort.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentCohort/Cohort.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testStudentCohortAssociation() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentCohort.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentCohort/StudentCohortAssociation.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentCohort/StudentCohortAssociation.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testStaffCohortAssociation() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentCohort.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentCohort/StaffCohortAssociation.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentCohort/StaffCohortAssociation.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

}
