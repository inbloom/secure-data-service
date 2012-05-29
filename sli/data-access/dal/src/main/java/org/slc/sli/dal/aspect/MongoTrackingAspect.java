package org.slc.sli.dal.aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * 
 * @author dkornishev
 * 
 */
@Aspect
public class MongoTrackingAspect {
    
    private Map<String, Pair<AtomicLong, AtomicLong>> stats = new HashMap<String, Pair<AtomicLong, AtomicLong>>();
    
    @Around("call(* org.springframework.data.mongodb.core.MongoTemplate.*(..)) && !this(MongoTrackingAspect)")
    public Object track(ProceedingJoinPoint pjp) throws Throwable {
        
        MongoTemplate mt = (MongoTemplate) pjp.getTarget();
        
        String key = mt.getDb().getName() + "#" + pjp.getSignature().getName();
        
        if (stats.get(pjp.getSignature().getName()) == null) {
            stats.put(key, Pair.of(new AtomicLong(0), new AtomicLong(0)));
        }
        
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long elapsed = System.currentTimeMillis() - start;
        
        Pair<AtomicLong, AtomicLong> pair = stats.get(key);
        pair.getLeft().incrementAndGet();
        pair.getRight().addAndGet(elapsed);
        
        return result;
    }
    
    public Map<String, Pair<AtomicLong, AtomicLong>> getStats() {
        return this.stats;
    }
    
    public void reset() {
        this.stats = new HashMap<String, Pair<AtomicLong, AtomicLong>>();
    }
}
