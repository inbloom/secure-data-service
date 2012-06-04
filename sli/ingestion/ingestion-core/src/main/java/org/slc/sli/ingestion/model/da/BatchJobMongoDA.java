package org.slc.sli.ingestion.model.da;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Iterator;
import java.util.List;

import com.mongodb.Bytes;
import com.mongodb.DBCursor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.CursorPreparer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;

/**
 *
 * @author ldalgado
 *
 */
@Component
public class BatchJobMongoDA implements BatchJobDAO {
    private static final String BATCHJOB_ERROR_COLLECTION = "error";
    private static final String BATCHJOB_STAGE_SEPARATE_COLLECTION = "batchJobStage";

    private static final String BATCHJOBID_FIELDNAME = "batchJobId";

    private MongoTemplate batchJobMongoTemplate;

    @Value("${sli.ingestion.errors.tracking}")
    private String trackIngestionErrors;

    @Override
    public void saveBatchJob(NewBatchJob job) {
        if (job != null) {
            batchJobMongoTemplate.save(job);
        }
    }

    @Override
    public void saveBatchJobStageSeparatelly(String batchJobId, Stage stage) {
        stage.setJobId(batchJobId);
        batchJobMongoTemplate.save(stage, BATCHJOB_STAGE_SEPARATE_COLLECTION);
    }

    @Override
    public List<Stage> getBatchStagesStoredSeperatelly(String batchJobId) {
        return batchJobMongoTemplate.find(query(where("jobId").is(batchJobId)), Stage.class, BATCHJOB_STAGE_SEPARATE_COLLECTION);
    }

    @Override
    public NewBatchJob findBatchJobById(String batchJobId) {
        return batchJobMongoTemplate.findOne(query(where("_id").is(batchJobId)), NewBatchJob.class);
    }

    @Override
    public List<Error> findBatchJobErrors(String jobId) {
        List<Error> errors = batchJobMongoTemplate.find(query(where(BATCHJOBID_FIELDNAME).is(jobId)), Error.class, BATCHJOB_ERROR_COLLECTION);
        return errors;
    }

    public List<Error> findBatchJobErrors(String jobId, CursorPreparer cursorPreparer) {
        List<Error> errors = batchJobMongoTemplate.find(query(where(BATCHJOBID_FIELDNAME).is(jobId)), Error.class, cursorPreparer, BATCHJOB_ERROR_COLLECTION);
        return errors;
    }

    @Override
    public void saveError(Error error) {
        if (error != null && trackIngestionErrors.equals("true")) {
            batchJobMongoTemplate.save(error);
        }
    }

    @Override
    public Iterable<Error> getBatchJobErrors(String jobId, int limit) {
        return new ErrorIterable(jobId, limit);
    }

    /**
     * Iterable error class
     *
     * @author bsuzuki
     *
     */
    private class ErrorIterable implements Iterable<Error> {

        private static final int ERROR_QUERY_DEFAULT_LIMIT = 100;

        private String jobId = null;
        private int resultLimit = ERROR_QUERY_DEFAULT_LIMIT;

        public ErrorIterable(String jobId, int queryResultLimit) {
            this.jobId = jobId;
            this.resultLimit = queryResultLimit;
        }

        @Override
        public Iterator<Error> iterator() {
            return new ErrorIterator(jobId, resultLimit);
        }

        /**
         * Iterator for errors
         *
         * @author bsuzuki
         *
         */
        private final class ErrorIterator implements Iterator<Error> {
            private String jobId = null;
            private Iterator<Error> currentIterator;
            private long remainingResults = 0;

            private LimitedCursorPreparer cursorPreparer;

            private ErrorIterator(String jobId, int queryResultLimit) {
                this.jobId = jobId;
                this.cursorPreparer = new LimitedCursorPreparer(queryResultLimit);
                this.remainingResults = batchJobMongoTemplate.getCollection(BATCHJOB_ERROR_COLLECTION).count(query(where(BATCHJOBID_FIELDNAME).is(jobId)).getQueryObject());
                // TODO use the following rather than the previous line when we upgrade to mongotemplate 1.0.0.M5 or above
                // this.remainingResults = batchJobMongoTemplate.count(query(where(BATCHJOBID_FIELDNAME).is(jobId)), BATCHJOB_ERROR_COLLECTION);
                this.currentIterator = getNextList();
            }

            @Override
            public boolean hasNext() {
                return currentIterator.hasNext() || (remainingResults > 0);
            }

            @Override
            public Error next() {
                if (!currentIterator.hasNext()) {
                    currentIterator = getNextList();
                }
                return currentIterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private Iterator<Error> getNextList() {
                List<Error> errors = batchJobMongoTemplate.find(query(where(BATCHJOBID_FIELDNAME).is(jobId)), Error.class, cursorPreparer, BATCHJOB_ERROR_COLLECTION);
                remainingResults -= errors.size();
                return errors.iterator();
            }
        }

        /**
         * Prepares the cursor to be used when querying for errors
         *
         * @author bsuzuki
         *
         */
        private final class LimitedCursorPreparer implements CursorPreparer {

            private final int limit;
            private int position = 0;

            public LimitedCursorPreparer(int limit) {
                this.limit = limit;
            }

            @Override
            /**
             * set the skip and limit for the internal cursor to get the result
             * in chunks of up to 'limit' size
             */
            public DBCursor prepare(DBCursor cursor) {

                DBCursor cursorToUse = cursor;

                cursorToUse = cursorToUse.skip(position);
                cursorToUse = cursorToUse.limit(limit);
                cursorToUse.batchSize(1000);

                position += limit;

                cursorToUse.addOption(Bytes.QUERYOPTION_NOTIMEOUT);

                return cursorToUse;
            }

        }

    }

    public void setBatchJobMongoTemplate(MongoTemplate mongoTemplate) {
        this.batchJobMongoTemplate = mongoTemplate;
    }

}
