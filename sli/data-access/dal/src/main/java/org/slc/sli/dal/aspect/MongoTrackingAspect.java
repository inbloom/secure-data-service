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
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *  Tracks calls  to mongo template and mongo driver
 *  Collects performance information.
 *  Logs slow queries
 *
 * @author dkornishev
 *
 */
@Aspect
public class MongoTrackingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(MongoTrackingAspect.class);

    private ConcurrentMap<String, Pair<AtomicLong, AtomicLong>> stats = new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>();
    private static final long SLOW_QUERY_THRESHOLD = 50;  // ms

    //@Around("call(* org.springframework.data.mongodb.core.MongoTemplate.*(..)) && !this(MongoTrackingAspect) && !within(org..*Test)")
    public Object track(ProceedingJoinPoint pjp) throws Throwable {

        MongoTemplate mt = (MongoTemplate) pjp.getTarget();

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

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long elapsed = System.currentTimeMillis() - start;

        this.upCounts(mt.getDb().getName(), pjp.getSignature().getName(), collection, elapsed);
        logSlowQuery(elapsed, mt.getDb().getName(), pjp.getSignature().getName(), collection, pjp);

        return result;
    }

    //@Around("call(* com.mongodb.DBCollection.*(..)) && !this(MongoTrackingAspect) && !within(org..*Test)")
    public Object trackDBCollection(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long elapsed = System.currentTimeMillis() - start;

        DBCollection col = (DBCollection) pjp.getTarget();

        this.upCounts(col.getDB().getName(), pjp.getSignature().getName(), col.getName(), elapsed);
        logSlowQuery(elapsed, col.getDB().getName(), pjp.getSignature().getName(), col.getName(), pjp);

        return result;
    }

    public Map<String, Pair<AtomicLong, AtomicLong>> getStats() {
        return this.stats;
    }

    public void reset() {
        this.stats = new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>();
    }

    private void upCounts(String db, String function, String collection, long elapsed) {
        stats.putIfAbsent(String.format("%s#%s#%s", db, function, collection), Pair.of(new AtomicLong(0), new AtomicLong(0)));

        Pair<AtomicLong, AtomicLong> pair = stats.get(String.format("%s#%s#%s", db, function, collection));

        pair.getLeft().incrementAndGet();
        pair.getRight().addAndGet(elapsed);
    }

    private void logSlowQuery(long elapsed, String db, String function, String collection, ProceedingJoinPoint pjp) {
        if (elapsed > SLOW_QUERY_THRESHOLD) {
            LOG.debug(String.format("Slow query: %s#%s#%s (%d ms)", db, function, collection, elapsed));
        }
    }
}
