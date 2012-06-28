package org.slc.sli.aspect;

import java.util.logging.Logger;
import java.util.logging.Level;

public privileged aspect StatsDAspect {
    
    pointcut entityMethods(): within(org.slc.sli.api.resources.v1.entity.*) && execution(public * *(..));
    pointcut assocMethods(): within(org.slc.sli.api.resources.v1.association.*) && execution(public * *(..));
    pointcut dalMethods(): within(org.slc.sli.dal.*) && execution(public * *(..));
    pointcut securityMethods(): within(org.slc.sli.api.security.*) && execution(public * *(..));
    pointcut validationMethods(): within(org.slc.sli.api.validation.*) && execution(public * *(..));
    pointcut serviceMethods(): within(org.slc.sli.api.service.*) && execution(public * *(..));
    pointcut encryptMethods(): within(org.slc.sli.common.encrypt.*) && execution(public * *(..));
    pointcut domainMethods(): within(org.slc.sli.domain.*) && execution(public * *(..));
    
    pointcut resourceMethods() : 
        cflow((entityMethods() || assocMethods() || encryptMethods()) && (serviceMethods() && dalMethods() && domainMethods() && securityMethods()));
        
    Object around() : resourceMethods() && !staticinitialization(*) && !initialization(*.new(..)) && !preinitialization(*.new(..))  && !handler(*) {
        //get the current time
        long begin = System.currentTimeMillis();
        
        //call the method 
        Object result = proceed();

        //get the current time
        long end = System.currentTimeMillis(); 
        
        //log the method information
        Logger.getLogger("StatsDAspect").log(Level.SEVERE, String.format("StatsD Info: %s: %d - %d", thisJoinPoint, begin, end));
        
        return result;        
    }

}
