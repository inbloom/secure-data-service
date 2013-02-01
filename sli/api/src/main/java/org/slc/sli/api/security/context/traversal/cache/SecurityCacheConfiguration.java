/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
