package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.FileNotFoundException;

import junitx.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.validation.TestErrorReport;
import org.slc.sli.ingestion.util.MD5;
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
    public void testValidFile() throws FileNotFoundException {
        // Test the XML reference resolution handler on a valid test file.
        File inputFile = IngestionTest.getFile("ReferenceResolution/studentAssessment_Valid.xml");
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_ASSESSMENT, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        IngestionFileEntry outputFileEntry = referenceResolutionHandler.doHandling(inputFileEntry, errorReport, new FileProcessStatus());
        Assert.assertSame(inputFile, outputFileEntry.getFile());
    }

    /**
     * @throws FileNotFoundException
     *
     */
    @Test
    @Ignore
    public void testInvalidFile() throws FileNotFoundException {
        // Test the XML reference resolution handler on an invalid test file.
        File inputFile = IngestionTest.getFile("ReferenceResolution/studentAssessment_inValid.xml");
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_ASSESSMENT, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        IngestionFileEntry outputFileEntry = referenceResolutionHandler.doHandling(inputFileEntry, errorReport, new FileProcessStatus());
        Assert.assertNotNull(outputFileEntry);
    }

    /**
     * @throws FileNotFoundException
     *
     */
    @Test
    public void testMalformedFile() throws FileNotFoundException {
        // Test the XML reference resolution handler on a malformed test file.
        File inputFile = IngestionTest.getFile("ReferenceResolution/studentAssessment_Malformed.xml");
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_ASSESSMENT, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        IngestionFileEntry outputFileEntry = referenceResolutionHandler.doHandling(inputFileEntry, errorReport, new FileProcessStatus());
        Assert.assertNotNull(outputFileEntry);
    }

}
