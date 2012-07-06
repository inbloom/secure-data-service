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


package org.slc.sli.ingestion.model.da;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CursorPreparer;
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

    private static final String BATCHJOB_ERROR_COLLECTION = "error";
    private static final String BATCHJOBID = "controlfile.ctl-2345342-2342334234";
    private static final int RESULTLIMIT = 3;
    private static final int START_INDEX = 0;

    @InjectMocks
    @Autowired
    private BatchJobMongoDA mockBatchJobMongoDA;

    @Mock
    MongoTemplate mockMongoTemplate;

    @Mock
    DBCollection mockedCollection;

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

    @Deprecated
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

    /**
     * Test when the last result chunk falls on the limit boundary
     */
    @Test
    public void testGetBatchJobErrorsLimitAlignedResults() {

        int errorIndex = START_INDEX;
        List<Error> errorsReturnedFirst = createErrorsFromIndex(errorIndex, RESULTLIMIT);
        errorIndex += errorsReturnedFirst.size();
        List<Error> errorsReturnedSecond = createErrorsFromIndex(errorIndex, RESULTLIMIT);
        errorIndex += errorsReturnedSecond.size();

        when(mockMongoTemplate.find((Query) any(), eq(Error.class), Matchers.isA(CursorPreparer.class), eq(BATCHJOB_ERROR_COLLECTION)))
        .thenReturn(errorsReturnedFirst)     // return the first time this method call is matched
        .thenReturn(errorsReturnedSecond)    // return the second time this method call is matched
        .thenReturn(Collections.<Error>emptyList()); // return the last time this method call is matched - should NOT be called
        when(mockMongoTemplate.getCollection(eq(BATCHJOB_ERROR_COLLECTION))).thenReturn(mockedCollection);
        when(mockedCollection.count(Matchers.isA(DBObject.class))).thenReturn((long) errorIndex);

        Iterable<Error> errorIterable = mockBatchJobMongoDA.getBatchJobErrors(BATCHJOBID, RESULTLIMIT);

        int iterationCount = START_INDEX;

        for (Error error : errorIterable) {
            assertOnErrorIterableValues(error, iterationCount);
            iterationCount++;
        }

        // check we use the prepared cursor to query the db twice
        verify(mockMongoTemplate, times(2)).find((Query) any(), eq(Error.class), Matchers.isA(CursorPreparer.class), eq(BATCHJOB_ERROR_COLLECTION));
    }

    /**
     * Test when the last result chunk does NOT fall on the limit boundary
     */
    @Test
    public void testGetBatchJobErrors() {

        int errorIndex = START_INDEX;
        List<Error> errorsReturnedFirst = createErrorsFromIndex(errorIndex, RESULTLIMIT);
        errorIndex += errorsReturnedFirst.size();
        List<Error> errorsReturnedSecond = createErrorsFromIndex(errorIndex, RESULTLIMIT - 1);
        errorIndex += errorsReturnedSecond.size();

        when(mockMongoTemplate.find((Query) any(), eq(Error.class), Matchers.isA(CursorPreparer.class), eq(BATCHJOB_ERROR_COLLECTION)))
        .thenReturn(errorsReturnedFirst)     // return the first time this method call is matched
        .thenReturn(errorsReturnedSecond)    // return the second time this method call is matched
        .thenReturn(Collections.<Error>emptyList()); // return the last time this method call is matched - should NOT be called
        when(mockMongoTemplate.getCollection(eq(BATCHJOB_ERROR_COLLECTION))).thenReturn(mockedCollection);
        when(mockedCollection.count(Matchers.isA(DBObject.class))).thenReturn((long) errorIndex);

        Iterable<Error> errorIterable = mockBatchJobMongoDA.getBatchJobErrors(BATCHJOBID, RESULTLIMIT);

        int iterationCount = START_INDEX;

        for (Error error : errorIterable) {
            assertOnErrorIterableValues(error, iterationCount);
            iterationCount++;
        }

        // check we use the prepared cursor to query the db twice
        verify(mockMongoTemplate, times(2)).find((Query) any(), eq(Error.class), Matchers.isA(CursorPreparer.class), eq(BATCHJOB_ERROR_COLLECTION));
    }

    private List<Error> createErrorsFromIndex(int errorStartIndex, int numberOfErrors) {
        List<Error> errors = new ArrayList<Error>();

        for (int errorIndex = errorStartIndex; errors.size() < numberOfErrors; errorIndex++) {
            errors.add(new Error(BATCHJOBID, BatchJobStageType.EDFI_PROCESSOR.getName(),
                "resourceid" + errorIndex,
                "sourceIp" + errorIndex,
                "hostname" + errorIndex,
                "recordId" + errorIndex,
                BatchJobUtils.getCurrentTimeStamp(),
                FaultType.TYPE_ERROR.getName(),
                "errorType" + errorIndex,
                "errorDetail" + errorIndex));
        }

        return errors;
    }

    private void assertOnErrorIterableValues(Error error, int iterationCount) {

        assertEquals(error.getBatchJobId(), BATCHJOBID);
        assertEquals(error.getStageName(), BatchJobStageType.EDFI_PROCESSOR.getName());
        assertEquals(error.getResourceId(), "resourceid" + iterationCount);
        assertEquals(error.getSourceIp(), "sourceIp" + iterationCount);
        assertEquals(error.getHostname(), "hostname" + iterationCount);
        assertEquals(error.getRecordIdentifier(), "recordId" + iterationCount);
        assertEquals(error.getSeverity(), FaultType.TYPE_ERROR.getName());
        assertEquals(error.getErrorType(), "errorType" + iterationCount);
        assertEquals(error.getErrorDetail(), "errorDetail" + iterationCount);

    }

}
