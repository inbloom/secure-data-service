package org.slc.sli.ingestion.handler;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.landingzone.validation.TestErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ReferenceResolutionHandlerTest {

    private ReferenceResolutionHandler referenceResolutionHandler = new ReferenceResolutionHandler();

    private final ErrorReport errorReport = new TestErrorReport();

    /**
     *
     */
    @Test
    public void testdoHandling() {

        // Test the XML file expander on a large test file.
        File inputFile = new File("C:\\Users\\tshewchuk\\workspace\\data\\Test_XML_Expander\\studentAssessment_1000.xml");
            referenceResolutionHandler.doHandling(inputFile, errorReport);
    }

}
