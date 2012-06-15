package org.slc.sli.ingestion.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A simplewrapper around a cache provider that allows for "buckets" of data within cache.
 *
 * @author dduran
 *
 */
@Component
public class BucketCache {

    @Autowired
    private CacheProvider cacheProvider;

    public void add(String bucket, String key, Object value) {
        cacheProvider.add("##" + bucket + "##" + key, value);
    }

    public Object get(String bucket, String key) {
        return cacheProvider.get("##" + bucket + "##" + key);
    }

    public void flush() {
        cacheProvider.flush();
    }
}
