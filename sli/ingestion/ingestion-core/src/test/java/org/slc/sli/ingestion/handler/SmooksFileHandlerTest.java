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

package org.slc.sli.ingestion.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import junitx.util.PrivateAccessor;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.smooks.SliSmooks;
import org.slc.sli.ingestion.smooks.SliSmooksFactory;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;
import org.slc.sli.ingestion.util.MD5;

/**
 * tests for SmooksFileHandler
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SmooksFileHandlerTest {

    @Autowired
    SmooksFileHandler smooksFileHandler;

    ReportStats reportStats = new SimpleReportStats();

    /*
     * XML TESTS
     */

    @Test
    @Ignore
    // we have removed the type information from the smooks mappings for this sprint.
    // TODO: remove @Ignore when the smooks mappings once again contain type information
    public void valueTypeNotMatchAttributeType() throws IOException, SAXException {
        // Get Input File
        File inputFile = IngestionTest
                .getFile("fileLevelTestData/invalidXML/valueTypeNotMatchAttributeType/student.xml");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(inputFile.getParentFile().getAbsolutePath(), FileFormat.EDFI_XML,
                FileType.XML_STUDENT_PARENT_ASSOCIATION, inputFile.getName(), MD5.calculate(inputFile));

        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        smooksFileHandler.handle(inputFileEntry, errorReport, reportStats);

        assertTrue("Value type mismatch should give error.", reportStats.hasErrors());
    }

    @Test
    public void malformedXML() throws IOException, SAXException {
        // Get Input File
        File inputFile = IngestionTest.getFile("fileLevelTestData/invalidXML/malformedXML/malformedXML.zip");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(inputFile.getAbsolutePath(), FileFormat.EDFI_XML,
                FileType.XML_STUDENT_PARENT_ASSOCIATION, "student.xml", MD5.calculate(inputFile));
        inputFileEntry.setBatchJobId("111111111-222222222-333333333-444444444-555555555-6");

        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        smooksFileHandler.handle(inputFileEntry, errorReport, reportStats);

        assertTrue("Malformed XML should give error.", reportStats.hasErrors());
    }

    @Test
    public void validXml() throws IOException, SAXException {
        // Get Input File
        File inputFile = IngestionTest.getFile("fileLevelTestData/validXML/validXML.zip");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(inputFile.getAbsolutePath(), FileFormat.EDFI_XML,
                FileType.XML_STUDENT_PARENT_ASSOCIATION, "student.xml", MD5.calculate(inputFile));

        inputFileEntry.setBatchJobId("111111111-222222222-333333333-444444444-555555555-6");

        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        smooksFileHandler.handle(inputFileEntry, errorReport, reportStats);

        assertFalse("Valid XML should give no errors.", reportStats.hasErrors());
    }

    @Test
    public void testXsdPreValidation() throws IOException, SAXException, NoSuchFieldException {
        SliSmooks smooks = Mockito.mock(SliSmooks.class);
        SliSmooksFactory factory = Mockito.mock(SliSmooksFactory.class);
        Mockito.when(
                factory.createInstance(Mockito.any(IngestionFileEntry.class), Mockito.any(AbstractMessageReport.class),
                        Mockito.any(ReportStats.class))).thenReturn(smooks);
        PrivateAccessor.setField(smooksFileHandler, "sliSmooksFactory", factory);

        File zipFile = IngestionTest.getFile("XsdValidation/InterchangeStudent-Valid.zip");
        IngestionFileEntry ife = new IngestionFileEntry(zipFile.getAbsolutePath(), FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION,
                "InterchangeStudent-Valid.xml", "");

        AbstractMessageReport errorReport = Mockito.mock(AbstractMessageReport.class);

        SmooksEdFiVisitor visitor = SmooksEdFiVisitor.createInstance("", "", errorReport, reportStats, ife);
        Mockito.when(smooks.getSmooksEdFiVisitor()).thenReturn(visitor);

        smooksFileHandler.handle(ife, errorReport, reportStats);
        Mockito.verify(errorReport, Mockito.never()).error(Mockito.any(ReportStats.class),
                Matchers.any(Source.class), Mockito.any(CoreMessageCode.class));
    }
}
