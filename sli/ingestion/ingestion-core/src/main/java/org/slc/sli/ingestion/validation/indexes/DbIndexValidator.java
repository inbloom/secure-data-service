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

package org.slc.sli.ingestion.validation.indexes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.util.IndexFileParser;
import org.slc.sli.ingestion.util.MongoIndex;

/**
 *
 * @author npandey
 *
 */

public abstract class DbIndexValidator {
    private Map<String, List<HashMap<String, Object>>> indexCache = new HashMap<String, List<HashMap<String, Object>>>();
    private Logger log = LoggerFactory.getLogger(DbIndexValidator.class);

    protected abstract List<MongoIndex> parseFile(String indexFile);

    private void updateCache(String collectionName, DB database) {
        // UN: Check the index cache, if the collection exists in the cache, use that
        // collection,
        // else query from Mongo and save it in the cache.
        if (!indexCache.containsKey(collectionName)) {
            if (database.collectionExists(collectionName)) {
                DBCollection collection = database.getCollection(collectionName);
                List<DBObject> indexList = collection.getIndexInfo();
                List<HashMap<String, Object>> indices = new ArrayList<HashMap<String, Object>>();
                for (DBObject dbObject : indexList) {
                    Object object = dbObject.get("key");
                    indices.add((HashMap<String, Object>) IndexFileParser.parseJson(object.toString()));
                }
                indexCache.put(collectionName, indices);
            }
        }
    }

    @SuppressWarnings("boxing")
    protected void checkIndexes(MongoIndex index, DB database) {

        String collectionName = index.getCollection();

        updateCache(collectionName, database);

        Map<String, Object> indexMap = index.getKeys();
        if (indexCache.containsKey(collectionName)) {
            List<HashMap<String, Object>> indices = indexCache.get(collectionName);
            for (Map<String, Object> indexMapFromCache : indices) {
                if (indexMapFromCache.size() != indexMap.size()) {
                    continue;
                }
                boolean indexMatch = true;
                for (Map.Entry<String, Object> indexCacheEntry : indexMapFromCache.entrySet()) {
                    if (!indexMap.containsKey(indexCacheEntry.getKey())) {
                        indexMatch = false;
                        break;
                    }

                    // UN: The value in DB is either saved as a double or integer
                    // (nondeterministic), so I
                    // need to compare it with both double as well as integer and verify that
                    // the
                    // index
                    // does not match.
                    double indexMapDoubleValue = Double.valueOf(indexMap.get(indexCacheEntry.getKey()).toString());
                    if (!indexCacheEntry.getValue().equals(indexMapDoubleValue)
                            && !indexCacheEntry.getValue().equals(indexMap.get(indexCacheEntry.getKey()))) {
                        indexMatch = false;
                        break;
                    }
                }
                if (indexMatch) {
                    log.info("{} : Index verified: {}", database.getName() + "." + collectionName, index.getKeys().toString());
                    break;
                }
            }
        } else {
            log.error("{} : Index missing: {}", database.getName() + "." + collectionName, index.getKeys().toString());
        }
    }

    public abstract void verifyIndexes();
}
