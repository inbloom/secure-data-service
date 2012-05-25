package org.slc.sli.ingestion.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.RemoteException;

import junit.framework.Assert;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * LogUtil unit-tests
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LogUtilTest {

    // private static final Logger LOG = LoggerFactory.getLogger(LogUtilTest.class);
    private static Logger logger = null;
    private static LoggerContext loggerContext;
    private static PatternLayoutEncoder encoder;
    private static FileAppender<ILoggingEvent> fileAppender;
    private static final String LOG_FILE_NAME = "target/logs/LogUtilTest.log";

    static {
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger = loggerContext.getLogger(LogUtilTest.class);

        encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%date %-5level %msg%n");
        encoder.start();

        fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName("LogUtilTest");
        fileAppender.setEncoder(encoder);
    }

    @Test
    public void testLogUtil() {
        // Log a nested exception.
        File logfile = new File(LOG_FILE_NAME);
        logfile.delete();  // Clear out old contents.
        logToFile(LOG_FILE_NAME);
        try {
            throw new RemoteException("*** SENSITIVE DATA ONE!!! ***");
        } catch (RemoteException re1) {
            try {
                throw new RuntimeException("*** SENSITIVE DATA TWO!!! ***", re1);
            } catch (RuntimeException re2) {
                try {
                    throw new AccessException("*** SENSITIVE DATA THREE!!! ***", re2);
                } catch (AccessException ae) {
                    String message = "This is a test of the LogUtil utility";
                    LogUtil.error(logger, message, ae);
                }
            }
        }

        // Verify the logged contents.
        String fileContents = readLogFile(LOG_FILE_NAME);

        // Verify log file does NOT contain exception local message.
        Assert.assertFalse(fileContents.contains("*** SENSITIVE DATA ONE!!! ***"));
        Assert.assertFalse(fileContents.contains("*** SENSITIVE DATA TWO!!! ***"));
        Assert.assertFalse(fileContents.contains("*** SENSITIVE DATA THREE!!! ***"));

        // Verify log file DOES contain error message with nested exception types and stack traces.
        Assert.assertTrue(fileContents.contains("ERROR This is a test of the LogUtil utility"));
        Assert.assertTrue(fileContents.contains("java.lang.Exception: class java.rmi.AccessException"));
        Assert.assertTrue(fileContents
                .contains("at org.slc.sli.ingestion.util.LogUtilTest.testLogUtil(LogUtilTest.java:70) ~[test-classes/:na]"));
        Assert.assertTrue(fileContents.contains("Caused by: java.lang.Exception: class java.lang.RuntimeException"));
        Assert.assertTrue(fileContents
                .contains("at org.slc.sli.ingestion.util.LogUtilTest.testLogUtil(LogUtilTest.java:67) ~[test-classes/:na]"));
        Assert.assertTrue(fileContents.contains("... 28 common frames omitted"));
        Assert.assertTrue(fileContents.contains("Caused by: java.lang.Exception: class java.rmi.RemoteException"));
        Assert.assertTrue(fileContents
                .contains("at org.slc.sli.ingestion.util.LogUtilTest.testLogUtil(LogUtilTest.java:64) ~[test-classes/:na]"));
    }

    private void logToFile(String fileName) {
        logger.detachAndStopAllAppenders();
        fileAppender.setFile(fileName);
        fileAppender.start();
        logger.addAppender(fileAppender);
    }

    String readLogFile(String fileName) {
        String fileContents = new String();
        try {
            FileInputStream inputFileStream = new FileInputStream(fileName);
            DataInputStream inputFileDataStream = new DataInputStream(inputFileStream);
            BufferedReader inputFileReader = new BufferedReader(new InputStreamReader(inputFileDataStream));
            String line;
            while ((line = inputFileReader.readLine()) != null) {
                fileContents += line;
            }
            inputFileReader.close();
        } catch (IOException e) {
            Assert.fail();
        }
        return fileContents;
    }

}
