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
