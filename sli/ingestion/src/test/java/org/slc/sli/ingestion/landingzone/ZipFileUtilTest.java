package org.slc.sli.ingestion.landingzone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.slc.sli.ingestion.landingzone.ZipFileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-test.xml" })
public class ZipFileUtilTest {

    @Autowired
    LocalFileSystemLandingZone lz;

    @Test
    public void testExtractFileValid() throws IOException {

        // valid zip file
        File zipFile = new File(lz.directory + File.separator + "GoodZip.zip");

        File path = ZipFileUtil.extract(zipFile);
        File ctlFile = new File(path, "ControlFle.ctl");

        // assert the control file is found
        assertEquals(ctlFile, ctlFile.getAbsoluteFile());

        deleteDirectory(new File(lz.directory + File.separator + "unzip"));
    }

    @Test
    public void testExtractFileInvalid() throws IOException {

        // invalid zip file
        File zipFile = new File(lz.directory + File.separator + "BadZip.zip");

        Exception eInvalidZip = null;

        File path = null;
        try {
            path = ZipFileUtil.extract(zipFile);
            File ctlFile = new File(path, "ControlFle.ctl");

        } catch (IOException e) {
            eInvalidZip = e;
        }

        // assert the exception object
        assertNotNull("No expected exception", eInvalidZip);

        deleteDirectory(new File(lz.directory + File.separator + "unzip"));

    }

    private void deleteDirectory(File path) throws IOException {
        if (path.isDirectory()) {
            for (File child : path.listFiles()) {
                deleteDirectory(child);
            }
        }

        if (!path.delete()) {
            throw new IOException("Could not delete " + path);
        }
    }
}
