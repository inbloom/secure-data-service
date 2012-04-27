package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.model.NewBatchJob;

/**
 * Tests for ControlFileProcessor
 *
 */
@Ignore
// made obsolete by NewBatchJob integration
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

        Exchange eObject = new DefaultExchange(new DefaultCamelContext());

        eObject.getIn().setHeaders(preObject.getIn().getHeaders());
        eObject.getIn().setBody(preObject.getIn().getBody());

        processor.process(eObject);

        NewBatchJob bj = eObject.getIn().getBody(NewBatchJob.class);

        assertNotNull("BatchJob is not defined", bj);

        boolean hasErrors = (Boolean) eObject.getIn().getHeader("hasErrors");

        assertNotNull("header [hasErrors] not set", hasErrors);
    }

}
