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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.reporting.CoreMessageCode;
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

    protected Set<MongoIndex> loadIndexInfoFromDB(DB database) {
        Set<MongoIndex> dbIndexes = new HashSet<MongoIndex>();

        Set<String> collectionNames = database.getCollectionNames();

        for (String collectionName : collectionNames) {
            DBCollection collection = database.getCollection(collectionName);
            List<DBObject> indexList = collection.getIndexInfo();
            for (DBObject dbObject : indexList) {
                DBObject keyObj = (DBObject) dbObject.get("key");
                Object uniqueField = dbObject.get("unique");
                boolean unique = false;
                if (uniqueField != null) {
                    unique = Boolean.parseBoolean(uniqueField.toString());
                }
                dbIndexes.add(new MongoIndex(collectionName, unique, keyObj));
            }
        }
        return dbIndexes;
    }

    /**
     * @param expectedIndexes
     * @param actualIndexes
     * @param report
     * @param reportStats
     * @return
     */
    protected static boolean isValid(Set<MongoIndex> expectedIndexes, Set<MongoIndex> actualIndexes,
            AbstractMessageReport report, AbstractReportStats reportStats) {

        boolean res = true;
        for (MongoIndex index : expectedIndexes) {
            if (actualIndexes.contains(index)) {
                res &= true;
                report.info(reportStats, CoreMessageCode.CORE_0018, index.getCollection(), index.getKeys(), index.isUnique());
            } else {
                res = false;
                report.error(reportStats, CoreMessageCode.CORE_0038, index.getCollection(), index.getKeys(), index.isUnique());
            }
        }
        return res;
    }

}
