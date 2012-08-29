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

package org.slc.sli.dal.aspect;

import java.util.Map;
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

import org.slc.sli.dal.MongoStat;
import org.slc.sli.dal.TenantContext;

/**
 * Tracks calls to mongo template and mongo driver
 * Collects performance information.
 * Logs slow queries
 *
 * @author dkornishev
 *
 */
@Aspect
public class MongoTrackingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(MongoTrackingAspect.class);

    private static final Logger PERF_LOG = LoggerFactory.getLogger("MongoPerformance");

    private static final long SLOW_QUERY_THRESHOLD = 50;  // ms

    private static boolean enabled = false;

    @Value("${sli.api.performance.tracking}")
    private String dbCallTracking;

    @Value("${sli.mongo.tracking}")
    public void setEnabledConfig(String enabledConfig) {
        setEnabled(enabledConfig);
    }

    @Autowired
    private MongoStat dbCallTracker;

    @SuppressWarnings("boxing")
    private static void setEnabled(String enabledConfig) {
        enabled = Boolean.valueOf(enabledConfig);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    // Map<jobId, Map<(db,function,collection), (opCount,totalElapsedMs)>>
    private ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>> stats = new ConcurrentHashMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>();

    @Around("call(* org.springframework.data.mongodb.core.MongoTemplate.*(..)) && !this(MongoTrackingAspect) && !within(org..*Test) && !within(org..*MongoPerfRepository)")
    public Object track(ProceedingJoinPoint pjp) throws Throwable {

        if (isEnabled()) {
        MongoTemplate mt = (MongoTemplate) pjp.getTarget();

        String collection = determineCollectionName(pjp);

        proceedAndTrack(pjp, mt.getDb().getName(), pjp.getSignature().getName(), collection);
        }
        if (Boolean.valueOf(dbCallTracking)) {
           dbCallTracker.increamentHitCount();
        }

        return pjp.proceed();
    }

    @Around("call(* com.mongodb.DBCollection.*(..)) && !this(MongoTrackingAspect) && !within(org..*Test) && !within(org..*MongoPerfRepository)")
    public Object trackDBCollection(ProceedingJoinPoint pjp) throws Throwable {

        if (isEnabled()) {
        DBCollection col = (DBCollection) pjp.getTarget();

        proceedAndTrack(pjp, col.getDB().getName(), pjp.getSignature().getName(), col.getName());
        }
        if (Boolean.valueOf(dbCallTracking)) {
            dbCallTracker.increamentHitCount();
        }
        return pjp.proceed();
    }

    private void  proceedAndTrack(ProceedingJoinPoint pjp, String db, String function, String collection)
            throws Throwable {

        long start = System.currentTimeMillis();
        long elapsed = System.currentTimeMillis() - start;

        trackCallStatistics(db, function, collection, elapsed);

        logSlowQuery(elapsed, db, function, collection, pjp);

    }

    private void trackCallStatistics(String db, String function, String collection, long elapsed) {
        String jobId = TenantContext.getJobId();
        if (jobId != null) {

            // init map for job
            stats.putIfAbsent(jobId, new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>());

            // init map for stats
            collection = collection.replaceAll("\\.", "DOT");
            String statsKey = String.format("%s#%s#%s", db, function, collection);
            stats.get(jobId).putIfAbsent(statsKey, Pair.of(new AtomicLong(0), new AtomicLong(0)));

            // increment
            Pair<AtomicLong, AtomicLong> pair = stats.get(jobId).get(statsKey);
            pair.getLeft().incrementAndGet();
            pair.getRight().addAndGet(elapsed);

            PERF_LOG.info(function + "," + db + "," + collection + "," + elapsed);
        }
    }

    private void logSlowQuery(long elapsed, String db, String function, String collection, ProceedingJoinPoint pjp) {
        if (elapsed > SLOW_QUERY_THRESHOLD) {
            LOG.debug(String.format("Slow query: %s#%s#%s (%d ms)", db, function, collection, elapsed));
        }
    }

    private String determineCollectionName(ProceedingJoinPoint pjp) {
        String collection = "UNKNOWN";
        Object[] args = pjp.getArgs();
        if (args.length > 0 && args[0] instanceof String) {
            collection = (String) args[0];
        } else if (args.length > 1 && args[1] instanceof String) {
            collection = (String) args[1];
        } else if (args.length > 2 && args[2] instanceof String) {
            collection = (String) args[2];
        }

        if (collection.lastIndexOf("_") > -1) {
            collection = collection.substring(0, collection.lastIndexOf("_"));
        }

        if (pjp.getSignature().getName().equals("executeCommand")) {
            collection = "EXEC-UNKNOWN";
        }
        return collection;
    }

    public Map<String, Pair<AtomicLong, AtomicLong>> getStats() {
        String jobId = TenantContext.getJobId();
        if (jobId != null) {
            return this.stats.get(TenantContext.getJobId());
        }
        return null;
    }

    public void reset() {
        String jobId = TenantContext.getJobId();
        if (jobId != null) {
            this.stats.put(jobId, new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>());
        }
        LOG.info("Mongo tracking stats are now cleared for job {}.", jobId);
    }

}
