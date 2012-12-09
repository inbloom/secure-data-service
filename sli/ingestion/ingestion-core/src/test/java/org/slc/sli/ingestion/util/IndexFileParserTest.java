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

/**
 *
 * @author npandey
 *
 */
public class IndexFileParserTest {

    private static final String INDEX_FILE = "testIndexes.js";

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

}
