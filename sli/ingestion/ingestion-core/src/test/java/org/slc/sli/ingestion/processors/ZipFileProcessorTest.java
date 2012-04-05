package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * ZipFileProcessor unit tests.
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ZipFileProcessorTest {

    @Autowired
    private ZipFileProcessor zipProc;

    @Test
    public void testHappyZipFileProcessor() throws Exception {

        Exchange preObject = new DefaultExchange(new DefaultCamelContext());

        preObject.getIn().setBody(IngestionTest.getFile("zip/ValidZip.zip"));

        zipProc.process(preObject);

        Assert.assertNotNull(preObject.getIn().getBody());
        Assert.assertEquals("ControlFile.ctl", ((File) preObject.getIn().getBody()).getName());
    }

    @Test
    public void testNoControlFileZipFileProcessor() throws Exception {

        Exchange preObject = new DefaultExchange(new DefaultCamelContext());

        preObject.getIn().setBody(IngestionTest.getFile("zip/NoControlFile.zip"));

        zipProc.process(preObject);

        Assert.assertNotNull(preObject.getIn().getBody());
        Assert.assertTrue((Boolean) preObject.getIn().getHeader("hasErrors"));
        Assert.assertEquals(preObject.getIn().getHeader("IngestionMessageType") , MessageType.BATCH_REQUEST.name());
    }
}
