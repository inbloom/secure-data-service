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
    
    @Around("call(* org.springframework.data.mongodb.core.MongoTemplate.*(..)) && !this(MongoTrackingAspect) && !within(org..*Test)")
    public Object track(ProceedingJoinPoint pjp) throws Throwable {
        
        MongoTemplate mt = (MongoTemplate) pjp.getTarget();
        
        String collection = "UNKNOWN";
        Object[] args = pjp.getArgs();
        if (args[0] instanceof String) {
            collection = (String) args[0];
        } else if (args.length > 1 && args[1] instanceof String) {
            collection = (String) args[1];
        } else if (args.length > 1 && args[2] instanceof String) {
            collection = (String) args[2];
        }
        
        if (collection.lastIndexOf("_") > -1) {
            collection = collection.substring(0, collection.lastIndexOf("_"));
        }
        
        String key = String.format("%s#%s#%s", mt.getDb().getName(), pjp.getSignature().getName(), collection);
        
        if (stats.get(pjp.getSignature().getName()) == null) {
            stats.put(pjp.getSignature().getName(), Pair.of(new AtomicLong(0), new AtomicLong(0)));
        }
        
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long elapsed = System.currentTimeMillis() - start;
        
        Pair<AtomicLong, AtomicLong> pair = stats.get(pjp.getSignature().getName());
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
