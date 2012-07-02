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


package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.xml.idref.IdRefResolutionHandler;

/**
 * A unit test for XMlFileProcessor
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class XmlFileProcessorTest {

    @Autowired
    XmlFileProcessor xmlFileProcessor;

    @Test
    public void testProcessValidXML() throws Exception {
        Exchange preObject = new DefaultExchange(new DefaultCamelContext());

        IngestionFileEntry entry = Mockito.mock(IngestionFileEntry.class);
        NewBatchJob job = Mockito.mock(NewBatchJob.class);
        job.addFile(entry);

        BatchJobDAO dao = Mockito.mock(BatchJobDAO.class);
        WorkNote workNote = WorkNote.createSimpleWorkNote(job.getId());
        preObject.getIn().setBody(workNote);

        IdRefResolutionHandler handler = Mockito.mock(IdRefResolutionHandler.class);
        FaultsReport faults = Mockito.mock(FaultsReport.class);

        Mockito.when(dao.findBatchJobById(Mockito.anyString())).thenReturn(job);
        Mockito.when(handler.handle(Mockito.any(IngestionFileEntry.class), Mockito.any(ErrorReport.class))).thenReturn(
                entry);
        Mockito.when(entry.getFaultsReport()).thenReturn(faults);
        Mockito.when(faults.hasErrors()).thenReturn(true);
        xmlFileProcessor.setIdRefResolutionHandler(handler);
        xmlFileProcessor.setBatchJobDAO(dao);
        xmlFileProcessor.process(preObject);
        Assert.assertEquals(workNote, preObject.getIn().getBody(WorkNote.class));
        Assert.assertEquals(false, preObject.getIn().getHeader("hasErrors"));
        Assert.assertEquals(MessageType.XML_FILE_PROCESSED.name(), preObject.getIn().getHeader("IngestionMessageType"));
    }
}
