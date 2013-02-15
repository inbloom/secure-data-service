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
public class InterchangeStudentDisciplineTest {

    public static final Logger LOG = LoggerFactory.getLogger(InterchangeStudentDisciplineTest.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    Resource schemaDir = new ClassPathResource("edfiXsd-SLI");

    @Test
    public void testDisciplineIncident() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentDiscipline.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentDiscipline/DisciplineIncident.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentDiscipline/DisciplineIncident.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testStudentDisciplineIncidentAssociation() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentDiscipline.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentDiscipline/StudentDisciplineIncidentAssociation.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentDiscipline/StudentDisciplineIncidentAssociation.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testDisciplineAction() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentDiscipline.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentDiscipline/DisciplineAction.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentDiscipline/DisciplineAction.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

}
