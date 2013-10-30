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
package org.slc.sli.bulk.extract.lea;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @author: npandey
 */
public class EntityToEdOrgDateCacheTest {
    private EntityToEdOrgDateCache cacheObject;
    private static final DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");

    String testEntity;
    String testEdOrg;
    DateTime testExpirationDate;

    @Before
    public void setUp() {
        cacheObject = new EntityToEdOrgDateCache();
        testEntity = "Student1";
        testEdOrg = "EdOrg1";
        testExpirationDate = DateTime.parse("2010-07-20", FMT);
    }

    @Test
    public void testInsertAddEntryToEmptyCache() {
        cacheObject.addEntry(testEntity, testEdOrg, testExpirationDate);

        Map<String, DateTime> result = cacheObject.getEntriesById(testEntity);

        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.keySet().contains(testEdOrg));
        Assert.assertEquals(testExpirationDate, result.get(testEdOrg));
    }

    @Test
    public void testInsertSameEntityDifferentEdOrg() {
        cacheObject.addEntry(testEntity, testEdOrg, testExpirationDate);
        DateTime expDate = DateTime.parse("2011-07-30", FMT);
        cacheObject.addEntry(testEntity, "EdOrg2", expDate);

        Map<String, DateTime> result = cacheObject.getEntriesById(testEntity);

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.keySet().contains(testEdOrg));
        Assert.assertTrue(result.keySet().contains("EdOrg2"));
        Assert.assertEquals(testExpirationDate, result.get(testEdOrg));
        Assert.assertEquals(expDate, result.get("EdOrg2"));
    }

    @Test
    public void testInsertDifferentEntitySameEdOrg() {
        cacheObject.addEntry(testEntity, testEdOrg, testExpirationDate);
        DateTime expDate = DateTime.parse("2013-07-10", FMT);
        cacheObject.addEntry("Student2", testEdOrg, expDate);

        Map<String, DateTime> result = cacheObject.getEntriesById("Student2");

        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.keySet().contains(testEdOrg));
        Assert.assertEquals(expDate, result.get(testEdOrg));

        result = cacheObject.getEntriesById(testEntity);

        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.keySet().contains(testEdOrg));
        Assert.assertEquals(testExpirationDate, result.get(testEdOrg));
    }

    @Test
    public void testInsertSameEdOrgAfterDate() {
        cacheObject.addEntry(testEntity, testEdOrg, testExpirationDate);
        DateTime expDate = DateTime.parse("2011-07-30", FMT);
        cacheObject.addEntry(testEntity, testEdOrg, expDate);

        Map<String, DateTime> result = cacheObject.getEntriesById(testEntity);

        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.keySet().contains(testEdOrg));
        Assert.assertEquals(expDate, result.get(testEdOrg));
    }

    @Test
    public void testInsertSameEdOrgBeforeDate() {
        cacheObject.addEntry(testEntity, testEdOrg, testExpirationDate);
        DateTime expDate = DateTime.parse("2009-07-14", FMT);
        cacheObject.addEntry(testEntity, testEdOrg, expDate);

        Map<String, DateTime> result = cacheObject.getEntriesById(testEntity);

        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.keySet().contains(testEdOrg));
        Assert.assertEquals(testExpirationDate, result.get(testEdOrg));
    }
}
