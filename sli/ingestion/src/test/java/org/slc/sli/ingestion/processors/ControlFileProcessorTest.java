package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;

/**
 * Tests for ControlFileProcessor
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class ControlFileProcessorTest {

    @Autowired
    LandingZone lz;

    @Autowired
    ControlFileProcessor processor;

    @Test
    public void shouldAcceptExchangeObjectReadExchangeControlFileAndSetExchangeBatchJob() throws Exception {

        assertNotNull("Landing zone is not being injected.", lz);

        assertTrue("Unexpected implementation of landing zone", lz instanceof LocalFileSystemLandingZone);

        Exchange preObject = new DefaultExchange(new DefaultCamelContext());

        preObject.getIn().setBody(IngestionTest.getFile("smooks/InterchangeStudentCsv.ctl"));

        new ControlFilePreProcessor(lz).process(preObject);

        Exchange eObject = new DefaultExchange(new DefaultCamelContext());

        eObject.getIn().setBody(preObject.getIn().getBody());

        processor.process(eObject);

        BatchJob bj = eObject.getIn().getBody(BatchJob.class);

        assertNotNull("BatchJob is not defined", bj);

        boolean hasErrors = (Boolean) eObject.getIn().getHeader("hasErrors");

        assertNotNull("header [hasErrors] not set on the exchange", hasErrors);

    }

}
