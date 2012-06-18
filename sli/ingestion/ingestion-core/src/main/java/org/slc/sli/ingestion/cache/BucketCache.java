package org.slc.sli.ingestion.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

/**
 * A simplewrapper around a cache provider that allows for "buckets" of data within cache.
 *
 * @author dduran
 *
 */
@Component
public class BucketCache {

    private ConcurrentMap<String, CacheProvider> bucketCache = new ConcurrentHashMap<String, CacheProvider>();

    public void addToBucket(String bucket, String key, Object value) {
        CacheProvider cacheProvider = bucketCache.get(bucket);
        if (cacheProvider == null) {
            bucketCache.putIfAbsent(bucket, new InmemoryCacheProvider());
            cacheProvider = bucketCache.get(bucket);
        }
        cacheProvider.add(key, value);
    }

    public Object getFromBucket(String bucket, String key) {
        CacheProvider cacheProvider = bucketCache.get(bucket);
        if (cacheProvider != null) {
            return cacheProvider.get(key);
        }
        return null;
    }

    public void flushAllBuckets() {
        for (String key : bucketCache.keySet()) {
            bucketCache.remove(key);
        }
    }

    public void flushBucket(String bucket) {
        if (bucket != null) {
            bucketCache.remove(bucket);
        }
    }
}
