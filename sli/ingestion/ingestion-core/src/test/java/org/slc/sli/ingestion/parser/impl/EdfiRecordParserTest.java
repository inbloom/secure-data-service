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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Map;

import org.jdom2.JDOMException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.RecordVisitor;
import org.slc.sli.ingestion.parser.XmlParseException;

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

        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordParserImpl2.parse(xml.getInputStream(), schema, tp, mockVisitor);

        verify(mockVisitor, atLeastOnce()).visit(any(RecordMeta.class), anyMap());
    }

    @Test(expected = IOException.class)
    public void testIOExceptionForSchema() throws Throwable {
        Resource schema = new ClassPathResource("does_not_exists.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/Student.xml");

        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordParserImpl2.parse(xml.getInputStream(), schema, tp, mockVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSourceLocation() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/ThirteenStudents.xml");

        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordParserImpl2.parse(xml.getInputStream(), schema, tp, mockVisitor);

        ArgumentCaptor<RecordMeta> recordMetaCaptor = ArgumentCaptor.forClass(RecordMeta.class);
        verify(mockVisitor, times(13)).visit(recordMetaCaptor.capture(), any(Map.class));

        int recordCount = 0;
        for (RecordMeta recordMeta : recordMetaCaptor.getAllValues()) {
            recordCount++;

            if (recordCount == 11) {
                assertEquals(1574, recordMeta.getSourceStartLocation().getLineNumber());
                assertEquals(1730, recordMeta.getSourceEndLocation().getLineNumber());
            } else if (recordCount == 13) {
                assertEquals(1888, recordMeta.getSourceStartLocation().getLineNumber());
                assertEquals(2044, recordMeta.getSourceEndLocation().getLineNumber());
            }
        }
    }

    // TODO: Reinstate this test once strict validation is re-enabled!
    @Ignore
    @Test
    public void testRejectIfExpectedElementMissing() throws Throwable {
        rejectRecord("parser/InterchangeStudentParent/StudentMissingName.xml",
                "edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
    }

    // TODO: Reinstate this test once strict validation is re-enabled!
    @Ignore
    @Test
    public void testRejectIfExtraElementIsPresent() throws Throwable {
        rejectRecord("parser/InterchangeStudentParent/StudentHasExtraElement.xml",
                "edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
    }

    // TODO: Reinstate this test once strict validation is re-enabled!
    @Ignore
    @Test
    public void testRejectIfInvalidElementType() throws Throwable {
        rejectRecord("parser/InterchangeStudentParent/StudentHasInvalidTypeForDOB.xml",
                "edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
    }

    @Test(expected = XmlParseException.class)
    public void testRejectIfTagIsNotClosed() throws Throwable {
            rejectRecord("parser/InterchangeStudentParent/StudentHasOneTagNotClosed.xml",
                    "edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
    }

    @Test(expected = XmlParseException.class)
    public void testRejectIfTagIsNotClosed2() throws Throwable {
        rejectRecord("parser/InterchangeStudentParent/StudentHasOneTagNotClosed2.xml",
                "edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
    }

    private void rejectRecord(String xmlClassPath, String schemaClassPath) throws Throwable {
        Resource schema = new ClassPathResource(schemaClassPath);
        Resource xml = new ClassPathResource(xmlClassPath);

        rejectRecord(xml, schema);
    }

    @SuppressWarnings("unchecked")
    private void rejectRecord(Resource xml, Resource schema) throws Throwable {
        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordParserImpl2.parse(xml.getInputStream(), schema, tp, mockVisitor);

        verify(mockVisitor, never()).visit(any(RecordMeta.class), anyMap());
    }

    @Test
    public void testNewLineCharacter() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/StudentWithNewLine.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentParent/Student.expected.json");

        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordParserImpl2.parse(xml.getInputStream(), schema, tp, mockVisitor);

        EntityTestHelper.captureAndCompare(mockVisitor, expectedJson);
    }

    @Test
    public void testUnicodeCharacter() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/StudentWithUnicode.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentParent/StudentWithUnicode.expected.json");

        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordParserImpl2.parse(xml.getInputStream(), schema, tp, mockVisitor);

        EntityTestHelper.captureAndCompare(mockVisitor, expectedJson);
    }

    @Test
    public void testCDATA() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/StudentWithCDATA.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentParent/StudentWithCDATA.expected.json");

        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordParserImpl2.parse(xml.getInputStream(), schema, tp, mockVisitor);

        EntityTestHelper.captureAndCompare(mockVisitor, expectedJson);
    }
}
