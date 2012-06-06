package org.slc.sli.ingestion.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.slf4j.Logger;
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

    @Test
    @Ignore( "Temporarily disabling log util from swallowing exception message")
    public void testLogUtil() {
        // Log a nested exception.
        final Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.isErrorEnabled()).thenReturn(true);
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
                    LogUtil.error(mockLogger, message, ae);

                    // Now test what would be logged.
                    Mockito.verify(mockLogger).error(Mockito.eq(message), Mockito.argThat(new IsCorrectException()));
                }
            }
        }

    }

    private class IsCorrectException extends ArgumentMatcher<Exception> {

        @Override
        public boolean matches(Object argument) {
            Exception arg = (Exception) argument;

            // Verify the logged contents.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            arg.printStackTrace(ps);
            String logContents = baos.toString(); // e.g. ISO-8859-1

            // Verify log file does NOT contain exception local message.
            if (logContents.contains("*** SENSITIVE DATA THREE!!! ***")
                    || logContents.contains("*** SENSITIVE DATA TWO!!! ***")
                    || logContents.contains("*** SENSITIVE DATA ONE!!! ***")) {
                return false;
            }

            Pattern p = Pattern.compile(".*\\.\\.\\. [\\d]* more.*", Pattern.DOTALL);

            // Verify log file DOES contain error message with nested exception types and stack
            // traces.
            if (!logContents.contains("java.lang.Exception: class java.rmi.AccessException")
                    || !logContents
                            .contains("at org.slc.sli.ingestion.util.LogUtilTest.testLogUtil(LogUtilTest.java:39)")
                    || !logContents.contains("Caused by: java.lang.Exception: class java.lang.RuntimeException")
                    || !logContents
                            .contains("at org.slc.sli.ingestion.util.LogUtilTest.testLogUtil(LogUtilTest.java:36)")
                    || !p.matcher(logContents).matches()
                    || !logContents.contains("Caused by: java.lang.Exception: class java.rmi.RemoteException")
                    || !logContents
                            .contains("at org.slc.sli.ingestion.util.LogUtilTest.testLogUtil(LogUtilTest.java:33)")) {
                return false;
            }

            return true;
        }
    };

}
