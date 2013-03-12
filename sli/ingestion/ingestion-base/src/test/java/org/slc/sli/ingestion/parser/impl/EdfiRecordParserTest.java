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

import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.slc.sli.ingestion.parser.XmlParseException;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.MessageCode;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.JobSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

/**
 * Tests the EdfiRecordParser
 * @author ablum
 *
 */
public class EdfiRecordParserTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testParsing() throws Throwable {
        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/Student.xml");
        AbstractMessageReport mr = Mockito.mock(AbstractMessageReport.class);

        EdfiRecordParser.parse(xml.getInputStream(), schema, mr, new SimpleReportStats(), new JobSource(xml.getFilename()));

        verify(mr, Mockito.never()).error(Mockito.any(ReportStats.class), Mockito.any(Source.class), Mockito.any(MessageCode.class), Mockito.any());
        verify(mr, Mockito.never()).warning(Mockito.any(ReportStats.class), Mockito.any(Source.class), Mockito.any(MessageCode.class), Mockito.any());
    }

    @Test(expected = IOException.class)
    public void testIOExceptionForSchema() throws Throwable {
        Resource schema = new ClassPathResource("does_not_exists.xsd");
        Resource xml = new ClassPathResource("parser/InterchangeStudentParent/Student.xml");
        AbstractMessageReport mr = Mockito.mock(AbstractMessageReport.class);

        EdfiRecordParser.parse(xml.getInputStream(), schema, mr, new SimpleReportStats(), new JobSource(xml.getFilename()));

    }

    @Test
    public void testRejectIfExpectedElementMissing() throws Throwable {
        rejectRecord("parser/InterchangeStudentParent/StudentMissingName.xml",
                "edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
    }

    @Test
    public void testRejectIfExtraElementIsPresent() throws Throwable {
        rejectRecord("parser/InterchangeStudentParent/StudentHasExtraElement.xml",
                "edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
    }

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
        AbstractMessageReport mr = Mockito.mock(AbstractMessageReport.class);

        EdfiRecordParser.parse(xml.getInputStream(), schema, mr, new SimpleReportStats(), new JobSource(xml.getFilename()));

        verify(mr, Mockito.atLeast(1)).error(Mockito.any(ReportStats.class), Mockito.any(Source.class), Mockito.any(MessageCode.class), Mockito.any());
    }


}
