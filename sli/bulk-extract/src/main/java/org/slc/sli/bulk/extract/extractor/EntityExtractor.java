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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.bulk.extract.files.DataExtractFile;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.treatment.TreatmentApplicator;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;


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

    /**
     * extract all the records of entity.
     *
     * @param tenant
     *          TenantId
     * @param archiveFile
     *          Archive File
     * @param entityName
     *          Name of the entity to be extracted
     */
    public void extractEntity(String tenant, ExtractFile archiveFile, String entityName) {

        LOG.info("Extracting " + entityName);
        DataExtractFile dataFile = null;
        String collectionName = getCollectionName(entityName);
        Query query = getQuery(entityName);
        long noOfRecords = 0;
        JsonFactory jsonFactory = new JsonFactory();

        try {
            TenantContext.setTenantId(tenant);
            Iterator<Entity> cursor = entityRepository.findEach(collectionName,
                    query);

            if (cursor.hasNext()) {
                dataFile = archiveFile.getDataFileEntry(entityName);

                OutputStream os = dataFile.getOutputStream();
                JsonGenerator jsonGenerator = jsonFactory
                        .createJsonGenerator(os);
                jsonGenerator.writeStartArray();
                jsonGenerator.flush();

                while (cursor.hasNext()) {
                    Entity record = cursor.next();
                    noOfRecords++;

                    record = applicator.apply(record);

                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(jsonGenerator, record.getBody());
                }
                jsonGenerator.writeEndArray();
                jsonGenerator.flush();
            }

            LOG.info("Finished extracting {} records for " + entityName,
                    noOfRecords);

        } catch (IOException e) {
            LOG.error("Error while extracting " + entityName, e);
        } finally {
            TenantContext.setTenantId(null);
            if (dataFile != null) {
                dataFile.close();
            }
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

    /**
     * get queried entities.
     * @param queriedEntities entities
     */
    public void setQueriedEntities(Map<String, String> queriedEntities) {
        this.queriedEntities = queriedEntities;
    }

    /**
     * set combined entities.
     * @param combinedEntities combined entities
     */
    public void setCombinedEntities(Map<String, String> combinedEntities) {
        this.combinedEntities = combinedEntities;
    }

    /**
     * set entity repository.
     * @param entityRepository entity repository
     */
    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

    /**
     * get applicator.
     * @return treatment applicator
     */
    public TreatmentApplicator getApplicator() {
        return applicator;
    }

    /**
     * set applicator.
     * @param applicator applicator
     */
    public void setApplicator(TreatmentApplicator applicator) {
        this.applicator = applicator;
    }
}
