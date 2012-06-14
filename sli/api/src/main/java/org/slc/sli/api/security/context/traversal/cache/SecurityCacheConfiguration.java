package org.slc.sli.api.security.context.traversal.cache;

import org.slc.sli.api.security.context.traversal.cache.impl.SimpleSecurityCachingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 * Serves up the security cache map
 * @author srupasinghe
 *
 */
@Configuration
public class SecurityCacheConfiguration {

    @Bean(name = "securityCache")
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Map<String, Set<String>> getSecurityCache() {
        Map<String, Set<String>> securityCache = new HashMap<String, Set<String>>();
        return securityCache;
    }

    @Bean(name = "securityCachingStrategy")
    public SecurityCachingStrategy getCachingStrategy() {
        return new SimpleSecurityCachingStrategy();
    }
}
