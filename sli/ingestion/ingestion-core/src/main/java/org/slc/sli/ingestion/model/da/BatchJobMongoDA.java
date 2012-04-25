package org.slc.sli.ingestion.model.da;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;

/**
 *
 * @author ldalgado
 *
 */
@Component
public class BatchJobMongoDA implements BatchJobDAO {

    private MongoTemplate batchJobMongoTemplate;

    @Override
    public void saveBatchJob(NewBatchJob job) {
        if (job != null) {
            batchJobMongoTemplate.save(job);
        }
    }

    @Override
    public NewBatchJob findBatchJobById(String batchJobId) {
        Query query = new Query(Criteria.where("_id").is(batchJobId));
        return batchJobMongoTemplate.findOne(query, NewBatchJob.class);
    }

    @Override
    public List<Error> findBatchJobErrors(String jobId) {
        Query query = new Query(Criteria.where("batchJobId").is(jobId));
        List<Error> errors = batchJobMongoTemplate.find(query, Error.class, "error");
        return errors;
    }

    @Override
    public void saveError(Error error) {
        if (error != null) {
            batchJobMongoTemplate.save(error);
        }
    }

    public void setBatchJobMongoTemplate(MongoTemplate mongoTemplate) {
        this.batchJobMongoTemplate = mongoTemplate;
    }

}
