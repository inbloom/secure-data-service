package org.slc.sli.api.security.context.traversal.cache.impl;

import org.slc.sli.api.security.context.traversal.cache.SecurityCachingStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple caching(can we really call this caching??) strategy using a hashmap ;-)
 *
 * @author srupasinghe
 *
 */
@Component
public class SimpleSecurityCachingStrategy implements SecurityCachingStrategy {

    @Resource(name = "securityCache")
    private Map<String, Set<String>> securityCache;

    @Override
    public void warm(String cacheId, Set<String> ids) {
        if (securityCache.containsKey(cacheId)) {
            securityCache.get(cacheId).addAll(ids);
        } else {
            securityCache.put(cacheId, new HashSet<String>(ids));
        }
    }

    @Override
    public Set<String> retrieve(String cacheId) {
        return securityCache.get(cacheId);
    }

    @Override
    public void expire() {
        securityCache.clear();
    }

    @Override
    public boolean contains(String cacheId) {
        return securityCache.containsKey(cacheId);
    }
}
