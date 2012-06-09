package org.slc.sli.ingestion.cache;
/**
 * Null cache
 *
 * @author smelody
 *
 */
public class NullCacheProvider implements CacheProvider {

    @Override
    public void add(String key, Object value) {

    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub

    }

}
