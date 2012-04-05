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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for OfflineTool main
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
    public boolean verify(String appSt, String target) {
        //parse the log
        Appender appender = OfflineTool.getThreadLocal().get().getAppender(appSt);
        if (appender.getClass() == FileAppender.class) {
            FileAppender fa = (FileAppender) appender;
            file = new File(fa.getFile());
        }

        try {
            reader = new FileReader(file);
            lreader = new LineNumberReader(reader);

            if (file.exists()) {
                try {
                    String cur;
                    while ((cur = lreader.readLine()) != null) {
                        if (cur.contains(target)) return true;
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
      * @param dir : the path of the test data
      * @param toolLog : the appender name of the log file. Can be
      *         either ToolLog or ConsoleAppender, as are hardcoded in OfflineTool
      * @param target: the target string to be verify in the log file
      */
     void toolTest(String dir, String toolLog, String target) {
         Resource fileResource = new ClassPathResource(dir);
         String [] args = new String[1];
         try {
             args[0] = fileResource.getFile().toString();
         } catch (IOException e) {
             fail("IO Exception");
         }
         try {
             OfflineTool.main(args);
             Assert.assertTrue(verify(toolLog, target));
         } catch (IOException e) {
             fail("IO Exception from main");
         }
     }

    @Test
    public void testMain() {

        //Testing valid zip file
        toolTest("zipFile/Session1.zip", "ToolLog", "processing is complete.");

        //Testing valid control file
        toolTest("test/MainControlFile.ctl", "ToolLog", "processing is complete.");

        //Testing valid control file
        toolTest("test/MainControlFile.ctl", "ToolLog", "processing is complete.");

    }

    void toolTestConsole(String path, String appenderName, String target) {
        String [] args3 = new String[1];
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
            Assert.assertTrue(verify(appenderName, target));
        } catch (IOException e) {
            fail("IO Exception from main");
        }
        file.delete();
    }

    @Test
    public void negativeTestMain() {

        //Testing an invalid zip file
        toolTest("invalidZip/SessionInvalid.zip", "ToolLog", "Please resubmit");

        //Testing an empty control file
        toolTest("invalid/MainControlFile.ctl", "ToolLog", "No files specified in control file");

        //Testing control file with wrong checksum
        toolTest("invalid/wrongChecksum.ctl", "ToolLog", "Please resubmit");


        //Testing more than 1 input arguments
        String [] args3 = new String[2];
        file = new File(tempConsole);
        try {
            System.setErr(new PrintStream(file));
        } catch (FileNotFoundException e) {
            fail("Temp console txt not found");
        }
        try {
            OfflineTool.main(args3);
            Assert.assertTrue(verify("ConsoleAppender", "Illegal options"));
        } catch (IOException e) {
            fail("IO Exception from main");
        }
        file.delete();

        //Testing doesn't existing file
        String [] args4 = new String[1];
        args4[0] = "/invalid/nonExist.ctl";
        file = new File(tempConsole);
        try {
            System.setErr(new PrintStream(file));
        } catch (FileNotFoundException e) {
            fail("Temp console txt not found");
        }
        try {
            OfflineTool.main(args4);
            Assert.assertTrue(verify("ConsoleAppender", "does not exist"));
        } catch (IOException e) {
            fail("IO Exception from main");
        }
        file.delete();

        //Testing direcotry
        toolTestConsole("invalid/", "ConsoleAppender", "Expecting a Zip or a Ctl");

        //Testing invalid XML file
        toolTest("invalidXML/MainControlFile.ctl", "ToolLog", "InterchangeAssessmentMetadata.xml: Checksum validation failed");
    }

}
