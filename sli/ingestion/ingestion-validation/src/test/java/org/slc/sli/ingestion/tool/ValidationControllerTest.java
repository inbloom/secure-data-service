/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
import java.lang.reflect.Field;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.handler.ZipFileHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;

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
        Mockito.when(ctlFile.getName()).thenReturn("Test.ctl");
        Mockito.when(ctlFile.isFile()).thenReturn(true);

        ValidationController vc = Mockito.spy(validationController);
        Mockito.doNothing().when(vc).processControlFile(ctlFile);
        vc.doValidation(ctlFile);
        Mockito.verify(vc, Mockito.atLeastOnce()).processControlFile(ctlFile);

    }

    @Test
    public void testDirectory() throws NoSuchFieldException, IllegalAccessException, IOException {
        File ctlFile = Mockito.mock(File.class);
        Mockito.when(ctlFile.getName()).thenReturn("Test");
        Mockito.when(ctlFile.isFile()).thenReturn(false);

        Logger log = Mockito.mock(Logger.class);
        Field logField = validationController.getClass().getDeclaredField("logger");
        logField.setAccessible(true);
        logField.set(null, log);

        validationController.doValidation(ctlFile);
        Mockito.verify(log, Mockito.atLeastOnce()).error("Invalid input: No clt/zip file found");
    }

    @Test
    public void testDoValidationZipFile() throws NoSuchFieldException, IllegalAccessException, IOException {
        File zipFile = Mockito.mock(File.class);
        Mockito.when(zipFile.getName()).thenReturn("Test.zip");
        Mockito.when(zipFile.isFile()).thenReturn(true);

        ValidationController vc = Mockito.spy(validationController);
        Mockito.doNothing().when(vc).processZip(zipFile);
        vc.doValidation(zipFile);
        Mockito.verify(vc, Mockito.atLeastOnce()).processZip(zipFile);

    }

    @Test
    public void testDoValidationInvalidFile() throws NoSuchFieldException, IllegalAccessException, IOException {
        Logger log = Mockito.mock(Logger.class);
        Field logField = validationController.getClass().getDeclaredField("logger");
        logField.setAccessible(true);
        logField.set(null, log);

        File invalidFile = Mockito.mock(File.class);
        Mockito.when(invalidFile.getName()).thenReturn("Test.txt");
        Mockito.when(invalidFile.isFile()).thenReturn(true);

        validationController.doValidation(invalidFile);
        Mockito.verify(log, Mockito.atLeastOnce()).error("Invalid input: No clt/zip file found");
    }

    @Test
    public void testProcessZip() throws IOException, NoSuchFieldException, IllegalAccessException {
        ZipFileHandler handler = Mockito.mock(ZipFileHandler.class);

        ErrorReport er = Mockito.mock(ErrorReport.class);
        Field errorReport = validationController.getClass().getDeclaredField("errorReport");
        errorReport.setAccessible(true);
        errorReport.set(null, er);

        Resource zipFileResource = new ClassPathResource(zipFileName);
        File zipFile = zipFileResource.getFile();

        ValidationController vc = Mockito.spy(validationController);
        Mockito.doNothing().when(vc).processControlFile((File) Mockito.any());
        Mockito.doReturn(zipFile).when(handler).handle(zipFile, er);

        vc.setZipFileHandler(handler);
        vc.processZip(zipFile);
        Mockito.verify(handler, Mockito.atLeastOnce()).handle(zipFile, er);
    }

    @Test
    public void testProcessInvalidZip() throws IOException, NoSuchFieldException, IllegalAccessException {

        ZipFileHandler handler = Mockito.mock(ZipFileHandler.class);
        Field zipField = validationController.getClass().getDeclaredField("zipFileHandler");
        zipField.setAccessible(true);
        zipField.set(validationController, handler);

        Resource zipFileResource = new ClassPathResource(zipFileName);
        File zipFile = zipFileResource.getFile();

        ValidationController vc = Mockito.spy(validationController);

        ErrorReport er = Mockito.mock(ErrorReport.class);
        Field errorReport = validationController.getClass().getDeclaredField("errorReport");
        errorReport.setAccessible(true);
        errorReport.set(null, er);

        Mockito.when(er.hasErrors()).thenReturn(true);
        Mockito.doReturn(zipFile).when(handler).handle(zipFile, er);
        vc.processZip(zipFile);
        Mockito.verify(vc, Mockito.never()).processControlFile(zipFile);
    }

    @Test
    public void testValidatorInValid() throws IOException, NoSuchFieldException, IllegalAccessException {
        Resource xmlResource = new ClassPathResource("emptyXml/InterchangeStudent.xml");
        File xmlFile = xmlResource.getFile();

        IngestionFileEntry ife = Mockito.mock(IngestionFileEntry.class);
        Mockito.when(ife.getFileName()).thenReturn("InterchangeStudent.xml");
        Mockito.when(ife.getFile()).thenReturn(xmlFile);
        Mockito.when(ife.getFileType()).thenReturn(FileType.XML_STUDENT_PARENT_ASSOCIATION);

        Job job = BatchJob.createDefault("testJob");
        job.addFile(ife);

        Logger log = Mockito.mock(Logger.class);
        Field logField = validationController.getClass().getDeclaredField("logger");
        logField.setAccessible(true);
        logField.set(null, log);

        validationController.processValidators(job);
        Mockito.verify(log, Mockito.atLeastOnce())
                .info("Processing of file: {} resulted in errors.", ife.getFileName());
    }

    @Test
    public void testValidatorValid() throws IOException, NoSuchFieldException, IllegalAccessException {
        Resource xmlResource = new ClassPathResource("test/InterchangeStudent.xml");
        File xmlFile = xmlResource.getFile();

        IngestionFileEntry ife = Mockito.mock(IngestionFileEntry.class);
        Mockito.when(ife.getFileName()).thenReturn("InterchangeStudent.xml");
        Mockito.when(ife.getFile()).thenReturn(xmlFile);
        Mockito.when(ife.getFileType()).thenReturn(FileType.XML_STUDENT_PARENT_ASSOCIATION);
        Job job = BatchJob.createDefault("testJob");
        job.addFile(ife);

        Logger log = Mockito.mock(Logger.class);
        Field logField = validationController.getClass().getDeclaredField("logger");
        logField.setAccessible(true);
        logField.set(null, log);

        validationController.processValidators(job);
        Mockito.verify(log, Mockito.atLeastOnce()).info("Processing of file: {} completed.", ife.getFileName());
    }

    @Test
    public void testValidProcessControlFile() throws IOException {
        Resource ctlFileResource = new ClassPathResource(controlFileName);
        File ctlFile = ctlFileResource.getFile();

        ValidationController vc = Mockito.spy(validationController);

        vc.processControlFile(ctlFile);

        Mockito.verify(vc).processValidators(Mockito.any(BatchJob.class));
    }

    @Test
    public void testInvalidProcessControlFile() throws IOException, SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Resource ctlFileResource = new ClassPathResource(invalidControlFile);
        File ctlFile = ctlFileResource.getFile();

        Logger log = Mockito.mock(Logger.class);
        Field logField = validationController.getClass().getDeclaredField("logger");
        logField.setAccessible(true);
        logField.set(null, log);

        validationController.processControlFile(ctlFile);

        Mockito.verify(log).error(Mockito.anyString());
    }

}