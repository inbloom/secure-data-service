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

    private static MongoTemplate template;

    @Override
    public void saveBatchJob(NewBatchJob job) {
        if (job != null) {
            template.save(job);
        }
    }

    @Override
    public NewBatchJob findBatchJobById(String batchJobId) {
        Query query = new Query(Criteria.where("_id").is(batchJobId));
        return template.findOne(query, NewBatchJob.class);
    }

    @Override
    public List<Error> findBatchJobErrors(String jobId) {
        Query query = new Query(Criteria.where("batchJobId").is(jobId));
        List<Error> errors = template.find(query, Error.class, "error");
        return errors;
    }

    @Override
    public void saveError(Error error) {
        if (error != null) {
            template.save(error);
        }
    }

}
