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


package org.slc.sli.ingestion.cache;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;
import net.spy.memcached.spring.MemcachedClientFactoryBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memcached backed provider of cache services.
 *
 * @author smelody
 *
 */

public class MemcachedCacheProvider implements CacheProvider {

    private static final Logger LOG = LoggerFactory.getLogger(MemcachedCacheProvider.class);

    private MemcachedClient client;

    private MemcachedClientFactoryBean memcachedClientFactory;

    public MemcachedCacheProvider(MemcachedClientFactoryBean factory) throws Exception {
        memcachedClientFactory = factory;

        client = (MemcachedClient) memcachedClientFactory.getObject();

    }

    public MemcachedClient getCacheClient() {

        return client;
    }

    /**
     * Asynchronously sets the value of this key in the memcached instance.
     */
    @Override
    public void add(String key, Object value) {

        // DON'T log any entities here - PII :)

        LOG.debug("Adding {} {}", key, value);

        client.set(key, 0, value);

    }

    @Override
    public Object get(String key) {
        Object val = null;
        try {
            val = client.get(key);

            LOG.debug("Memcached {} for {} ", val == null ? "MISS" : "HIT", key);
            if (val != null) {

                LOG.debug("Found {} for key {}", val, key);
            }
        } catch (OperationTimeoutException ex) {
            LOG.warn("Operation timed out - is memcached responding? ", ex);
        }
        return val;
    }

    @Override
    public void flush() {

        client.flush();

    }
}
