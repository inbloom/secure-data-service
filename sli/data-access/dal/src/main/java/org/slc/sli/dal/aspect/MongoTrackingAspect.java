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

    @Value("${sli.mongo.tracking.interval.seconds}")
    private String trackingInterval;

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
    private ConcurrentMap<String, Pair<AtomicLong, ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>>> stats =
            new ConcurrentHashMap<String, Pair<AtomicLong, ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>>>();

    @Around("call(* org.springframework.data.mongodb.core.MongoTemplate.*(..)) && !this(MongoTrackingAspect) && !within(org..*Test) && !within(org..*MongoPerfRepository)")
    public Object track(ProceedingJoinPoint pjp) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long end = System.currentTimeMillis();

        if (isEnabled()) {
            MongoTemplate mt = (MongoTemplate) pjp.getTarget();
            String collection = determineCollectionName(pjp);
            proceedAndTrack(pjp, mt.getDb().getName(), pjp.getSignature().getName(), collection, start, end);
        }
        if (Boolean.valueOf(dbCallTracking)) {
            dbCallTracker.increamentHitCount();
        }

        return result;
    }

    @Around("call(* com.mongodb.DBCollection.*(..)) && !this(MongoTrackingAspect) && !within(org..*Test) && !within(org..*MongoPerfRepository)")
    public Object trackDBCollection(ProceedingJoinPoint pjp) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long end = System.currentTimeMillis();

        if (isEnabled()) {
            DBCollection col = (DBCollection) pjp.getTarget();
            proceedAndTrack(pjp, col.getDB().getName(), pjp.getSignature().getName(), col.getName(), start, end);
        }
        if (Boolean.valueOf(dbCallTracking)) {
            dbCallTracker.increamentHitCount();
        }
        return result;
    }

    private void proceedAndTrack(ProceedingJoinPoint pjp, String db, String function, String collection, long start,
            long end) throws Throwable {
        long elapsed = end - start;
        trackCallStatistics(db, function, collection, start, elapsed);

        logSlowQuery(elapsed, db, function, collection, pjp);

    }

    private void trackCallStatistics(String db, String function, String collection, long start, long elapsed) {
        String jobId = TenantContext.getJobId();
        if (jobId != null) {

            // Init map for job.
            Pair<AtomicLong, ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>> jobStatsPair = stats
                    .get(jobId);
            long startInt = 0;
            long trackingInt = Long.valueOf(trackingInterval);
            if (trackingInt <= 0) {
                trackingInt = 1;
            }
            if (jobStatsPair == null) {
                ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>> jobStats = new ConcurrentHashMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>();
                jobStatsPair = Pair.of(new AtomicLong(start), jobStats);
                stats.put(jobId, jobStatsPair);
            } else if (stats.get(jobId).getLeft().get() == 0) {
                stats.get(jobId).getLeft().set(start);
            } else {
                startInt = (stats.get(jobId).getRight().size() - 1) * trackingInt;
            }

            // Init map for intervals.
            long endInt = startInt + trackingInt;
            String currJobInterval = String.format("%ss - %ss", String.valueOf(startInt), String.valueOf(endInt));
            stats.get(jobId).getRight()
                .putIfAbsent(currJobInterval, new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>());
            long newInt = ((start - stats.get(jobId).getLeft().get()) / (trackingInt * 1000)) * trackingInt;
            while (newInt > startInt) {
                startInt += trackingInt;
                endInt = startInt + trackingInt;
                currJobInterval = String.format("%ss - %ss", String.valueOf(startInt), String.valueOf(endInt));
                stats.get(jobId).getRight()
                        .putIfAbsent(currJobInterval, new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>());
            }

            // Init map for stats.
            collection = collection.replaceAll("\\.", "DOT");
            String statsKey = String.format("%s#%s#%s", db, function, collection);
            stats.get(jobId).getRight().get(currJobInterval)
                    .putIfAbsent(statsKey, Pair.of(new AtomicLong(0), new AtomicLong(0)));

            // Increment.
            Pair<AtomicLong, AtomicLong> pair = stats.get(jobId).getRight().get(currJobInterval).get(statsKey);
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

    public Map<String, ? extends Map<String, Pair<AtomicLong, AtomicLong>>> getStats() {
        String jobId = TenantContext.getJobId();
        if (jobId != null) {
            return this.stats.get(jobId).getRight();
        }
        return null;
    }

    public void reset() {
        String jobId = TenantContext.getJobId();
        if (jobId != null) {
            ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>> jobStats =
                    new ConcurrentHashMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>();
            Pair<AtomicLong, ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>> jobStatsPair =
                    Pair.of(new AtomicLong(0), jobStats);
            this.stats.put(jobId, jobStatsPair);
        }
        LOG.info("Mongo tracking stats are now cleared for job {}.", jobId);
    }

}
