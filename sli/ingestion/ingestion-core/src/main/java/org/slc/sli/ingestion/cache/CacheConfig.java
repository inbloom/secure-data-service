package org.slc.sli.ingestion.cache;

import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.spring.MemcachedClientFactoryBean;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${sli.ingestion.cache.servers}")
    private String cacheServers;

    @Autowired
    public MemcachedClientFactoryBean getMemcachedClientFactoryBean() {

        MemcachedClientFactoryBean bean = new MemcachedClientFactoryBean();
        bean.setServers(cacheServers);
        bean.setProtocol(Protocol.BINARY);

        return bean;
    }

    @Bean
    public CacheProvider getCacheProvider() throws Exception {

        if ("memcached".equals(cacheType)) {

            MemcachedClientFactoryBean bean = getMemcachedClientFactoryBean();

            MemcachedCacheProvider provider = new MemcachedCacheProvider(bean);
            return provider;
        } else {
            return new InmemoryCacheProvider();
        }
    }
}
