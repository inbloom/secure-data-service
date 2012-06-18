package org.slc.sli.api.security.context.traversal.cache;

import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Common interface for security caching strategies
 * @author srupasinghe
 *
 */
@Component
public interface SecurityCachingStrategy {
    public void warm(String cacheId, Set<String> ids);

    public Set<String> retrieve(String cacheId);

    public void expire();

    public boolean contains(String cacheId);

}
