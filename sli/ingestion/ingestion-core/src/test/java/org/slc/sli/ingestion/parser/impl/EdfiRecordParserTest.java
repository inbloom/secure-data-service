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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.RecordVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 *
 * @author dduran
 *
 */
public class EdfiRecordParserTest {

    public static final Logger LOG = LoggerFactory.getLogger(EdfiRecordParserTest.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SuppressWarnings("unchecked")
    public void testParsing() throws Throwable {
        Resource[] schemaFiles = new PathMatchingResourcePatternResolver().getResources("classpath:edfiXsd-SLI/*.xsd");

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/Student.xml");

        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(xml.getInputStream());

        RecordVisitor visitor = Mockito.mock(RecordVisitor.class);

        XsdTypeProvider tp = new XsdTypeProvider();

        tp.setSchemaFiles(schemaFiles);

        EdfiRecordParserImpl.parse(reader, schema, tp, visitor);

        verify(visitor, atLeastOnce()).visit(any(RecordMeta.class), anyMap());
    }

    @Test
    public void testStudentParsing() throws Throwable {
        Resource[] schemaFiles = new PathMatchingResourcePatternResolver().getResources("classpath:edfiXsd-SLI/*.xsd");

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/Student.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentParent/Student.expected.json");

        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(xml.getInputStream());

        RecordVisitor visitor = new TestingRecordVisitor(expectedJson, objectMapper);

        XsdTypeProvider tp = new XsdTypeProvider();
        tp.setSchemaFiles(schemaFiles);

        EdfiRecordParserImpl.parse(reader, schema, tp, visitor);
    }

}
