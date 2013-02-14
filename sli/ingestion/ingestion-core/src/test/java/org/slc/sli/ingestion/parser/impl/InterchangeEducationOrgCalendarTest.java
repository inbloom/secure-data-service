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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.RecordVisitor;

/**
 *
 * @author dduran
 *
 */
public class InterchangeEducationOrgCalendarTest {

    public static final Logger LOG = LoggerFactory.getLogger(InterchangeEducationOrgCalendarTest.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    Resource schemaDir = new ClassPathResource("edfiXsd-SLI");

    @Test
    public void testGradingPeriod() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-EducationOrgCalendar.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeEducationOrgCalendar/GradingPeriod.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeEducationOrgCalendar/GradingPeriod.expected.json");

        entityTestHelper(schema, inputXml, expectedJson);
    }

    @Test
    public void testSession() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-EducationOrgCalendar.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeEducationOrgCalendar/Session.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeEducationOrgCalendar/Session.expected.json");

        entityTestHelper(schema, inputXml, expectedJson);
    }

    @Test
    public void testCalendarDate() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-EducationOrgCalendar.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeEducationOrgCalendar/CalendarDate.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeEducationOrgCalendar/CalendarDate.expected.json");

        entityTestHelper(schema, inputXml, expectedJson);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void entityTestHelper(Resource schema, Resource inputXmlResource, Resource expectedJsonResource)
            throws Throwable {

        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(inputXmlResource.getInputStream());

        XsdTypeProvider tp = new XsdTypeProvider();
        tp.setSchemaFiles(new PathMatchingResourcePatternResolver().getResources("classpath:edfiXsd-SLI/*.xsd"));

        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordParserImpl.parse(reader, schema, tp, mockVisitor);

        ArgumentCaptor<Map> mapArgCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockVisitor, atLeastOnce()).visit(any(RecordMeta.class), mapArgCaptor.capture());
        LOG.info("Visitor invoked {} times.", mapArgCaptor.getAllValues().size());

        LOG.info(objectMapper.writeValueAsString(mapArgCaptor.getValue()));
        Object expected = objectMapper.readValue(expectedJsonResource.getFile(), Map.class);
        assertEquals(expected, mapArgCaptor.getValue());
    }

}
