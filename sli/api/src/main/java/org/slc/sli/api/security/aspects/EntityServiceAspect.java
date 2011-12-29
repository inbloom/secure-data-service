package org.slc.sli.api.security.aspects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.After;       // remove when switch to Around
import org.aspectj.lang.annotation.Before;      // remove when switch to Around
//import org.aspectj.lang.annotation.Around;    // uncomment when switch to Around

@Aspect
public class EntityServiceAspect {
    private static final Logger ASPECT_LOG = LoggerFactory.getLogger(EntityServiceAspect.class);
    @Before("call(* org.slc.sli.api.service.EntityService.get(..))")
    public void logBefore() {
        ASPECT_LOG.debug("[iii] entered get() function of EntityService");
    }
    
    @After("call(* org.slc.sli.api.service.EntityService.get(..))")
    public void logAfter() {
        ASPECT_LOG.error("[iii] exiting get() function of EntityService");
    }
}
