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


package org.slc.sli.api.security.context.traversal.cache.impl;

import org.slc.sli.api.security.context.traversal.cache.SecurityCachingStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
            securityCache.put(cacheId, ids);
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
