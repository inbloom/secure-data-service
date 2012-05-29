package org.slc.sli.unit.manager;

import junit.framework.Assert;
import net.sf.ehcache.CacheManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slc.sli.manager.ApiClientManager;

/**
 * Test ApiClientManager caching methods
 *
 */
public class ApiClientManagerTest extends  ApiClientManager {
    private static CacheManager cacheManager = CacheManager.create();

    private static final String TEST_CACHE = "testcache";
    @BeforeClass
    public static void setUpClass() throws Exception {
        cacheManager.addCache(TEST_CACHE);
    }

    @Before
    public void setUp() throws Exception {
        setCacheManager(cacheManager);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        cacheManager.shutdown();
    }

    @Test
    public void testCacheByToken() {
        Integer one = 1;
        Integer two = 2;
        // put different values for different tokens
        putToCache(TEST_CACHE, "1", one);
        putToCache(TEST_CACHE, "2", two);
        // test values is what we put in
        Assert.assertEquals(one, getFromCache(TEST_CACHE, "1"));
        Assert.assertEquals(two, getFromCache(TEST_CACHE, "2"));
        // test the one we didn't put in
        Assert.assertNull(getFromCache(TEST_CACHE, "3"));
    }

    @Test
    public void testCacheByTokenByKey() {
        Integer one = 1;
        Integer two = 2;
        // put different values for the same token
        putToCache(TEST_CACHE, "1", one, one);
        putToCache(TEST_CACHE, "1", two, two);
        // test it's what we put in
        Assert.assertEquals(one, getFromCache(TEST_CACHE, "1", one));
        Assert.assertEquals(two, getFromCache(TEST_CACHE, "1", two));
        // overwrite the value for key one with value two
        putToCache(TEST_CACHE, "1", one, two);
        // test the value is two is for key one now
        Assert.assertEquals(two, getFromCache(TEST_CACHE, "1", one));
        // value for key two didn't change
        Assert.assertEquals(two, getFromCache(TEST_CACHE, "1", two));
    }

    @Test
    public void testCacheElementByTokenByKey() {
        Integer one = 1;
        String two = "2";
        //
        putToCache(TEST_CACHE, "1", one, one);
        putToCache(TEST_CACHE, "1", two, two);

        Assert.assertEquals(one, getCacheValueFromCache(TEST_CACHE, "1", one).get());
        Assert.assertEquals(two, getCacheValueFromCache(TEST_CACHE, "1", two).get());
        // check putting null values
        putToCache(TEST_CACHE, "1", one, null);
        CacheValue<Integer> myValue  = getCacheValueFromCache(TEST_CACHE, "1", one);
        Assert.assertNotNull(myValue);
        Assert.assertNull(myValue.get());
    }

    @Test
    public void testRemoveFromCache() {
        Integer one = 1;
        // test by token by key
        putToCache(TEST_CACHE, "1", one, one);
        Assert.assertEquals(one, getCacheValueFromCache(TEST_CACHE, "1", one).get());
        removeFromCache(TEST_CACHE, "1", one);
        Assert.assertNull(getCacheValueFromCache(TEST_CACHE, "1", one));
        // test by token only
        putToCache(TEST_CACHE, "1", one);
        Assert.assertEquals(one, getCacheValueFromCache(TEST_CACHE, "1").get());
        removeFromCache(TEST_CACHE, "1");
        Assert.assertNull(getCacheValueFromCache(TEST_CACHE, "1"));
    }
}
