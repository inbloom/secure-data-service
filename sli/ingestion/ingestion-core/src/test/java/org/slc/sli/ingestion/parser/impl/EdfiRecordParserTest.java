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
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.jdom2.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
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
public class EdfiRecordParserTest {

    XsdTypeProvider tp = new XsdTypeProvider();

    @Before
    public void setup() throws IOException, JDOMException {
        Resource[] schemaFiles = new PathMatchingResourcePatternResolver().getResources("classpath:edfiXsd-SLI/*.xsd");
        tp.setSchemaFiles(schemaFiles);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testParsing() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/Student.xml");

        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(xml.getInputStream());

        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordParserImpl.parse(reader, schema, tp, mockVisitor);

        verify(mockVisitor, atLeastOnce()).visit(any(RecordMeta.class), anyMap());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSourceLocation() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/ThirteenStudents.xml");

        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(xml.getInputStream());

        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordParserImpl.parse(reader, schema, tp, mockVisitor);

        ArgumentCaptor<RecordMeta> recordMetaCaptor = ArgumentCaptor.forClass(RecordMeta.class);
        verify(mockVisitor, times(13)).visit(recordMetaCaptor.capture(), any(Map.class));

        int recorCount = 0;
        for (RecordMeta recordMeta : recordMetaCaptor.getAllValues()) {
            recorCount++;

            if (recorCount == 11) {
                assertEquals(1584, recordMeta.getSourceStartLocation().getLineNumber());
                assertEquals(1741, recordMeta.getSourceEndLocation().getLineNumber());
            } else if (recorCount == 13) {
                assertEquals(1900, recordMeta.getSourceStartLocation().getLineNumber());
                assertEquals(2057, recordMeta.getSourceEndLocation().getLineNumber());
            }
        }
    }
}
