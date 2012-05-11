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
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
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
        Job job = BatchJob.createDefault();
        ((BatchJob) job).addFile(entry);
        preObject.getIn().setBody(job);
        IdRefResolutionHandler handler = Mockito.mock(IdRefResolutionHandler.class);
        FaultsReport faults = Mockito.mock(FaultsReport.class);

        Mockito.when(handler.handle(Mockito.any(IngestionFileEntry.class), Mockito.any(ErrorReport.class))).thenReturn(
                entry);
        Mockito.when(entry.getFaultsReport()).thenReturn(faults);
        Mockito.when(faults.hasErrors()).thenReturn(true);
        xmlFileProcessor.setIdRefResolutionHandler(handler);

        xmlFileProcessor.process(preObject);
        Assert.assertEquals(job, preObject.getIn().getBody(BatchJob.class));
    }

}
