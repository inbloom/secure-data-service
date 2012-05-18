package org.slc.sli.lander.util;

/**
 * Tests the FileUtils Handler
 *
 * @author srichards
 *
 */
public class FileUtilsTest {
    /* The code for this test is in DataUtils, not FileUtils, and the test should be moved to reflect that.
    @Test
    public void zipFilesInDirectory() {
        File directory = new File("target");
        File testFile = new File(directory, "test.xml");
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            fail("Unable to set up test preconditions.");
        }
        assertTrue("zip operation should succeed", FileUtils.zipIngestionData("target"));
        File zip = new File(directory, FileUtils.ZIP_FILE_NAME);
        assertTrue("zip file should exist", zip.exists());
    }*/
}
