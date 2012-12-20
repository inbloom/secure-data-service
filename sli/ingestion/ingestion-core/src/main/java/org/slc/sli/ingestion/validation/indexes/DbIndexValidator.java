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

import java.util.Collections;
import java.util.Set;

import com.mongodb.DB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.util.MongoIndex;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

/**
 *
 * @author npandey
 *
 */

public class DbIndexValidator extends SimpleValidatorSpring<DB> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbIndexValidator.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(DB db, AbstractMessageReport report, AbstractReportStats reportStats) {
        Set<MongoIndex> expectedIndexes = loadExpectedIndexes();
        Set<MongoIndex> actualIndexes = loadIndexInfoFromDB(db);

        return isValid(expectedIndexes, actualIndexes, report, reportStats);
    }

    protected Set<MongoIndex> loadExpectedIndexes() {
        return Collections.emptySet();
    }

    private Set<MongoIndex> loadIndexInfoFromDB(DB db) {
        return Collections.emptySet();
        /*Set<MongoIndex> dbIndexes = new HashSet<MongoIndex>();

        Set<String> collectionNames = database.getCollectionNames();

        for (String collectionName : collectionNames) {
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

        return indexCache;*/
    }

    /**
     * @param expectedIndexes
     * @param actualIndexes
     * @param report
     * @param reportStats
     * @return
     */
    protected boolean isValid(Set<MongoIndex> expectedIndexes, Set<MongoIndex> actualIndexes,
            AbstractMessageReport report, AbstractReportStats reportStats) {
        return false;
    }

    protected void checkIndexes(MongoIndex index, DB database) {
/*
        String collectionName = index.getCollection();

        Map<String, List<MongoIndex>> indexCache = loadIndexInfo(database);

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
                    logInfo("Index verified: {} {}, unique: {}", collectionName, index.getKeys(), index.isUnique());
                    break;
                }
            }

            if (!indexMatch) {
                logError("Index missing: {} {}, unique: {}", collectionName, index.getKeys(), index.isUnique());
            }
        } else {
            logError("Index missing: {} {}, unique: {}", collectionName, index.getKeys(), index.isUnique());
        }*/
    }

    protected void logError(String message, Object... args) {
        LOGGER.error(message, args);
    }

    protected void logInfo(String message, Object... args) {
        LOGGER.info(message, args);
    }
/*
    public void verifyIndexes() {
        List<MongoIndex> indexes = retrieveExpectedIndexes();
            for (MongoIndex index : indexes) {
                checkIndexes(index, mongoTemplate.getDb().getSisterDB(dbName));
            }
    }
*/
}
