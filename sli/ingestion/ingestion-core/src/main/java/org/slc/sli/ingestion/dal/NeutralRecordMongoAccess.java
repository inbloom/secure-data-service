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


package org.slc.sli.ingestion.dal;

import java.util.List;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.NeutralQuery.SortOrder;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Order;

/**
 * write NeutralRecord objects to mongo
 *
 * @author dduran
 *
 */
public class NeutralRecordMongoAccess implements NeutralRecordAccess, ResourceWriter<NeutralRecord>, InitializingBean {

    private NeutralRecordRepository neutralRecordRepository;

    @Value("${sli.ingestion.staging.mongotemplate.writeConcern}")
    private String writeConcern;

    @Override
    public void afterPropertiesSet() throws Exception {
        neutralRecordRepository.setWriteConcern(writeConcern);
    }

    @Override
    public void writeResource(NeutralRecord neutralRecord, String jobId) {
        neutralRecordRepository.createForJob(neutralRecord, jobId);
    }
    
    @Override
    public void insertResource(NeutralRecord neutralRecord, String jobId) {
        neutralRecordRepository.insertForJob(neutralRecord, jobId);
    }

    @Override
    public void insertResources(List<NeutralRecord> neutralRecords, String collectionName, String jobId) {
        neutralRecordRepository.insertAllForJob(neutralRecords, collectionName, jobId);
    }
    
    public NeutralRecordRepository getRecordRepository() {
        return this.neutralRecordRepository;
    }

    public void setNeutralRecordRepository(NeutralRecordRepository neutralRecordRepository) {
        this.neutralRecordRepository = neutralRecordRepository;
    }

    @Override
    public long collectionCountForJob(String collectionNameAsStaged, String jobId) {
        return neutralRecordRepository.countForJob(collectionNameAsStaged, new NeutralQuery(), jobId);
    }

    @Override
    public long countCreationTimeWithinRange(String collectionName, long min, long max, String jobId) {
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("creationTime", NeutralCriteria.CRITERIA_GTE, min));
        query.addCriteria(new NeutralCriteria("creationTime", NeutralCriteria.CRITERIA_LT, max));
        
        return neutralRecordRepository.countForJob(collectionName, query, jobId);
    }

    @Override
    public long getMaxCreationTimeForEntity(IngestionStagedEntity stagedEntity, String jobId) {
        return getCreationTimeForEntity(stagedEntity, jobId, Order.DESCENDING) + 1;
    }

    @Override
    public long getMinCreationTimeForEntity(IngestionStagedEntity stagedEntity, String jobId) {
        return getCreationTimeForEntity(stagedEntity, jobId, Order.ASCENDING);
    }

    private long getCreationTimeForEntity(IngestionStagedEntity stagedEntity, String jobId, Order order) {
        NeutralQuery query = new NeutralQuery(1);
        query.setSortBy("creationTime");
        
        if (order.equals(Order.ASCENDING)) {
            query.setSortOrder(SortOrder.ascending);
        } else if (order.equals(Order.DESCENDING)) {
            query.setSortOrder(SortOrder.descending);
        }

        NeutralRecord record = neutralRecordRepository.findOneForJob(
                stagedEntity.getCollectionNameAsStaged(), query, jobId);
        
        return record.getCreationTime();
    }
}
