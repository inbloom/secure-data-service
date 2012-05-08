package org.slc.sli.common.util.logging;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging aspect for Security calls
 * @author dshaw
 *
 */
@Aspect
abstract public class SecurityLoggingAspect {
    
    protected Logger securityLog = LoggerFactory.getLogger("Job.SecurityMonitor");
    
}