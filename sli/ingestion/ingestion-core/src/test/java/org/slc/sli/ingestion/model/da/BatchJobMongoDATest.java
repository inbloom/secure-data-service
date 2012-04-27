package org.slc.sli.ingestion.model.da;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.util.BatchJobUtils;

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

    @InjectMocks
    @Autowired
    private BatchJobMongoDA mockBatchJobMongoDA;

    @Mock
    MongoTemplate mockMongoTemplate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindBatchJob() {
        NewBatchJob job = new NewBatchJob(BATCHJOBID);

        when(mockMongoTemplate.findOne((Query) any(), eq(NewBatchJob.class))).thenReturn(job);

        NewBatchJob resultJob = mockBatchJobMongoDA.findBatchJobById(BATCHJOBID);

        assertEquals(resultJob.getId(), BATCHJOBID);
    }

    @Test
    public void testFindBatchJobErrors() {
        List<Error> errors = new ArrayList<Error>();
        Error error = new Error(BATCHJOBID, BatchJobStageType.EDFI_PROCESSOR.getName(), "resourceid", "sourceIp",
                "hostname", "recordId", BatchJobUtils.getCurrentTimeStamp(), FaultType.TYPE_ERROR.getName(), "errorType", "errorDetail");
        errors.add(error);

        when(mockMongoTemplate.find((Query) any(), eq(Error.class), eq("error"))).thenReturn(errors);

        List<Error> errorList = mockBatchJobMongoDA.findBatchJobErrors(BATCHJOBID);

        Error errorReturned = errorList.get(0);
        assertEquals(errorReturned.getBatchJobId(), BATCHJOBID);
        assertEquals(errorReturned.getStageName(), BatchJobStageType.EDFI_PROCESSOR.getName());
        assertEquals(errorReturned.getResourceId(), "resourceid");
        assertEquals(errorReturned.getSourceIp(), "sourceIp");
        assertEquals(errorReturned.getHostname(), "hostname");
        assertEquals(errorReturned.getRecordIdentifier(), "recordId");
        assertEquals(errorReturned.getSeverity(), FaultType.TYPE_ERROR.getName());
        assertEquals(errorReturned.getErrorType(), "errorType");
        assertEquals(errorReturned.getErrorDetail(), "errorDetail");
    }

}
