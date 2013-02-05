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

import org.slf4j.Logger;

/**
 * Utility class for writing exception information without PII to the log file
 *
 * @author tshewchuk
 *
 */
public class LogUtil {

    private static boolean includeExceptionMessage;

    /**
     * Write the appropriate trace message to the log file
     *
     * @param log
     *            logger to write the message
     * @param message
     *            specific message to write to the log file
     * @param exception
     *            the exception which caused the log file entry
     */
    public static void trace(Logger log, String message, Throwable exception) {
        // Log the error with a message-safe exception.
        if (log.isTraceEnabled()) {
            Throwable loggingException = createLoggingException(exception);
            log.trace(message, loggingException);
        }
    }

    /**
     * Write the appropriate debug message to the log file
     *
     * @param log
     *            logger to write the message
     * @param message
     *            specific message to write to the log file
     * @param exception
     *            the exception which caused the log file entry
     */
    public static void debug(Logger log, String message, Throwable exception) {
        // Log the error with a message-safe exception.
        if (log.isDebugEnabled()) {
            Throwable loggingException = createLoggingException(exception);
            log.debug(message, loggingException);
        }
    }

    /**
     * Write the appropriate info message to the log file
     *
     * @param log
     *            logger to write the message
     * @param message
     *            specific message to write to the log file
     * @param exception
     *            the exception which caused the log file entry
     */
    public static void info(Logger log, String message, Throwable exception) {
        // Log the error with a message-safe exception.
        if (log.isInfoEnabled()) {
            Throwable loggingException = createLoggingException(exception);
            log.info(message, loggingException);
        }
    }

    /**
     * Write the appropriate warning message to the log file
     *
     * @param log
     *            logger to write the message
     * @param message
     *            specific message to write to the log file
     * @param exception
     *            the exception which caused the log file entry
     */
    public static void warn(Logger log, String message, Throwable exception) {
        // Log the error with a message-safe exception.
        if (log.isWarnEnabled()) {
            Throwable loggingException = createLoggingException(exception);
            log.warn(message, loggingException);
        }
    }

    /**
     * Write the appropriate error message to the log file
     *
     * @param log
     *            logger to write the message
     * @param message
     *            specific message to write to the log file
     * @param exception
     *            the exception which caused the log file entry
     */
    public static void error(Logger log, String message, Throwable exception) {
        // Log the error with a message-safe exception.
        if (log.isErrorEnabled()) {
            Throwable loggingException = createLoggingException(exception);
            log.error(message, loggingException);
        }
    }

    /**
     * Create a message-safe exception from the original exception
     *
     * @param exception
     *            original exception from which the message-safe exception will be created
     *
     * @return Exception
     *         the new message-safe exception
     */
    private static Throwable createLoggingException(Throwable exception) {
        return createLoggingException(exception, includeExceptionMessage);
    }

    /**
     * Create a message-safe exception from the original exception
     *
     * @param exception
     *            original exception from which the message-safe exception will be created
     *
     * @return Exception
     *         the new message-safe exception
     */
    public static Throwable createLoggingException(Throwable exception, boolean includeMessage) {
        if (includeMessage) {
            return exception;
        }

        Exception loggingException;
        if (exception.getCause() == null) {
            loggingException = new Exception(exception.getClass().toString());  //NOPMD Need to use raw exception as type of exception is unknown
        } else {
            loggingException = new Exception(exception.getClass().toString(),   //NOPMD Need to use raw exception as type of exception is unknown
                    createLoggingException(exception.getCause(), includeMessage));
        }
        loggingException.setStackTrace(exception.getStackTrace());
        return loggingException;
    }

    public static boolean isIncludeExceptionMessage() {
        return includeExceptionMessage;
    }

    public static void setIncludeExceptionMessage(boolean includeExceptionMessage) {
        LogUtil.includeExceptionMessage = includeExceptionMessage;
    }

}
