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

package org.slc.sli.ingestion.dal;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.util.MongoCommander;

/**
 * write NeutralRecord objects to mongo
 *
 * @author dduran
 *
 */
public class NeutralRecordMongoAccess implements NeutralRecordAccess, ResourceWriter<NeutralRecord>, InitializingBean {

    private NeutralRecordRepository neutralRecordRepository;

    private Set<String> stagingIndexes;

    @Value("${sli.ingestion.staging.mongotemplate.writeConcern}")
    private String writeConcern;

    @Value("${sli.ingestion.totalRetries}")
    private int numberOfRetries;

    @Override
    public void afterPropertiesSet() throws Exception {
        neutralRecordRepository.setWriteConcern(writeConcern);
    }

    @Override
    public void writeResource(NeutralRecord neutralRecord) {
        neutralRecordRepository.createForJob(neutralRecord);
    }

    @Override
    public void insertResource(NeutralRecord neutralRecord) {
        neutralRecordRepository.insert(neutralRecord);
    }

    @Override
    public void insertResources(List<NeutralRecord> neutralRecords, String collectionName) {
        neutralRecordRepository.insertAllWithRetries(neutralRecords, collectionName, numberOfRetries);
    }

    public NeutralRecordRepository getRecordRepository() {
        return this.neutralRecordRepository;
    }

    public void setNeutralRecordRepository(NeutralRecordRepository neutralRecordRepository) {
        this.neutralRecordRepository = neutralRecordRepository;
    }

    public void setStagingIndexes(Set<String> stagingIndexes) {
        this.stagingIndexes = stagingIndexes;
    }

    @Override
    public long collectionCountForJob(String collectionNameAsStaged) {
        return neutralRecordRepository.countForJob(collectionNameAsStaged, new NeutralQuery());
    }

    @Override
    public long countCreationTimeWithinRange(String collectionName, long min, long max) {
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("creationTime", NeutralCriteria.CRITERIA_GTE, min, false));
        query.addCriteria(new NeutralCriteria("creationTime", NeutralCriteria.CRITERIA_LT, max, false));

        return neutralRecordRepository.countForJob(collectionName, query);
    }

    @Override
    public long getMaxCreationTimeForEntity(IngestionStagedEntity stagedEntity) {
        return getCreationTimeForEntity(stagedEntity.getCollectionNameAsStaged(), Order.DESCENDING) + 1;
    }

    @Override
    public long getMinCreationTimeForEntity(IngestionStagedEntity stagedEntity) {
        return getCreationTimeForEntity(stagedEntity.getCollectionNameAsStaged(), Order.ASCENDING);
    }

    @Override
    public void cleanupJob(String batchJobId) {
        neutralRecordRepository.deleteStagedRecordsForJob(batchJobId);
    }

    /**
     * Gets the creation time for the first entity that matches the criteria of collection name,
     * batch job id, and sort order.
     *
     * @param collectionName
     *            Collection in which to find entity.
     * @param jobId
     *            Current batch job id.
     * @param order
     *            Sort order (ascending, descending).
     * @return Long representing creation time of entity.
     */
    private long getCreationTimeForEntity(String collectionName, Order order) {
        Query query = new Query().limit(1);
        query.sort().on("creationTime", order);

        Iterable<NeutralRecord> nr = neutralRecordRepository.findAllByQuery(collectionName, query);
        return nr.iterator().next().getCreationTime();
    }

    @Override
    public void ensureIndexes() {
        MongoTemplate mongoTemplate = neutralRecordRepository.getTemplate();
        MongoCommander.ensureIndexes(stagingIndexes, mongoTemplate.getDb().getName(), mongoTemplate);
    }
}
