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

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.handler.ReferenceResolutionHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * A unit test for XMlFileProcessor
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
        BatchJob job = BatchJob.createDefault();
        job.addFile(entry);
        preObject.getIn().setBody(job);
        ReferenceResolutionHandler handler = Mockito.mock(ReferenceResolutionHandler.class);
                FaultsReport faults = Mockito.mock(FaultsReport.class);

        Mockito.when(handler.handle(Mockito.any(IngestionFileEntry.class), Mockito.any(ErrorReport.class))).thenReturn(entry);
        Mockito.when(entry.getFaultsReport()).thenReturn(faults);
        Mockito.when(faults.hasErrors()).thenReturn(true);
        xmlFileProcessor.setReferenceResolutionHandler(handler);

        xmlFileProcessor.process(preObject);
        Assert.assertEquals(job, preObject.getIn().getBody(BatchJob.class));
        Assert.assertEquals(true, preObject.getIn().getHeader("hasErrors"));
        Assert.assertEquals(MessageType.XML_FILE_PROCESSED.name(), preObject.getIn().getHeader("IngestionMessageType"));
    }

}
