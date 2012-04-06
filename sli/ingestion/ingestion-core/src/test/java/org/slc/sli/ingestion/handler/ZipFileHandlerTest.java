package org.slc.sli.ingestion.handler;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FaultsReport;

/**
 * ZipFileHandler unit tests.
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ZipFileHandlerTest {

    @Autowired
    private ZipFileHandler zipHandler;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void testZipHandling() {
        File zip = new File("src/test/resources/zip/ValidZip.zip");

        FaultsReport errorReport = new FaultsReport();

        File ctlFile = zipHandler.handle(zip, errorReport);

        Assert.assertFalse(errorReport.hasErrors());
        Assert.assertNotNull(ctlFile);
        Assert.assertTrue(ctlFile.exists());
    }

    @Test
    public void testAbsenceOfZipHandling() {
        File zip = new File("src/test/resources/zip/NoControlFile.zip");

        FaultsReport errorReport = new FaultsReport();

        File ctlFile = zipHandler.handle(zip, errorReport);

        Assert.assertTrue(errorReport.hasErrors());
        Assert.assertNull(ctlFile);
    }

    @Test
    public void testIOExceptionHandling() {
        File zip = new File("src/test/resources/zip/NoControlFile2.zip");

        ZipFileHandler zipHandler = new ZipFileHandler();
        zipHandler.setMessageSource(messageSource);

        FaultsReport errorReport = new FaultsReport();

        File ctlFile = zipHandler.handle(zip, errorReport);

        Assert.assertTrue(errorReport.hasErrors());
        Assert.assertNull(ctlFile);
    }

}
