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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.util.IndexFileParser;
import org.slc.sli.ingestion.util.MongoIndex;


/**
 * @author tke
 *
 */
public class TenantDBIndexValidator extends DbIndexValidator {

    private static final Logger LOG = LoggerFactory.getLogger(TenantDBIndexValidator.class);

    private static final String INDEX_FILE = "tenantDB_indexes.txt";
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TenantDA tenantDA;
    @Override
    protected List<MongoIndex> parseFile(String indexFile) {
        return IndexFileParser.parseTxtFile(indexFile);
    }


    @Override
    public void verifyIndexes() {
        List<MongoIndex> indexes = IndexFileParser.parseTxtFile(INDEX_FILE);
        List<String> tenantDbs = tenantDA.getAllTenantDbs();

        for (String tenantDb : tenantDbs) {
            LOG.info("Validating indexes for {} database", tenantDb);
            for (MongoIndex index : indexes) {
                checkIndexes(index, mongoTemplate.getDb().getSisterDB(tenantDb));
            }
        }
    }

    /**
     * @return the mongoTemplate
     */
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }


    /**
     * @param mongoTemplate the mongoTemplate to set
     */
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    /**
     * @return the tenantDA
     */
    public TenantDA getTenantDA() {
        return tenantDA;
    }


    /**
     * @param tenantDA the tenantDA to set
     */
    public void setTenantDA(TenantDA tenantDA) {
        this.tenantDA = tenantDA;
    }


}
