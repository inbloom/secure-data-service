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
package org.slc.sli.bulk.extract;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Mongo access to bulk extract data.
 * @author tke
 *
 */
public class BulkExtractMongoDA {
    private static final Logger LOG = LoggerFactory.getLogger(BulkExtractMongoDA.class);

    public static final String BULK_EXTRACT_COLLECTION = "bulkExtractFiles";
    private static final String FILES = "files";
    private static final String FILE_PATH = "path";
    private static final String DATE = "date";
    private static final String TENANT_ID = "tenantId";

    private Repository<Entity> entityRepository;


    /** Insert a new record is the tenant doesn't exist. Update if existed
     * @param tenantId
     * @param path  path to the extracted file.
     * @param date  the date when the bulk extract was created
     */
    public void updateDBRecord(String tenantId, String path, Date date) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(TENANT_ID, tenantId);
        body.put(FILE_PATH, path);
        body.put(DATE, date);

        BulkExtractEntity bulkExtractEntity = new BulkExtractEntity(body, tenantId);

        entityRepository.update(BULK_EXTRACT_COLLECTION, bulkExtractEntity, false);

        LOG.info("Finished creating bulk extract record");

    }

    public Repository<Entity> getEntityRepository() {
        return entityRepository;
    }

    /**
     * @param entityRepository the entityRepository to set
     */
    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

}
