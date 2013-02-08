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

package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.handler.SmooksFileHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.util.MD5;

/**
 * Unit Tests for EdFiProcessor
 *
 * @author tshewchuk
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class EdFiProcessorTest {

    @Autowired
    @InjectMocks
    EdFiProcessor edFiProcessor;

    @Mock
    SmooksFileHandler smooksFileHandler;

    @Mock
    private BatchJobDAO mockedBatchJobDAO;

    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";
    private static final String ERROR_MESSAGE = "ErrorMessage";

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidXMLFile() throws Exception {

        String batchJobId = NewBatchJob.createId("validXML.zip");
        Exchange preObject = new DefaultExchange(new DefaultCamelContext());
        preObject.getIn().setHeader("BatchJobId", batchJobId);

        // Get Input File.
        File zipFile = IngestionTest.getFile("fileLevelTestData/validXML/validXML.zip");
        File inputFile = new File("student.xml");

        NewBatchJob mockedJob = new NewBatchJob(batchJobId, "SLI");
        mockedJob.setTenantId("MyTenant");
        mockedJob.setBatchProperties(new HashMap<String, String>());
        mockedJob.setSourceId(zipFile.getParentFile().getCanonicalPath() + File.separator);
        createResourceEntryAndAddToJob(zipFile, FileFormat.ZIP_FILE.getCode(), mockedJob);
        createResourceEntryAndAddToJob(inputFile, FileFormat.EDFI_XML.getCode(), mockedJob);
        Mockito.when(mockedBatchJobDAO.findBatchJobById(Matchers.eq(batchJobId))).thenReturn(mockedJob);

        // Create Ingestion File Entry.
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(zipFile.getAbsolutePath(), FileFormat.EDFI_XML,
                FileType.XML_STUDENT_PARENT_ASSOCIATION, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setBatchJobId(batchJobId);
        FileEntryWorkNote fileEntryWorkNote = new FileEntryWorkNote(batchJobId, inputFileEntry, false);
        Mockito.when(
                smooksFileHandler.handle(Matchers.eq(inputFileEntry), Matchers.any(AbstractMessageReport.class),
                        Matchers.any(SimpleReportStats.class))).thenReturn(new FileProcessStatus());

        preObject.getIn().setBody(fileEntryWorkNote);

        Exchange eObject = new DefaultExchange(new DefaultCamelContext());

        eObject.getIn().setHeaders(preObject.getIn().getHeaders());
        eObject.getIn().setBody(preObject.getIn().getBody());

        edFiProcessor.setSmooksFileHandler(smooksFileHandler);

        edFiProcessor.process(eObject);

        assertNull("Should not return with errors", eObject.getIn().getHeader("hasErrors"));
        assertEquals("Mismatched message type", MessageType.DATA_TRANSFORMATION.name(),
                eObject.getIn().getHeader(INGESTION_MESSAGE_TYPE));
        assertNull("Should be no error message", eObject.getIn().getHeader(ERROR_MESSAGE));
    }

    @Test
    public void testXMLFileNotInJobResources() throws Exception {

        String batchJobId = NewBatchJob.createId("validXML.zip");
        Exchange preObject = new DefaultExchange(new DefaultCamelContext());
        preObject.getIn().setHeader("BatchJobId", batchJobId);

        // Get Input File.
        File zipFile = IngestionTest.getFile("fileLevelTestData/validXML/validXML.zip");
        File inputFile = new File("student.xml");
        File noSuchFile = new File("noSuchFile.xml");

        NewBatchJob mockedJob = new NewBatchJob(batchJobId, "SLI");
        mockedJob.setSourceId(batchJobId);
        createResourceEntryAndAddToJob(zipFile, FileFormat.ZIP_FILE.getCode(), mockedJob);
        createResourceEntryAndAddToJob(inputFile, FileFormat.EDFI_XML.getCode(), mockedJob);
        mockedJob.setSourceId(zipFile.getParentFile().getCanonicalPath() + File.separator);
        Mockito.when(mockedBatchJobDAO.findBatchJobById(Matchers.eq(batchJobId))).thenReturn(mockedJob);

        // Create Ingestion File Entry.
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(zipFile.getAbsolutePath(), FileFormat.EDFI_XML,
                FileType.XML_STUDENT_PARENT_ASSOCIATION, noSuchFile.getName(), MD5.calculate(noSuchFile));
        inputFileEntry.setBatchJobId(batchJobId);
        FileEntryWorkNote fileEntryWorkNote = new FileEntryWorkNote(batchJobId, inputFileEntry, false);

        Mockito.when(
                smooksFileHandler.handle(Matchers.eq(inputFileEntry), Matchers.any(AbstractMessageReport.class),
                        Matchers.any(SimpleReportStats.class))).thenReturn(new FileProcessStatus());

        preObject.getIn().setBody(fileEntryWorkNote);

        Exchange eObject = new DefaultExchange(new DefaultCamelContext());

        eObject.getIn().setHeaders(preObject.getIn().getHeaders());
        eObject.getIn().setBody(preObject.getIn().getBody());

        edFiProcessor.setSmooksFileHandler(smooksFileHandler);

        edFiProcessor.process(eObject);

        assertNull("Should not return with errors", eObject.getIn().getHeader("hasErrors"));
        assertEquals("Mismatched message type", MessageType.ERROR.name(),
                eObject.getIn().getHeader(INGESTION_MESSAGE_TYPE));
    }

    @Test
    public void testNoSuchBatchJobId() throws Exception {

        Exchange preObject = new DefaultExchange(new DefaultCamelContext());

        // Get Input File.
        File zipFile = IngestionTest.getFile("fileLevelTestData/validXML/validXML.zip");
        File inputFile = new File("student.xml");

        String batchJobId = NewBatchJob.createId("validXML.zip");
        NewBatchJob mockedJob = new NewBatchJob(batchJobId, "SLI");
        mockedJob.setSourceId(batchJobId);
        createResourceEntryAndAddToJob(zipFile, FileFormat.ZIP_FILE.getCode(), mockedJob);
        createResourceEntryAndAddToJob(inputFile, FileFormat.EDFI_XML.getCode(), mockedJob);
        mockedJob.setSourceId(zipFile.getParentFile().getCanonicalPath() + File.separator);
        Mockito.when(mockedBatchJobDAO.findBatchJobById(Matchers.eq(batchJobId))).thenReturn(mockedJob);

        // Create Ingestion File Entry.
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(zipFile.getAbsolutePath(), FileFormat.EDFI_XML,
                FileType.XML_STUDENT_PARENT_ASSOCIATION, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setBatchJobId(batchJobId);
        Mockito.when(
                smooksFileHandler.handle(Matchers.eq(inputFileEntry), Matchers.any(AbstractMessageReport.class),
                        Matchers.any(SimpleReportStats.class))).thenReturn(new FileProcessStatus());

        preObject.getIn().setBody(inputFileEntry);

        Exchange eObject = new DefaultExchange(new DefaultCamelContext());

        eObject.getIn().setHeaders(preObject.getIn().getHeaders());
        eObject.getIn().setBody(preObject.getIn().getBody());

        edFiProcessor.setSmooksFileHandler(smooksFileHandler);

        edFiProcessor.process(eObject);

        assertNull("Should not return with errors", eObject.getIn().getHeader("hasErrors"));
        assertEquals("Mismatched message type", MessageType.ERROR.name(),
                eObject.getIn().getHeader(INGESTION_MESSAGE_TYPE));
    }

    private void createResourceEntryAndAddToJob(File resourceFile, String format, NewBatchJob newJob)
            throws IOException {
        ResourceEntry resourceName = new ResourceEntry();
        resourceName.setResourceName(resourceFile.getCanonicalPath());
        resourceName.setResourceId(resourceFile.getName());
        resourceName.setExternallyUploadedResourceId(resourceFile.getName());
        resourceName.setResourceFormat(format);
        newJob.getResourceEntries().add(resourceName);
    }

}
