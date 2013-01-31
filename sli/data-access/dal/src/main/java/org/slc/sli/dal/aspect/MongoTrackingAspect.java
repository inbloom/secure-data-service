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

/**
 * Tracks calls to mongo template and mongo driver
 * Collects performance information.
 * Logs slow queries
 *
 * @author dkornishev
 *
 */
@Aspect
@SuppressWarnings("PMD.MoreThanOneLogger")
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

    private static boolean isEnabled() {
        return enabled;
    }

    // Map<jobId, Map<(db,function,collection), (opCount,totalElapsedMs)>>
    private ConcurrentMap<String, Pair<AtomicLong, ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>>> stats = new ConcurrentHashMap<String, Pair<AtomicLong, ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>>>();

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
            dbCallTracker.incrementHitCount();
            // dbCallTracker.addMetric("t", pjp.getSignature().toShortString(), end-start);
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
            dbCallTracker.incrementHitCount();
            // dbCallTracker.addMetric("t", pjp.getSignature().toShortString(), end-start);
        }
        return result;
    }
        
    /**
     * Track calls in the various implementation of the Repository interface. 
     */
    
    // Note: This is commented out, because it incurs overhead at runtime
    // @Around("call(* org.slc.sli.domain.Repository+.*(..)) && !this(MongoTrackingAspect) && !within(org..*Test) && !within(org..*MongoPerfRepository)")
    public Object trackDALCalls(ProceedingJoinPoint pjp) throws Throwable {
        return trackCalls(pjp); 
    }
        
    private Object trackCalls(ProceedingJoinPoint pjp) throws Throwable {

        Object result; 
        if (Boolean.valueOf(dbCallTracking)) {
            dbCallTracker.addEvent("s", pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName(), System.currentTimeMillis(), null);
            result = pjp.proceed();
            long end = System.currentTimeMillis(); 
            // capture the arguments
            Object[] callArgs = pjp.getArgs(); 
            List<String> args = new ArrayList<String>(callArgs.length); 
            for(Object ca : callArgs) {
                args.add((null == ca) ? null : ca.getClass().getName() + ":" + ca.toString()); 
            }
            dbCallTracker.addEvent("e", "", end, args);
        }
        else {
            result = pjp.proceed(); 
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
            long trackingInt = Long.valueOf(trackingInterval) * 1000;
            if (trackingInt <= 0) {
                trackingInt = 1000;
            }

            long jobBegin = start;
            long startInt = start;

            // Init map for job.
            if (stats.get(jobId) == null) {
                ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>> jobStats = new ConcurrentHashMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>();
                Pair<AtomicLong, ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>> jobStatsPair = Pair
                        .of(new AtomicLong(start), jobStats);

                stats.putIfAbsent(jobId, jobStatsPair);

            } else if (stats.get(jobId).getLeft().get() == 0) {
                stats.get(jobId).getLeft().set(start);
            } else {
                jobBegin = stats.get(jobId).getLeft().get();
                startInt = ((stats.get(jobId).getRight().size() - 1) * trackingInt) + jobBegin;
            }

            // Init map for intervals.
            // FIXME: Calendar and SimpleDateFormat objects are expensive to create.
            // Can we just use epoch time from System.currentTimeMillis()
            String currJobInterval = null;
            long newInt = (((start - jobBegin) / trackingInt) * trackingInt) + jobBegin;
            if (newInt < startInt) {
                startInt = newInt;
            }
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            while (newInt >= startInt) {
                long endInt = startInt + trackingInt;
                calendar.setTimeInMillis(startInt);
                String startTimeStamp = formatter.format(calendar.getTime());
                calendar.setTimeInMillis(endInt);
                String endTimeStamp = formatter.format(calendar.getTime());
                currJobInterval = String.format("%s - %s", startTimeStamp, endTimeStamp);
                stats.get(jobId).getRight()
                        .putIfAbsent(currJobInterval, new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>());
                startInt = endInt;
            }

            // Init map for stats.
            String collectionKeyName = collection.replaceAll("\\.", "DOT");
            String statsKey = String.format("%s#%s#%s", db, function, collectionKeyName);
            stats.get(jobId).getRight().get(currJobInterval)
                    .putIfAbsent(statsKey, Pair.of(new AtomicLong(0), new AtomicLong(0)));

            // Increment.
            Pair<AtomicLong, AtomicLong> pair = stats.get(jobId).getRight().get(currJobInterval).get(statsKey);
            pair.getLeft().incrementAndGet();
            pair.getRight().addAndGet(elapsed);

            PERF_LOG.info(function + "," + db + "," + collectionKeyName + "," + elapsed);
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
        if ((jobId != null) && (this.stats.get(jobId) != null)) {
            return this.stats.get(jobId).getRight();
        }
        return null;
    }

    public void reset() {
        String jobId = TenantContext.getJobId();
        if (jobId != null) {
            ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>> jobStats = new ConcurrentHashMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>();
            Pair<AtomicLong, ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>> jobStatsPair = Pair
                    .of(new AtomicLong(0), jobStats);
            this.stats.put(jobId, jobStatsPair);
        }
        LOG.info("Mongo tracking stats are now cleared for job {}.", jobId);
    }

}
