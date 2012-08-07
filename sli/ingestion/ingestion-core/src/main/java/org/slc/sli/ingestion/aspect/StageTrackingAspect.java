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

package org.slc.sli.ingestion.aspect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.dal.TenantContext;

/**
 * Track elapsed time spent executing specific ingestion methods and the number of times executed.
 *
 * @author dduran
 *
 */
@Aspect
public class StageTrackingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(StageTrackingAspect.class);

    // Map<jobId, Map<stage, (opCount,totalElapsedMs)>>
    private ConcurrentMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>> stats = new ConcurrentHashMap<String, ConcurrentMap<String, Pair<AtomicLong, AtomicLong>>>();

    @Around("call(public * org.slc.sli.ingestion.transformation.normalization.IdNormalizer.*(..)) && !within(org.slc.sli.ingestion.transformation.normalization.IdNormalizer) && !within(org..*Test)")
    public Object trackIdNormalizer(ProceedingJoinPoint pjp) throws Throwable {

        return proceedAndTrackCall(pjp);
    }

    private Object proceedAndTrackCall(ProceedingJoinPoint pjp) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long elapsed = System.currentTimeMillis() - start;

        // class name (sans package) # method name. e.g. "IdNormalizer#resolveInternalIds"
        String statsKey = pjp.getStaticPart().getSignature().getDeclaringType().getSimpleName() + "#"
                + pjp.getStaticPart().getSignature().getName();

        trackCallStatistics(statsKey, elapsed);

        return result;
    }

    private void trackCallStatistics(String statsKey, long elapsed) {
        String jobId = TenantContext.getJobId();
        if (jobId != null) {

            ConcurrentMap<String, Pair<AtomicLong, AtomicLong>> statsForJob = stats.get(jobId);
            if (statsForJob == null) {
                stats.putIfAbsent(jobId, new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>());
                statsForJob = stats.get(jobId);
            }

            Pair<AtomicLong, AtomicLong> pair = statsForJob.get(statsKey);
            if (pair == null) {
                statsForJob.putIfAbsent(statsKey, Pair.of(new AtomicLong(0L), new AtomicLong(0L)));
                pair = statsForJob.get(statsKey);
            }

            // increment
            pair.getLeft().incrementAndGet();
            pair.getRight().addAndGet(elapsed);
        }
    }

    public Map<String, Pair<AtomicLong, AtomicLong>> getStats() {
        String jobId = TenantContext.getJobId();
        if (jobId != null) {
            return stats.get(TenantContext.getJobId());
        }
        return null;
    }

    public void reset() {
        String jobId = TenantContext.getJobId();
        if (jobId != null) {
            stats.put(jobId, new ConcurrentHashMap<String, Pair<AtomicLong, AtomicLong>>());
        }
        LOG.info("Stage tracking stats are now cleared for job {}.", jobId);
    }

}
