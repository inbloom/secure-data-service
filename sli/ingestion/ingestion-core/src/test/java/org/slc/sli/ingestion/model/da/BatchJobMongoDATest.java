package org.slc.sli.ingestion.model.da;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;

/**
 * JUnits for testing the BatchJobMongoDA class.
 *
 * @author bsuzuki
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class BatchJobMongoDATest {

    private static final String BATCHJOBID = "controlfile.ctl-2345342-2342334234";

    @Test
    public void testFindBatchJob() {
        NewBatchJob job = new NewBatchJob(BATCHJOBID);

        MongoTemplate mockedTemplate = mock(MongoTemplate.class);
        when(mockedTemplate.findOne((Query) any(), eq(NewBatchJob.class))).thenReturn(job);

        BatchJobMongoDA bjmda = new BatchJobMongoDA();
        bjmda.setBatchJobMongoTemplate(mockedTemplate);
        NewBatchJob resultJob = bjmda.findBatchJobById(BATCHJOBID);

        assertEquals(resultJob.getId(), BATCHJOBID);
    }

    @Test
    public void testFindBatchJobErrors() {
        List<Error> errors = new ArrayList<Error>();
        Error error = new Error(BATCHJOBID, BatchJobStageType.EDFI_PROCESSOR.getName(), "resourceid",
                "sourceIp", "hostname", "recordId", "timestamp", FaultType.TYPE_ERROR.getName(),
                "errorType", "errorDetail");
        errors.add(error);

        MongoTemplate mockedTemplate = mock(MongoTemplate.class);
        when(mockedTemplate.find((Query) any(), eq(Error.class), eq("error"))).thenReturn(errors);

        BatchJobMongoDA bjmda = new BatchJobMongoDA();
        bjmda.setBatchJobMongoTemplate(mockedTemplate);
        BatchJobMongoDAStatus resultStatus = bjmda.findBatchJobErrors(BATCHJOBID);

        assertTrue(resultStatus.isSuccess());
        assertEquals(resultStatus.getMessage(), "Returned errors for " + BATCHJOBID);

        Error errorReturned = ((List<Error>) resultStatus.getResult()).get(0);
        assertEquals(errorReturned.getBatchJobId(), BATCHJOBID);
        assertEquals(errorReturned.getStageName(), BatchJobStageType.EDFI_PROCESSOR.getName());
        assertEquals(errorReturned.getResourceId(), "resourceid");
        assertEquals(errorReturned.getSourceIp(), "sourceIp");
        assertEquals(errorReturned.getHostname(), "hostname");
        assertEquals(errorReturned.getRecordIdentifier(), "recordId");
        assertEquals(errorReturned.getTimestamp(), "timestamp");
        assertEquals(errorReturned.getSeverity(), FaultType.TYPE_ERROR.getName());
        assertEquals(errorReturned.getErrorType(), "errorType");
        assertEquals(errorReturned.getErrorDetail(), "errorDetail");
    }

}