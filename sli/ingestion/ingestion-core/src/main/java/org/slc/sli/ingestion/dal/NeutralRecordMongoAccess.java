package org.slc.sli.ingestion.dal;

import java.util.Iterator;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;

/**
 * write NeutralRecord objects to mongo
 *
 * @author dduran
 *
 */
public class NeutralRecordMongoAccess implements NeutralRecordAccess, ResourceWriter<NeutralRecord> {

    private NeutralRecordRepository neutralRecordRepository;

    @Override
    public void writeResource(NeutralRecord neutralRecord, String jobId) {
        neutralRecordRepository.createForJob(neutralRecord, jobId);
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
        Criteria limiter = Criteria.where("creationTime").gte(min).lt(max);
        Query query = new Query().addCriteria(limiter);

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
        Query query = new Query();
        query.sort().on("creationTime", order);
        query.limit(1);
        Iterable<NeutralRecord> nr = neutralRecordRepository.findByQueryForJob(
                stagedEntity.getCollectionNameAsStaged(), query, jobId);
        Iterator<NeutralRecord> nrIterator = nr.iterator();

        return nrIterator.next().getCreationTime();
    }

}
