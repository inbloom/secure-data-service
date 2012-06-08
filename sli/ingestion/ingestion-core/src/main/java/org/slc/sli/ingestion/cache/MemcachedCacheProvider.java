package org.slc.sli.ingestion.cache;

import java.util.concurrent.ExecutionException;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
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

    private final static Logger LOG = LoggerFactory.getLogger(MemcachedCacheProvider.class);

    private MemcachedClient client;

    private MemcachedClientFactoryBean memcachedClientFactory;

    public MemcachedCacheProvider(MemcachedClientFactoryBean factory) throws Exception {
        memcachedClientFactory = factory;

        client = (MemcachedClient) memcachedClientFactory.getObject();

    }

    public MemcachedClient getCacheClient() {

        return client;
    }

    @Override
    public void add(String key, Object value) {

        // DON'T log any entities here - PII :)

        LOG.debug("Adding {} {}", key, value);

        OperationFuture<Boolean> status = client.set(key, 0, value);

        // Assuming we need to block until completion?
        Boolean completed = false;
        try {
            completed = status.get();
        } catch (ExecutionException ex) {
            LOG.error("Error adding key", ex);
        } catch (InterruptedException ex) {
            LOG.error("Error adding key", ex);
        }

        LOG.debug("Completed add: {}", completed);

    }

    @Override
    public Object get(String key) {
        return client.get(key);
    }
}
