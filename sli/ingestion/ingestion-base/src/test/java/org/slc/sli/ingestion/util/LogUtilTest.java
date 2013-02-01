/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.regex.Pattern;

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
    public void testLogUtil() {
        // Log a nested exception.
        final Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.isErrorEnabled()).thenReturn(true);
        try {
            throw new RemoteException("*** EXCEPTION MESSAGE ONE!!! ***");
        } catch (RemoteException re1) {
            try {
                throw new RuntimeException("*** EXCEPTION MESSAGE TWO!!! ***", re1);
            } catch (RuntimeException re2) {
                try {
                    throw new AccessException("*** EXCEPTION MESSAGE THREE!!! ***", re2);
                } catch (AccessException ae) {
                    String message = "This is a test of the LogUtil utility";

                    // Now test what would be logged.

                    // First, without exception local message logging.
                    LogUtil.setIncludeExceptionMessage(false);
                    LogUtil.error(mockLogger, message, ae);
                    Mockito.verify(mockLogger).error(Mockito.eq(message), Mockito.argThat(new IsCorrectException()));

                    // Next, with exception local message logging.
                    LogUtil.setIncludeExceptionMessage(true);
                    LogUtil.error(mockLogger, message, ae);
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

            // Verify the presence of the logged exception message based upon the configuration
            // property value.
            if (LogUtil.isIncludeExceptionMessage()) {
                // Verify log file DOES contain exception local message.
                if (!logContents.contains("java.rmi.AccessException: *** EXCEPTION MESSAGE THREE!!! ***")
                        || !logContents
                                .contains("Caused by: java.lang.RuntimeException: *** EXCEPTION MESSAGE TWO!!! ***")
                        || !logContents
                                .contains("Caused by: java.rmi.RemoteException: *** EXCEPTION MESSAGE ONE!!! ***")) {
                    return false;
                }
            } else {
                // Verify log file does NOT contain exception local message.
                if (logContents.contains("*** EXCEPTION MESSAGE THREE!!! ***")
                        || logContents.contains("*** EXCEPTION MESSAGE TWO!!! ***")
                        || logContents.contains("*** EXCEPTION MESSAGE ONE!!! ***")) {
                    return false;
                }

                // Verify log file DOES specialized exception types.
                if (!logContents.contains("java.lang.Exception: class java.rmi.AccessException")
                        || !logContents.contains("Caused by: java.lang.Exception: class java.lang.RuntimeException")
                        || !logContents.contains("Caused by: java.lang.Exception: class java.rmi.RemoteException")) {
                    return false;
                }
            }

            Pattern p = Pattern.compile(".*\\.\\.\\. [\\d]* more.*", Pattern.DOTALL);

            // Verify log file DOES contain nested exception stack traces.
            if (!logContents.contains("at org.slc.sli.ingestion.util.LogUtilTest.testLogUtil(LogUtilTest.java:56)")
                    || !logContents
                            .contains("at org.slc.sli.ingestion.util.LogUtilTest.testLogUtil(LogUtilTest.java:53)")
                    || !p.matcher(logContents).matches()
                    || !logContents
                            .contains("at org.slc.sli.ingestion.util.LogUtilTest.testLogUtil(LogUtilTest.java:50)")) {
                return false;
            }

            return true;
        }
    }

}
