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

package org.slc.sli.ingestion.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author npandey
 *
 */
public class IndexJSFileParserTest {
    private static final String INDEX_FILE = "testIndexes.js";

    @Test
    public void parseJSTest() {
        IndexJSFileParser indexJSFileParser = new IndexJSFileParser();
        Set<MongoIndex> indexes = indexJSFileParser.parse(INDEX_FILE);

        Map<String, MongoIndex> expectedIndexes = new HashMap<String, MongoIndex>();

        DBObject userSessionIndex = new BasicDBObject();
        userSessionIndex.put("body.expiration", 1);
        userSessionIndex.put("body.hardLogout", 1);
        userSessionIndex.put("body.appSession.token", 1);

        expectedIndexes.put("usersession", new MongoIndex("userSession", false, userSessionIndex));

        DBObject tenantIndex = new BasicDBObject();
        tenantIndex.put("body.tenantId", 1);

        expectedIndexes.put("tenant", new MongoIndex("tenant", true, tenantIndex));

        assertEquals(4, indexes.size());

        for (MongoIndex idx : indexes) {
            if (idx.getCollection().equalsIgnoreCase("realm")) {
                fail("Invalid index was parsed");
            } else if (idx.getCollection().equalsIgnoreCase("usersession")) {
                assertEquals(idx, expectedIndexes.get("usersession"));
            } else if (idx.getCollection().equalsIgnoreCase("tenant")) {
                assertEquals(idx, expectedIndexes.get("tenant"));
                assertTrue(idx.isUnique());
            }
        }

    }

    @Ignore
    public void parseJsonTest() {
        //Map<String, Object> value = IndexFileParser.parseJson("{\"batchJobId\" : 1, \"creationTime\":1}");
        Map<String, Object> value = null;
        assertFalse(value == null);
        assertTrue(value.get("batchJobId").equals(1));
        assertTrue(value.get("creationTime").equals(1));
    }

    @Test
    public void invalidIndexTest() {
        String index1 = "#collection,false,body.tenantId:1";
        //assertFalse(IndexFileParser.validIndex(index1));

        String index2 = "true, collection:keys";
        //assertFalse(IndexFileParser.validIndex(index2));
    }

    @Ignore
    public void parseInvalidIndexTest() {
        String invalidTokensIndex = "student,false:body.tenantId:1";
        try {
            //IndexFileParser.parseIndex(invalidTokensIndex);
            fail("parseIndex() did not throw the expected exception");
        } catch (IllegalStateException ex) {
            assertEquals("Expected at least 3 tokens for index config definition: " + invalidTokensIndex, ex.getMessage());
        }

        String invalidKeysIndex = "student,true,body.tenantId:1,body.studentId:1:-1";

        try {
            //IndexFileParser.parseIndex(invalidKeysIndex);
            fail("parseIndex() did not throw the expected exception");
        } catch (IllegalStateException ex) {
            assertEquals("Unexpected index order: body.studentId:1:-1", ex.getMessage());
        }
    }

}
