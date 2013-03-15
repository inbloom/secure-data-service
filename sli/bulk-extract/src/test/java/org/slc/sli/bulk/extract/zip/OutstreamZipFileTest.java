package org.slc.sli.bulk.extract.zip;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class OutstreamZipFileTest {
    private static final String TESTPATH = "src/test/resources/";
    
    @Test
    public void testOutstreamZipFile() {
        File tmpFile = null;
        try {
            new OutstreamZipFile(TESTPATH, "test");
            
            tmpFile = new File(TESTPATH + "test_tmp.zip");
            Assert.assertTrue(tmpFile.exists());
            Assert.assertTrue(!tmpFile.isDirectory());
            
        } catch (IOException e) {
            Assert.fail();
        } finally {
            FileUtils.deleteQuietly(tmpFile);
        }
    }
    
    @Test
    public void testCreateAndWriteArchiveEntry() {
        OutstreamZipFile zip = null;
        File unzipDirectory = null;
        try {
            zip = new OutstreamZipFile(TESTPATH, "test");
            
            zip.createArchiveEntry("testArchive.json");
            zip.writeData("testArchive");
            zip.renameTempZipFile();
            
            unzipDirectory = new File(TESTPATH + "unzip/test");
            if (unzipDirectory.exists()) {
                FileUtils.deleteQuietly(unzipDirectory);
            }
            ZipFileUtil.extract(zip.getZipFile());
            File archivedFile = new File(TESTPATH + "unzip/test/testArchive.json");
            Assert.assertTrue(archivedFile.exists());
        } catch (IOException e) {
            Assert.fail();
        } finally {
            FileUtils.deleteQuietly(zip.getZipFile());
            FileUtils.deleteQuietly(unzipDirectory);
        }
    }
    
}
