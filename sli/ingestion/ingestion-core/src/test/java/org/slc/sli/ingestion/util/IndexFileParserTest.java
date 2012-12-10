/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author npandey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class IndexFileParserTest {

    private static final String INDEX_FILE = "testIndexes.js";
    private static final String INDEX_TXT_FILE = "testIndexes.txt";

    @Test
    public void parseJSTest() {
        List<MongoIndex> indexes = IndexFileParser.parseJSFile(INDEX_FILE);
        MongoIndex index;

        assertEquals(4, indexes.size());

        index = indexes.get(2);
        assertTrue(index.isUnique());

        index = indexes.get(3);
        assertEquals(3, index.getKeys().toMap().size());

        for (MongoIndex idx : indexes) {
            if (idx.getCollection().equalsIgnoreCase("realm")) {
                fail("Invalid index was parsed");
            }
        }
    }

    @Test
    public void parseJsonTest() {
        Map<String, Object> value = IndexFileParser.parseJson("{\"batchJobId\" : 1, \"creationTime\":1}");
        assertFalse(value == null);
        assertTrue(value.get("batchJobId").equals(1));
        assertTrue(value.get("creationTime").equals(1));
    }

    @Test
    public void parseTxtTest() {
        List<MongoIndex> indexes = IndexFileParser.parseTxtFile(INDEX_TXT_FILE);
        MongoIndex index;

        assertEquals(3, indexes.size());

        index = indexes.get(1);
        assertTrue(index.isUnique());

        index = indexes.get(2);
        assertEquals(4, index.getKeys().toMap().size());
    }

    @Test
    public void invalidIndexTest() {
        String index1 = "#collection,false,body.tenantId:1";
        assertFalse(IndexFileParser.validIndex(index1));

        String index2 = "true, collection:keys";
        assertFalse(IndexFileParser.validIndex(index2));
    }

    @Test
    public void parseInvalidIndexTest() {
        String invalidTokensIndex = "student,false:body.tenantId:1";
        try {
            IndexFileParser.parseIndex(invalidTokensIndex);
            fail("parseIndex() did not throw the expected exception");
        } catch(IllegalStateException ex) {
            assertEquals("Expected at least 3 tokens for index config definition: "+invalidTokensIndex, ex.getMessage());
        }

        String invalidKeysIndex = "student,true,body.tenantId:1,body.studentId:1:-1";

        try {
            IndexFileParser.parseIndex(invalidKeysIndex);
            fail("parseIndex() did not throw the expected exception");
        } catch(IllegalStateException ex) {
            assertEquals("Unexpected index order: body.studentId:1:-1", ex.getMessage());
        }
    }

}
