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

import net.spy.memcached.ConnectionFactoryBuilder.Locator;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.spring.MemcachedClientFactoryBean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for caches.
 *
 * @author smelody
 *
 */
@Configuration
public class CacheConfig {

    @Value("${sli.ingestion.cache.type}")
    private String cacheType;

    /** Timeout in ms */
    @Value("${sli.ingestion.cache.opTimeout}")
    private long opTimeout;


    @Value("${sli.ingestion.cache.servers}")
    private String cacheServers;

    @Bean
    public MemcachedClientFactoryBean getMemcachedClientFactoryBean() {

        MemcachedClientFactoryBean bean = new MemcachedClientFactoryBean();
        bean.setServers(cacheServers);
        bean.setLocatorType(Locator.CONSISTENT);
        bean.setProtocol(Protocol.BINARY);
        bean.setOpTimeout(opTimeout);

        return bean;
    }

    @Bean
    public CacheProvider getCacheProvider() throws Exception {

        if ("memcached".equals(cacheType)) {

            MemcachedClientFactoryBean bean = getMemcachedClientFactoryBean();

            MemcachedCacheProvider provider = new MemcachedCacheProvider(bean);


            return provider;
        } else if ("inmemory".equals(cacheType)) {
            return new InmemoryCacheProvider();
        } else {
            return new NullCacheProvider();
        }
    }
}
