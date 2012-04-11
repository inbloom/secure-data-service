package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.validation.TestErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ReferenceResolutionHandlerTest {

    private ReferenceResolutionHandler referenceResolutionHandler = new ReferenceResolutionHandler();

    private final ErrorReport errorReport = new TestErrorReport();

    /**
     * @throws FileNotFoundException
     *
     */
    @Test
    public void testdoHandling() throws FileNotFoundException {

        // Test the XML file expander on a large test file.
        File inputFile = IngestionTest.getFile("ReferenceResolution/studentAssessment_1000.xml");
        referenceResolutionHandler.doHandling(inputFile, errorReport);
    }

}
