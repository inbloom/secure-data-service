package org.slc.sli.ingestion.landingzone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-test.xml" })
public class BatchJobAssemblerTest {

    @Autowired
    private BatchJobAssembler jobAssembler;

    @Autowired
    MessageSource messageSource;

    @Test
    public void testAssembleJobValid() throws IOException {

        // set up some valid entries
        ArrayList<ControlFile.FileEntry> entries = new ArrayList<ControlFile.FileEntry>();
        entries.add(new ControlFile.FileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT, "InterchangeStudent.xml",
                getMd5("InterchangeStudent.xml", jobAssembler.getLandingZone())));
        entries.add(new ControlFile.FileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_ENROLLMENT,
                "InterchangeEnrollment.xml", getMd5("InterchangeEnrollment.xml", jobAssembler.getLandingZone())));

        // set up some valid properties
        Properties props = new Properties();
        props.setProperty("hello", "world");

        ControlFile ctlFile = new ControlFile(null, entries, props);

        BatchJob job = jobAssembler.assembleJob(ctlFile);

        assertEquals(2, job.getFiles().size());
        assertEquals(0, job.getFaults().size());
        assertFalse(job.hasErrors());
        assertEquals("world", job.getProperty("hello"));

    }

    private static String getMd5(String fileName, LandingZone lz) {
        String md5 = "";

        File f = lz.getFile(fileName);
        DigestInputStream dis = null;

        if (f != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");

                dis = new DigestInputStream(new FileInputStream(f), md);

                byte[] buf = new byte[1024];

                while (dis.read(buf, 0, 1024) != -1) {
                }

                md5 = Hex.encodeHexString(dis.getMessageDigest().digest());
            } catch (NoSuchAlgorithmException e) {
                md5 = "";
            } catch (IOException e) {
                md5 = "";
            } finally {
                if (dis != null) {
                    try {
                        dis.close();
                    } catch (IOException e) {
                        dis = null;
                    }
                }
            }
        }

        return md5;
    }

    @Ignore
    @Test
    public void testAssembleJobWithWarnings() {
        fail("Not yet implemented");
    }

    @Test
    public void testAssembleJobWithErrors() throws IOException {

        // set up entries
        ArrayList<ControlFile.FileEntry> entries = new ArrayList<ControlFile.FileEntry>();

        // file name deliberately incorrect
        entries.add(new ControlFile.FileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT, "ZInterchangeStudent.xml",
                "1257ae55b836dc57d635f0733c115179"));

        // checksum deliberately incorrect
        entries.add(new ControlFile.FileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_ENROLLMENT,
                "InterchangeEnrollment.xml", "Zae81485112ff2e1a62bc06b35a131692"));

        // set up some valid properties
        Properties props = new Properties();
        props.setProperty("hello", "world");

        ControlFile ctlFile = new ControlFile(null, entries, props);

        BatchJob job = jobAssembler.assembleJob(ctlFile);

        assertEquals(0, job.getFiles().size());
        assertEquals(2, job.getFaults().size());
        assertTrue(job.hasErrors());

        assertEquals(job.getFaults().get(0).getMessage(),
                messageSource.getMessage("SL_ERR_MSG8", new Object[] { entries.get(0).fileName }, null));
        assertEquals(job.getFaults().get(1).getMessage(),
                messageSource.getMessage("SL_ERR_MSG2", new Object[] { entries.get(1).fileName }, null));
        assertEquals("world", job.getProperty("hello"));

    }

}
