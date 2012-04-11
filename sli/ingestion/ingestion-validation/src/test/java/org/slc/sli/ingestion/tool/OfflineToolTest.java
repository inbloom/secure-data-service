package org.slc.sli.ingestion.tool;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for OfflineTool main
 *
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class OfflineToolTest {
    File file;
    FileReader reader = null;
    LineNumberReader lreader = null;

    final String tempConsole = "tempConsole.txt";

    @SuppressWarnings("rawtypes")
    public boolean verify(String target, Boolean isConsole) {

        if (isConsole) {
            file = new File(tempConsole);
        } else {
            // parse the log
            Appender appender = LoggerUtil.getFileAppender();
            if (appender.getClass() == FileAppender.class) {
                FileAppender fa = (FileAppender) appender;
                file = new File(fa.getFile());
            }
        }

        try {
            reader = new FileReader(file);
            lreader = new LineNumberReader(reader);

            if (file.exists()) {
                try {
                    String cur;
                    while ((cur = lreader.readLine()) != null) {
                        if (cur.contains(target))
                            return true;
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    /**
     *
     * @param dir
     *            : the path of the test data
     * @param target
     *            : the target string to be verify in the log file
     */
    void toolTest(String dir, String target) {
        Resource fileResource = new ClassPathResource(dir);
        String[] args = new String[1];
        try {
            args[0] = fileResource.getFile().toString();
        } catch (IOException e) {
            fail("IO Exception");
        }
        try {
            OfflineTool.main(args);
            Assert.assertTrue(verify(target, false));
        } catch (IOException e) {
            fail("IO Exception from main");
        }
    }

    @Test
    public void testMain() {

        // Testing valid zip file
        toolTest("zipFile/Session1.zip", "processing is complete.");

        // Testing valid control file
        toolTest("test/MainControlFile.ctl", "processing is complete.");

        // Testing valid control file
        toolTest("test/MainControlFile.ctl", "processing is complete.");

    }

    void toolTestConsole(String path, String target) {
        String[] args3 = new String[1];
        Resource fileResource = new ClassPathResource(path);
        file = new File(tempConsole);
        try {
            System.setErr(new PrintStream(file));
        } catch (FileNotFoundException e) {
            fail("Temp console txt not found");
        }
        try {
            args3[0] = fileResource.getFile().toString();
            OfflineTool.main(args3);
            Assert.assertTrue(verify(target, true));
        } catch (IOException e) {
            fail("IO Exception from main");
        }
        file.delete();
    }

    @Ignore
    public void negativeTestMain() {

        // Testing an invalid zip file
        toolTest("invalidZip/SessionInvalid.zip", "Please resubmit");

        // Testing an empty control file
        toolTest("invalid/MainControlFile.ctl", "No files specified in control file");

        // Testing control file with wrong checksum
        toolTest("invalid/wrongChecksum.ctl", "Checksum validation failed");

        toolTest("invalid/XsdTest/XSD.ctl", "cvc-minLength-valid");

        // Testing more than 1 input arguments
        String[] args3 = new String[2];
        file = new File(tempConsole);
        try {
            System.setErr(new PrintStream(file));
        } catch (FileNotFoundException e) {
            fail("Temp console txt not found");
        }
        try {
            OfflineTool.main(args3);
            Assert.assertTrue(verify("Illegal options", true));
        } catch (IOException e) {
            fail("IO Exception from main");
        }
        file.delete();

        // Testing doesn't existing file
        String[] args4 = new String[1];
        args4[0] = "/invalid/nonExist.ctl";
        file = new File(tempConsole);
        try {
            System.setErr(new PrintStream(file));
        } catch (FileNotFoundException e) {
            fail("Temp console txt not found");
        }
        try {
            OfflineTool.main(args4);
            Assert.assertTrue(verify("does not exist", true));
        } catch (IOException e) {
            fail("IO Exception from main");
        }
        file.delete();

        // Testing direcotry
        toolTestConsole("invalid/", "Expecting a Zip or a Ctl");

        // Testing invalid XML file
        toolTest("invalidXML/MainControlFile.ctl", "InterchangeAssessmentMetadata.xml: Checksum validation failed");
    }

}
