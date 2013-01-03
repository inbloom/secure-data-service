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

import org.slc.sli.ingestion.util.MongoIndex;

/**
 *
 * @author npandey
 *
 */

public abstract class DbIndexValidator {
    private Map<String, List<MongoIndex>> indexCache = new HashMap<String, List<MongoIndex>>();

    private static final Logger LOG = LoggerFactory.getLogger(DbIndexValidator.class);

    protected abstract List<MongoIndex> parseFile(String indexFile);

    private void updateCache(String collectionName, DB database) {
        // UN: Check the index cache, if the collection exists in the cache, use that
        // collection,
        // else query from Mongo and save it in the cache.
        if (!indexCache.containsKey(collectionName)) {
            if (database.collectionExists(collectionName)) {
                DBCollection collection = database.getCollection(collectionName);
                List<DBObject> indexList = collection.getIndexInfo();
                List<MongoIndex> indexFromDb = new ArrayList<MongoIndex>();
                for (DBObject dbObject : indexList) {
                    DBObject keyObj = (DBObject) dbObject.get("key");
                    Object uniqueField = dbObject.get("unique");
                    boolean unique = false;
                    if (uniqueField != null) {
                        unique = Boolean.parseBoolean(uniqueField.toString());
                    }
                    indexFromDb.add(new MongoIndex(collectionName, unique, keyObj));
                }
                indexCache.put(collectionName, indexFromDb);
            }
        }
    }

    @SuppressWarnings({ "boxing", "unchecked" })
    protected void checkIndexes(MongoIndex index, DB database) {

        String collectionName = index.getCollection();

        updateCache(collectionName, database);

        Map<String, Object> indexMap = index.getKeys().toMap();
        if (indexCache.containsKey(collectionName)) {
            List<MongoIndex> indices = indexCache.get(collectionName);

            boolean indexMatch = false;
            for (MongoIndex indexFromCache : indices) {
                Map<String, Object> keysFromCache = indexFromCache.getKeys().toMap();
                if (keysFromCache.size() != indexMap.size()) {
                    continue;
                }

                for (Map.Entry<String, Object> indexCacheEntry : keysFromCache.entrySet()) {
                    if (!indexMap.containsKey(indexCacheEntry.getKey())) {
                        indexMatch = false;
                        break;
                    }

                    if (index.isUnique() != indexFromCache.isUnique()) {
                        indexMatch = false;
                        break;
                    }

                    // The value in DB is either saved as a double or integer (nondeterministic),
                    // compare with both double as well as integer and verify that the index does
                    // not match.
                    double indexMapDoubleValue = Double.valueOf(indexMap.get(indexCacheEntry.getKey()).toString());
                    if (!indexCacheEntry.getValue().equals(indexMapDoubleValue)
                            && !indexCacheEntry.getValue().equals(indexMap.get(indexCacheEntry.getKey()))) {
                        indexMatch = false;
                        break;
                    } else {
                        indexMatch = true;
                    }
                }
                if (indexMatch) {
                    logInfo("Index verified: " + collectionName + " " + index.getKeys().toString() +
                            ", unique:" + index.isUnique());
                    break;
                }
            }

            if (!indexMatch) {
                logError("Index missing: " + collectionName + " " + index.getKeys().toString() +
                        ", unique:" + index.isUnique());
            }
        } else {
            logError("Index missing: " + collectionName + " " + index.getKeys().toString() +
                    ", unique:" + index.isUnique());
        }
    }

    protected void logError(String message) {
        LOG.error(message);
    }

    protected void logInfo(String message) {
        LOG.info(message);
    }

    /**
     * @return the indexCache
     */
    public Map<String, List<MongoIndex>> getIndexCache() {
        return indexCache;
    }

    /**
     * @param indexCache the indexCache to set
     */
    public void setIndexCache(Map<String, List<MongoIndex>> indexCache) {
        this.indexCache = indexCache;
    }

    public abstract void verifyIndexes();

}
