package org.slc.sli.manager;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slc.sli.client.APIClient;
import org.slc.sli.util.SecurityUtil;

/**
 *
 * Superclass for manager classes.
 *
 * @author dwu
 *
 */
public abstract class ApiClientManager implements Manager {

    private CacheManager cacheManager;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Cache value class that wraps the actual value and can act as a marker for cached NULL value if needed
     * @author agrebneva
     *
     * @param <T>
     */
    protected static class CacheValue<T> {
        T value;

        CacheValue(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }
    }
    /**
     * Cache key representation for internal managers. Always include token in the key since
     * received data is user-centric
     * @author agrebneva
     *
     */
    protected static class CacheKey implements Serializable {
        private static final long serialVersionUID = 1L;
        String token;
        Object key;

        CacheKey(String token, Object key) {
            this.token = token;
            this.key = key;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = prime + ((token == null) ? 0 : token.hashCode());
            if (key != null) {
                result = prime * result + ((key == null) ? 0 : key.hashCode());
            }
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            if (key == null) {
                if (other.key != null) {
                    return false;
                }
            } else if (!key.equals(other.key)) {
                return false;
            }
            if (token == null) {
                if (other.token != null) {
                    return false;
                }
            } else if (!token.equals(other.token)) {
                return false;
            }
            return true;
        }
    }

    private APIClient apiClient;

    public APIClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(APIClient apiClient) {
        this.apiClient = apiClient;
    }

    public static final String DUMMY_EDORG_NAME = "No Ed-Org";

    protected String getToken() {
        return SecurityUtil.getToken();
    }

    protected <T> CacheValue<T> getCacheValueFromCache(String cacheName, String token) {
        return getCacheValueFromCache(cacheName, token, null);
    }

    /**
     * Get object from cache by user token if cacheManager is configured. The object is a wrapper so in case when the object
     * is in cache but NULL, the object returned will be not NULL, wrapping a NULL object - to avoid a re-query of NULL objects.
     * If object is not in cache or cache is not configured, a null will be returned.
     * @param cacheName - cache name
     * @param token - user token
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T> CacheValue<T> getCacheValueFromCache(String cacheName, String token, Object key) {
        if (cacheManager != null) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Element elem = cache.get(new CacheKey(token, key));
                if (elem != null) {
                    return new CacheValue<T>((T) elem.getObjectValue());
                }
            }
        }
        return null;
    }

    protected <T> T getFromCache(String cacheName, String token) {
        return getFromCache(cacheName, token, null);
    }

    protected <T> T getFromCache(String cacheName, String token, Object key) {
        CacheValue<T> value = getCacheValueFromCache(cacheName, token, key);
        return value == null ? null : value.get();
    }

    protected <T> void putToCache(String cacheName, String token, T value) {
        putToCache(cacheName, token, null, value);
    }

    /**
     * Put cached object by user token if cacheManager is configured
     * @param cacheName - cache name
     * @param token - user token
     * @param key - object key
     * @param value - value to cache
     */
    protected <T> void putToCache(String cacheName, String token, Object key, T value) {
        if (cacheManager != null) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.put(new Element(new CacheKey(token, key), value));
            }
        }
    }

    protected void removeFromCache(String cacheName, String token) {
        removeFromCache(cacheName, token, null);
    }

    /**
     * remove object from cache
     * @param cacheName
     * @param token
     */
    protected void removeFromCache(String cacheName, String token, Object key) {
        if (cacheManager != null) {
            cacheManager.getCache(cacheName).remove(new CacheKey(token, key));
        }
    }
}
