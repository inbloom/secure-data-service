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
package org.slc.sli.ingestion.validation;

import com.mongodb.DB;

import org.springframework.data.mongodb.core.MongoTemplate;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;

/**
 * Validator to validate Mongo information.
 *
 * @author tke
 *
 */
public class MongoValidator extends ComplexValidator<Object> {

    private static final String STAGE_NAME = "Mongo Validation";

    private MongoTemplate mongoTemplate;

    @Override
    public boolean isValid(Object object, AbstractMessageReport report, ReportStats reportStats, Source source) {
        boolean isValid = true;
        DB dbConn = mongoTemplate.getDb();

        for (Validator<Object> validator : this.getValidators()) {
            isValid &= validator.isValid(dbConn, report, reportStats, source);
        }

        return isValid;
    }

    /**
     * @return the mongoTemplate
     */
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    /**
     * @param mongoTemplate
     *            the mongoTemplate to set
     */
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

}
