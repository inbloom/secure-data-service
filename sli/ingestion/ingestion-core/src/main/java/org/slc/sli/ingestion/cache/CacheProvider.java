package org.slc.sli.ingestion.cache;

/**
 * Interface that provides caching services.
 *
 * @author smelody
 *
 */
public interface CacheProvider {

    void add(String key, Object value);

    Object get(String key);

    void flush();
}
