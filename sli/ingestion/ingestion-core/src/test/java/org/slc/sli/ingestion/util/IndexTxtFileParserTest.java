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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.junit.Test;

/**
 * @author tke
 *
 */
public class IndexTxtFileParserTest {

    private static final String INDEX_TXT_FILE = "testIndexes.txt";

    @Test
    public void parseTxtTest() {
        IndexTxtFileParser indexTxtFileParser = new IndexTxtFileParser();
        Set<MongoIndex> indexes = indexTxtFileParser.parse(INDEX_TXT_FILE);

        assertEquals(3, indexes.size());

        Map<String, MongoIndex> expectedIndex = new HashMap<String, MongoIndex>();

        DBObject adminDelegationIndex = new BasicDBObject();
        adminDelegationIndex.put("body.localEdOrgId", 1);
        adminDelegationIndex.put("body.appApprovalEnabled", 1);
        expectedIndex.put("adminDelegation", new MongoIndex("adminDelegation", false, adminDelegationIndex));

        DBObject applicationAuthorizationIndex = new BasicDBObject();
        applicationAuthorizationIndex.put("body.appIds", 1);
        expectedIndex.put("applicationAuthorization", new MongoIndex("applicationAuthorization", true, applicationAuthorizationIndex));

        for (MongoIndex index : indexes) {
            String collection = index.getCollection();
            if (collection.equalsIgnoreCase("learningObjective")) {
                fail("Invalid index was parsed");
            } else if (collection.equalsIgnoreCase("adminDelegation")) {
                assertEquals(index, expectedIndex.get("adminDelegation"));
            } else if (collection.equalsIgnoreCase("applicationAuthorization")) {
                assertEquals(index, expectedIndex.get("applicationAuthorization"));
                assertTrue(index.isUnique());
            }
        }
    }

}
