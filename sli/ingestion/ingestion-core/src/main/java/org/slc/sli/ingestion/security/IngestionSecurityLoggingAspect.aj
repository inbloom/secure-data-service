package org.slc.sli.ingestion.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.logging.SecurityLoggingAspect;
import org.slc.sli.dal.security.SecurityEvent;

/**
 * Aspect for security logging of ingestion operations
 *
 * @author dshaw
 *
 */
@Aspect
@Component("IngestionSecurityLoggingAspect")
public class IngestionSecurityLoggingAspect extends SecurityLoggingAspect {
    
    /**
     * advises after all the public methods returning a SecurityEvent
     * @return
     */
    @AfterReturning(
            pointcut = "execution(public SecurityEvent *(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logMessage(joinPoint, (SecurityEvent)result);
    }
    
    /**
     * Logs the security call information to the logger
     * @param jp The JoinPoint
     * @param SecurityEvent SecurityEventInfo
     */
    protected void logMessage(JoinPoint jp, SecurityEvent event) {
        securityLog.info(event.write());
    }
}
