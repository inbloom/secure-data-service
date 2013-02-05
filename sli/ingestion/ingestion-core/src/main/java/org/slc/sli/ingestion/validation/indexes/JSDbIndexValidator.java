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

package org.slc.sli.ingestion.validation.indexes;

import java.util.Set;

import com.mongodb.DB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.util.IndexParser;
import org.slc.sli.ingestion.util.MongoCommander;
import org.slc.sli.ingestion.util.MongoIndex;

/**
 * Index validator for those dbs whose indexes are defined in js file
 *
 * @author npandey
 *
 */
public class JSDbIndexValidator extends DbIndexValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSDbIndexValidator.class);

    private String indexFile;

    private IndexParser<String> indexJSFileParser;

    private String dbName;

    @Override
    protected Set<MongoIndex> loadExpectedIndexes() {
        return indexJSFileParser.parse(indexFile);
    }

    @Override
    public boolean isValid(DB db, AbstractMessageReport report, ReportStats reportStats, Source source) {
        DB dbConn = MongoCommander.getDB(dbName, db);
        LOGGER.info("Validating indexes for DB: " + dbName);
        return super.isValid(dbConn, report, reportStats, source);
    }

    /**
     * @return the indexFile
     */
    public String getIndexFile() {
        return indexFile;
    }

    /**
     * @param indexFile
     *            the indexFile to set
     */
    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }

    /**
     * @return the indexJSFileParser
     */
    public IndexParser<String> getIndexJSFileParser() {
        return indexJSFileParser;
    }

    /**
     * @param indexJSFileParser
     *            the indexJSFileParser to set
     */
    public void setIndexJSFileParser(IndexParser<String> indexJSFileParser) {
        this.indexJSFileParser = indexJSFileParser;
    }

    /**
     * @return the dbName
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @param dbName
     *            the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
