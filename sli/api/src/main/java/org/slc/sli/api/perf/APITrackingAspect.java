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
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.weaver.ast.Test;
import org.slc.sli.dal.repository.MongoPerfRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.MongoStat;
import org.slc.sli.dal.aspect.MongoTrackingAspect;

/**
 * Tracks calls throughout the API
 * Collects performance information.
 * Logs slow queries
 */
@Aspect
@SuppressWarnings("PMD.MoreThanOneLogger")
public class APITrackingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(MongoTrackingAspect.class);

    @Value("${sli.api.performance.tracking}")
    private String apiCallTracking;

    @Autowired
    private MongoStat callTracker;

    /**
     * Track calls in the various implementation of the Repository interface.
     */
    private List<String> getArgs(JoinPoint pjp) {
        List<String> args = null;
        Object[] callArgs = pjp.getArgs();
        args = new ArrayList<String>(callArgs.length);
        for(Object ca : callArgs) {
            args.add((null == ca) ? null : ca.getClass().getName() + ":" + ca.toString());
        }
        return args;
    }

    // !call(* java.util..*.*(..))
    @After("call(* *..*.*(..)) && !call(* java.lang..*.*(..)) && !call(* java.util..*.*(..)) && !call(* org.slc.sli.dal.MongoStat(..)) && !within(org.slc..*.APITrackingAspect) && !within(org..*Test) && !within(org..*MongoPerfRepository)")
    public void trackAPIEnd(JoinPoint pjp) throws Throwable {
        if (Boolean.valueOf(apiCallTracking)) {
            callTracker.addEvent("e", pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName(), System.nanoTime(), null);
        }
    }

    @Before("call(* *..*.*(..)) && !call(* java.lang..*.*(..)) && !call(* java.util..*.*(..)) && !call(* org.slc.sli.dal.MongoStat(..)) && !this(org.slc.sli.api.perf.APITrackingAspect) && !within(org.slc..*.APITrackingAspect) && !within(org..*Test) && !within(org..*MongoPerfRepository)")
    public void trackAPIStart(JoinPoint pjp) throws Throwable {
        if (Boolean.valueOf(apiCallTracking)) {
            List<String> args = getArgs(pjp);
            callTracker.addEvent("s", pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName(), System.nanoTime(), args);
        }
    }
}
