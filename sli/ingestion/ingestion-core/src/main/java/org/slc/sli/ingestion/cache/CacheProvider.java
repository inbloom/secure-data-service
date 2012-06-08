package org.slc.sli.ingestion.cache;

/**
 * Interface that provides caching services.
 *
 * @author smelody
 *
 */
public interface CacheProvider {

    public void add( String key, Object value );

    public Object get ( String key ) ;
}
