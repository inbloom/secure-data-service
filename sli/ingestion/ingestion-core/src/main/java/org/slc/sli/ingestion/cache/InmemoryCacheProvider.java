package org.slc.sli.ingestion.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * HashMap cache for dev testing.
 *
 */
public class InmemoryCacheProvider implements CacheProvider {

    private Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

    @Override
    public void add(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

}
