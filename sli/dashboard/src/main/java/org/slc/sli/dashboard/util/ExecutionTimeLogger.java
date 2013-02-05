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


package org.slc.sli.dashboard.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple util that provides annotation based time logging mechanism for a method execution
 * @author agrebneva
 *
 */
@Aspect
public class ExecutionTimeLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTimeLogger.class);
    /**
     * Annotation to mark methods that need execution time logging
     * @author agrebneva
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface LogExecutionTime {
    }
    @Around(value = "@annotation(annotation)")
    public Object log(final ProceedingJoinPoint proceedingJoinPoint, final LogExecutionTime annotation) throws Throwable {
        final long startTime = System.nanoTime();
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            if (ProceedingJoinPoint.METHOD_CALL.equals(proceedingJoinPoint.getKind())) {
                LOGGER.info(">>>>>" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + " ms " + proceedingJoinPoint.toShortString() + "," + Arrays.asList(proceedingJoinPoint.getArgs()));
            }
        }
    }
}
