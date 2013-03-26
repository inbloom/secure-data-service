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
package org.slc.sli.bulk.extract.extractor;

import java.util.Iterator;
import java.util.Map;

import org.slc.sli.bulk.extract.treatment.TreatmentApplicator;
import org.slc.sli.bulk.extract.zip.OutstreamZipFile;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


/**
 * Extractor pulls data for on entity from mongo and writes it to file.
 *
 * @author tshewchuk
 *
 */
public class EntityExtractor{

    private static final Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);

    private Map<String, String> queriedEntities;

    private Map<String, String> combinedEntities;

    private Repository<Entity> entityRepository;

    private TreatmentApplicator applicator;

    public void extractEntity(String tenant, OutstreamZipFile zipFile, String entityName) {

        LOG.info("Extracting " + entityName);

        String collectionName = getCollectionName(entityName);
        Query query = getQuery(entityName);
        long noOfRecords = 0;

        try {
            TenantContext.setTenantId(tenant);
            Iterator<Entity> cursor = entityRepository.findEach(collectionName, query);

            if (cursor.hasNext()) {
                zipFile.createArchiveEntry(entityName + ".json");
                //zipFile.writeJsonDelimiter("[");
                zipFile.writeStartArray();

                while (cursor.hasNext()) {
                    Entity record = cursor.next();
                    noOfRecords++;

                    record = applicator.apply(record);
                    zipFile.writeData(record.getBody());
                }
                zipFile.writeEndArray();
            }

            LOG.info("Finished extracting {} records for " + entityName, noOfRecords);

        } catch (Exception e) {
            LOG.error("Error while extracting " + entityName, e);
        } finally {
            TenantContext.setTenantId(null);
        }
    }

    private String getCollectionName(String entityName) {
        if (queriedEntities.containsKey(entityName)) {
            return queriedEntities.get(entityName);
        } else {
            return entityName;
        }
    }

    private Query getQuery(String entityName) {
        Query query = new Query();

        if (queriedEntities.containsKey(entityName)) {
            query.addCriteria(Criteria.where("type").is(entityName));
        }


        if (combinedEntities.containsValue(entityName)) {
            query = new Query(Criteria.where("type").in(combinedEntities.keySet()));
        }
        return query;
    }

    public void setQueriedEntities(Map<String, String> queriedEntities) {
        this.queriedEntities = queriedEntities;
    }

    public void setCombinedEntities(Map<String, String> combinedEntities) {
        this.combinedEntities = combinedEntities;
    }

    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

    public TreatmentApplicator getApplicator() {
        return applicator;
    }

    public void setApplicator(TreatmentApplicator applicator) {
        this.applicator = applicator;
    }
}
