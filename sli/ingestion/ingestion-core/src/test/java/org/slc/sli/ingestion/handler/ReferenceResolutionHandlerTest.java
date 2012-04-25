package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junitx.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileFormat;
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
     * @throws IOException
     *
     */
    @Test
    public void testValidFile() throws IOException {
        // Test the XML reference resolution handler on a valid test file.
        File inputFile = IngestionTest.getFile("ReferenceResolution/studentAssessment_Valid.xml");
        File tempInputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml")) + "_TEMP.xml");

        IOUtils.copy(new FileInputStream(inputFile), new FileOutputStream(tempInputFile));

        long inputFileLength = inputFile.length();
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_ASSESSMENT, tempInputFile.getName(), MD5.calculate(tempInputFile));
        inputFileEntry.setFile(tempInputFile);
        IngestionFileEntry outputFileEntry = referenceResolutionHandler.doHandling(inputFileEntry, errorReport, null);
        long outputFileLength = outputFileEntry.getFile().length();
        Assert.assertEquals(tempInputFile.getName(), "studentAssessment_Valid_TEMP.xml");
        Assert.assertNotSame(inputFileLength, outputFileLength);

        tempInputFile.delete();
    }

    /**
     * @throws IOException
     *
     */
    @Test
    public void testInvalidFile() throws IOException {
        // Test the XML reference resolution handler on an invalid test file.
        File inputFile = IngestionTest.getFile("ReferenceResolution/studentAssessment_Invalid.xml");
        File tempInputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml")) + "_TEMP.xml");

        IOUtils.copy(new FileInputStream(inputFile), new FileOutputStream(tempInputFile));

        long inputFileLength = inputFile.length();
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_ASSESSMENT, tempInputFile.getName(), MD5.calculate(tempInputFile));
        inputFileEntry.setFile(tempInputFile);
        IngestionFileEntry outputFileEntry = referenceResolutionHandler.doHandling(inputFileEntry, errorReport, null);
        long outputFileLength = outputFileEntry.getFile().length();
        Assert.assertEquals(tempInputFile.getName(), "studentAssessment_Invalid_TEMP.xml");
        Assert.assertNotSame(inputFileLength, outputFileLength);

        tempInputFile.delete();
    }

    /**
     * @throws FileNotFoundException
     *
     */
    @Test
    public void testMalformedFile() throws FileNotFoundException {
        // Test the XML reference resolution handler on a malformed test file.
        File inputFile = IngestionTest.getFile("ReferenceResolution/studentAssessment_Malformed.xml");
        long inputFileLength = inputFile.length();
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_ASSESSMENT, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        IngestionFileEntry outputFileEntry = referenceResolutionHandler.doHandling(inputFileEntry, errorReport, null);
        long outputFileLength = outputFileEntry.getFile().length();
        Assert.assertEquals(inputFile.getName(), "studentAssessment_Malformed.xml");
        Assert.assertEquals(inputFileLength, outputFileLength);
    }

}
