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

package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junitx.util.PrivateAccessor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.handler.Handler;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.FileResource;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.JobSource;

/**
 * Unit Tests for ValidationController
 *
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class ValidationControllerTest {

    final String controlFileName = "test/MainControlFile.ctl";
    final String invalidControlFile = "test/MainControlFileCheckSumError.ctl";
    final String zipFileName = "zipFile/Session1.zip";
    @Autowired
    private ValidationController validationController;

    @Test
    public void testDoValidationCtlFile() throws NoSuchFieldException, IllegalAccessException, IOException {
        File ctlFile = Mockito.mock(File.class);
        File ctlDirectory = Mockito.mock(File.class);
        Mockito.when(ctlFile.getName()).thenReturn("Test.ctl");
        Mockito.when(ctlFile.isFile()).thenReturn(true);
        Mockito.when(ctlFile.getParentFile()).thenReturn(ctlDirectory);
        Mockito.when(ctlDirectory.getAbsoluteFile()).thenReturn(ctlDirectory);

        ValidationController vc = Mockito.spy(validationController);
        Mockito.doNothing().when(vc).processControlFile(Mockito.eq(ctlDirectory),
                Mockito.eq("Test.ctl"), Mockito.any(ReportStats.class));

        vc.doValidation(ctlFile);

        Mockito.verify(vc, Mockito.atLeastOnce()).processControlFile(Mockito.eq(ctlDirectory),
                Mockito.eq("Test.ctl"), Mockito.any(ReportStats.class));
    }

    @Test
    public void testDirectory() throws NoSuchFieldException, IllegalAccessException, IOException {
        File ctlFile = Mockito.mock(File.class);
        Mockito.when(ctlFile.getName()).thenReturn("Test");
        Mockito.when(ctlFile.isFile()).thenReturn(false);

        AbstractMessageReport messageReport = Mockito.mock(AbstractMessageReport.class);
        PrivateAccessor.setField(validationController, "messageReport", messageReport);

        validationController.doValidation(ctlFile);
        Mockito.verify(messageReport, Mockito.atLeastOnce()).error(Matchers.any(ReportStats.class),
                Matchers.any(Source.class), Matchers.eq(ValidationMessageCode.VALIDATION_0015));
    }

    @Test
    public void testDoValidationZipFile() throws NoSuchFieldException, IllegalAccessException, IOException {
        File zipFile = Mockito.mock(File.class);
        Mockito.when(zipFile.getName()).thenReturn("Test.zip");
        Mockito.when(zipFile.getAbsolutePath()).thenReturn("/Test.zip");
        Mockito.when(zipFile.isFile()).thenReturn(true);

        ValidationController vc = Mockito.spy(validationController);
        Mockito.doNothing().when(vc).processZip(Mockito.eq(zipFile), Mockito.any(ReportStats.class));

        vc.doValidation(zipFile);

        Mockito.verify(vc, Mockito.atLeastOnce()).processZip(Mockito.eq(zipFile), Mockito.any(ReportStats.class));
    }

    @Test
    public void testDoValidationInvalidFile() throws NoSuchFieldException, IllegalAccessException, IOException {
        AbstractMessageReport messageReport = Mockito.mock(AbstractMessageReport.class);
        PrivateAccessor.setField(validationController, "messageReport", messageReport);

        File invalidFile = Mockito.mock(File.class);
        Mockito.when(invalidFile.getName()).thenReturn("Test.txt");
        Mockito.when(invalidFile.isFile()).thenReturn(true);

        validationController.doValidation(invalidFile);
        Mockito.verify(messageReport, Mockito.atLeastOnce()).error(Matchers.any(ReportStats.class),
                Matchers.any(Source.class), Matchers.eq(ValidationMessageCode.VALIDATION_0001));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessZip() throws IOException, NoSuchFieldException, IllegalAccessException {
        Handler<org.slc.sli.ingestion.Resource, String> handler = Mockito.mock(Handler.class);

        ReportStats rs = Mockito.mock(ReportStats.class);
        Mockito.when(rs.hasErrors()).thenReturn(false);

        AbstractMessageReport messageReport = Mockito.mock(AbstractMessageReport.class);

        PrivateAccessor.setField(validationController, "messageReport", messageReport);

        File zipFile = (new ClassPathResource(zipFileName)).getFile();
        FileResource zipFileResource = new FileResource(zipFile.getAbsolutePath());

        ValidationController vc = Mockito.spy(validationController);

        Mockito.doNothing().when(vc).processControlFile(Mockito.any(FileResource.class),
                Mockito.any(String.class), Mockito.any(ReportStats.class));

        Mockito.doReturn(zipFile.getName()).when(handler).handle(zipFileResource, messageReport, rs);

        vc.setZipFileHandler(handler);
        vc.processZip(zipFileResource, rs);
        Mockito.verify(handler, Mockito.atLeastOnce()).handle(zipFileResource, messageReport, rs);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessInvalidZip() throws IOException, NoSuchFieldException, IllegalAccessException {

        Handler<org.slc.sli.ingestion.Resource, String> handler = Mockito.mock(Handler.class);

        ReportStats reportStats = Mockito.mock(ReportStats.class);
        Mockito.when(reportStats.hasErrors()).thenReturn(true);

        File zipFile = (new ClassPathResource(zipFileName)).getFile();
        FileResource zipFileResource = new FileResource(zipFile.getAbsolutePath());

        ValidationController vc = Mockito.spy(validationController);

        AbstractMessageReport messageReport = Mockito.mock(AbstractMessageReport.class);

        PrivateAccessor.setField(validationController, "messageReport", messageReport);

        Mockito.doReturn("Session1.ctl").when(handler).handle(zipFileResource, messageReport, reportStats);
        vc.processZip(zipFileResource, reportStats);
        Mockito.verify(vc, Mockito.never()).processControlFile(zipFileResource, "Session1.ctl", reportStats);
    }

    @Test
    public void testValidatorInValid() throws IOException, NoSuchFieldException, IllegalAccessException {
        Resource xmlResource = new ClassPathResource("emptyXml/InterchangeStudent.xml");
        File xmlFile = xmlResource.getFile();

        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION, xmlFile.getName(), "");

        List<IngestionFileEntry> fileEntries = new ArrayList<IngestionFileEntry>();
        fileEntries.add(ife);

        ControlFile cfl = Mockito.mock(ControlFile.class);
        Mockito.when(cfl.getFileEntries()).thenReturn(fileEntries);

        AbstractMessageReport messageReport = Mockito.mock(AbstractMessageReport.class);
        PrivateAccessor.setField(validationController, "messageReport", messageReport);

        ReportStats reportStats = Mockito.mock(ReportStats.class);
        Mockito.when(reportStats.hasErrors()).thenReturn(true);

        validationController.processValidators(cfl, reportStats);

        Mockito.verify(messageReport, Mockito.atLeastOnce()).info(Matchers.any(ReportStats.class),
                Matchers.any(Source.class), Matchers.eq(ValidationMessageCode.VALIDATION_0003), Matchers.eq(xmlFile.getName()));
    }

    @Test
    public void testValidatorValid() throws IOException, NoSuchFieldException, IllegalAccessException {
        Resource xmlResource = new ClassPathResource("test/InterchangeStudent.xml");
        File xmlFile = xmlResource.getFile();

        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION, xmlFile.getName(), "");

        List<IngestionFileEntry> fileEntries = new ArrayList<IngestionFileEntry>();
        fileEntries.add(ife);

        ControlFile cfl = Mockito.mock(ControlFile.class);
        Mockito.when(cfl.getFileEntries()).thenReturn(fileEntries);

        AbstractMessageReport messageReport = Mockito.mock(AbstractMessageReport.class);
        PrivateAccessor.setField(validationController, "messageReport", messageReport);

        ReportStats reportStats = Mockito.mock(ReportStats.class);
        Mockito.when(reportStats.hasErrors()).thenReturn(false);

        validationController.processValidators(cfl, reportStats);
        Mockito.verify(messageReport, Mockito.atLeastOnce()).info(Matchers.any(ReportStats.class),
                Matchers.any(Source.class), Matchers.eq(ValidationMessageCode.VALIDATION_0002), Matchers.eq(xmlFile.getName()));
    }

    @Test
    public void testValidProcessControlFile() throws IOException, NoSuchFieldException {
        Resource ctlFileResource = new ClassPathResource(controlFileName);
        File ctlFile = ctlFileResource.getFile();

        ValidationController vc = Mockito.spy(validationController);

        ReportStats reportStats = Mockito.mock(ReportStats.class);
        Mockito.when(reportStats.hasErrors()).thenReturn(false);

        AbstractMessageReport messageReport = Mockito.mock(AbstractMessageReport.class);

        PrivateAccessor.setField(validationController, "messageReport", messageReport);

        vc.processControlFile(ctlFile.getParentFile(), ctlFile.getName(), reportStats);

        Mockito.verify(vc).processValidators(Mockito.any(ControlFile.class),
                Mockito.any(ReportStats.class));
    }

    @Test
    public void testInvalidProcessControlFile() throws IOException, SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Resource ctlFileResource = new ClassPathResource(invalidControlFile);
        File ctlFile = ctlFileResource.getFile();

        AbstractMessageReport messageReport = Mockito.mock(AbstractMessageReport.class);
        PrivateAccessor.setField(validationController, "messageReport", messageReport);

        ReportStats reportStats = Mockito.mock(ReportStats.class);
        Mockito.when(reportStats.hasErrors()).thenReturn(false);

        validationController.processControlFile(ctlFile.getParentFile(), ctlFile.getName(), reportStats);

        Mockito.verify(messageReport).error(Matchers.any(ReportStats.class),
                Matchers.any(Source.class), Matchers.eq(ValidationMessageCode.VALIDATION_0010), Matchers.anyString());
    }

}
