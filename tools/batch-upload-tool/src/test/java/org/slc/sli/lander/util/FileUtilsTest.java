package org.slc.sli.lander.util;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests the FileUtils Handler
 * 
 * @author srichards
 * 
 */
public class FileUtilsTest {
    @Test
    public void zipFilesInDirectory() throws IOException {
        File directory = new File("target");
        File testFile = new File(directory, "test.xml");
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            Assert.fail("Unable to set up test preconditions.");
        }
        Assert.assertTrue("zip operation should succeed", FileUtils.zipIngestionData("target") != null);
        File zip = new File(directory, FileUtils.ZIP_FILE_NAME);
        Assert.assertTrue("zip file should exist", zip.exists());
    }
}
