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

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import org.slc.sli.ingestion.parser.RecordVisitor;

/**
 *
 * @author tshewchuk
 *
 */
public class InterchangeEducationOrganizationTest {

    public static final Logger LOG = LoggerFactory.getLogger(InterchangeEducationOrganizationTest.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    Resource schemaDir = new ClassPathResource("edfiXsd-SLI");

    @Test
    public void testCompetencyLevelDescriptor() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-EducationOrganization.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeEducationOrganization/CompetencyLevelDescriptor.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeEducationOrganization/CompetencyLevelDescriptor.json");

        entityTestHelper(schema, inputXml, expectedJson);
    }

    @Test
    public void testCourse() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-EducationOrganization.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeEducationOrganization/Course.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeEducationOrganization/Course.json");

        entityTestHelper(schema, inputXml, expectedJson);
    }

    @Test
    public void testLocalEducationAgency() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-EducationOrganization.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeEducationOrganization/LocalEducationAgency.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeEducationOrganization/LocalEducationAgency.json");

        entityTestHelper(schema, inputXml, expectedJson);
    }

    @Test
    public void testProgram() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-EducationOrganization.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeEducationOrganization/Program.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeEducationOrganization/Program.json");

        entityTestHelper(schema, inputXml, expectedJson);
    }

    @Test
    public void testSchool() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-EducationOrganization.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeEducationOrganization/School.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeEducationOrganization/School.json");

        entityTestHelper(schema, inputXml, expectedJson);
    }

    @Test
    public void testStateEducationAgency() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-EducationOrganization.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeEducationOrganization/StateEducationAgency.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeEducationOrganization/StatelEducationAgency.json");

        entityTestHelper(schema, inputXml, expectedJson);
    }

    private void entityTestHelper(Resource schema, Resource inputXmlResource, Resource expectedJsonResource)
            throws Throwable {

        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(inputXmlResource.getInputStream());

        XsdTypeProvider tp = new XsdTypeProvider();
        tp.setSchemaFiles(new PathMatchingResourcePatternResolver().getResources("classpath:edfiXsd-SLI/*.xsd"));

        RecordVisitor visitor = new TestingRecordVisitor(expectedJsonResource, objectMapper);
        EdfiRecordParserImpl.parse(reader, schema, tp, visitor);

    }

}
