/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.api.perf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.mongodb.DBCollection;

import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.MongoStat;
import org.slc.sli.dal.aspect.MongoTrackingAspect;

/**
 * Tracks calls throught the API 
 * Collects performance information.
 * Logs slow queries
 */
@Aspect
@SuppressWarnings("PMD.MoreThanOneLogger")
public class APITrackingAspect {
    
    private static final Logger LOG = LoggerFactory.getLogger(MongoTrackingAspect.class);

    @Value("${sli.api.performance.tracking}")
    private String apiCallTracking;
    
//    public void setEnabled(String enableTracking) { 
//        // apiCallTracking = Boolean.parseBoolean(enableTracking);
//        boolean x = Boolean.parseBoolean(enableTracking);
//        LOG.debug("XXXXXXXXXXXXXXXXXXXXXX  Enable value: " + x); 
//        apiCallTracking = Boolean.parseBoolean(enableTracking);
//    }

    @Autowired
    private MongoStat callTracker;
        
    /**
     * Track calls in the various implementation of the Repository interface. 
     */

    // Note: This is commented out, because it creates 1000+ point cuts, that are called even when 
    // tracking is disabled.
    // @Around("(call(* org.slc.sli.api.resources..*.*(..)) || call(* org.slc.sli.api.security..*(..)) || call(* org.slc.sli.api.service..*(..))) && !this(APITrackingAspect) && !within(org..*Test) && !within(org..*MongoPerfRepository)")
    public Object trackAPICalls(ProceedingJoinPoint pjp) throws Throwable {
        if (Boolean.valueOf(apiCallTracking)) { 
            return trackCallStartEnd(pjp);
        }
        else {
            return pjp.proceed();
        }
    }
    
    private Object trackCallStartEnd(ProceedingJoinPoint pjp) throws Throwable {
        Object result; 
        long start = System.currentTimeMillis();
        callTracker.addEvent("s", pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName(), start, null);
        result = pjp.proceed();
        long end = System.currentTimeMillis(); 

//        // capture the arguments
//        List<String> args = null; 
//        if ((end - start) > 0) {
//            Object[] callArgs = pjp.getArgs(); 
//            args = new ArrayList<String>(callArgs.length); 
//            for(Object ca : callArgs) {
//                args.add((null == ca) ? null : ca.getClass().getName() + ":" + ca.toString()); 
//            }
//        }
        // we are not keeping the signature, because it's stored in the first event
        callTracker.addEvent("e", "", end, null);
        return result;
    }

    private Object trackCallTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start; 
        if (duration > 0) { 
            callTracker.addMetric("t", pjp.getSignature().toString(), duration);
        }
        return result;
    }
}
