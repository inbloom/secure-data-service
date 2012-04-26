package org.slc.sli.util;

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
    private Logger logger = LoggerFactory.getLogger(getClass());
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
                logger.info(">>>>>" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + " ms " + proceedingJoinPoint.toShortString() + "," + Arrays.asList(proceedingJoinPoint.getArgs()));
            }
        }
    }
}
