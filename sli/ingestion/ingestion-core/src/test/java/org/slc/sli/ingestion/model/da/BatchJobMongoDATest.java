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


package org.slc.sli.ingestion.model.da;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * JUnits for testing the BatchJobMongoDA class.
 *
 * @author bsuzuki
 *
 * TODO Missing unit test coverage as of sprint 6.5 start
 * (low) findLatestBatchJob
 * (low) createPersistenceLatch - MongoException
 * (medium) getStagedEntitiesForJob
 * (medium) removeAllPersistedStagedEntitiesFromJob
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/BatchJob-Mongo.xml" })
public class BatchJobMongoDATest {

    private static final String BATCHJOB_ERROR_COLLECTION = "error";
    private static final int DUP_KEY_CODE = 11000;
    private static final String BATCHJOBID = "controlfile.ctl-2345342-2342334234";
    private static final String RESOURCEID = "InterchangeStudentParent.xml";
    private static final int RESULTLIMIT = 3;
    private static final int START_INDEX = 0;
    private static final String RECORD_HASH_COLLECTION = "recordHash";

    @InjectMocks
    @Autowired
    private BatchJobMongoDA mockBatchJobMongoDA = new BatchJobMongoDA();

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
        NewBatchJob job = new NewBatchJob(BATCHJOBID, "SLI");

        when(mockMongoTemplate.findOne((Query) any(), eq(NewBatchJob.class))).thenReturn(job);

        NewBatchJob resultJob = mockBatchJobMongoDA.findBatchJobById(BATCHJOBID);

        assertEquals(resultJob.getId(), BATCHJOBID);
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

        when(mockMongoTemplate.find((Query) any(), eq(Error.class), eq(BATCHJOB_ERROR_COLLECTION)))
        .thenReturn(errorsReturnedFirst)     // return the first time this method call is matched
        .thenReturn(errorsReturnedSecond)    // return the second time this method call is matched
        .thenReturn(Collections.<Error>emptyList()); // return the last time this method call is matched - should NOT be called
        when(mockMongoTemplate.getCollection(eq(BATCHJOB_ERROR_COLLECTION))).thenReturn(mockedCollection);
        when(mockedCollection.count(Matchers.isA(DBObject.class))).thenReturn((long) errorIndex);

        Iterable<Error> errorIterable = mockBatchJobMongoDA.getBatchJobErrors(BATCHJOBID, RESOURCEID, FaultType.TYPE_ERROR, RESULTLIMIT);

        int iterationCount = START_INDEX;

        for (Error error : errorIterable) {
            assertOnErrorIterableValues(error, iterationCount);
            iterationCount++;
        }

        // Check we queried the db once.
        verify(mockMongoTemplate, times(1)).find((Query) any(), eq(Error.class), eq(BATCHJOB_ERROR_COLLECTION));
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

        when(mockMongoTemplate.find((Query) any(), eq(Error.class), eq(BATCHJOB_ERROR_COLLECTION)))
        .thenReturn(errorsReturnedFirst)     // return the first time this method call is matched
        .thenReturn(errorsReturnedSecond)    // return the second time this method call is matched
        .thenReturn(Collections.<Error>emptyList()); // return the last time this method call is matched - should NOT be called
        when(mockMongoTemplate.getCollection(eq(BATCHJOB_ERROR_COLLECTION))).thenReturn(mockedCollection);
        when(mockedCollection.count(Matchers.isA(DBObject.class))).thenReturn((long) errorIndex);

        Iterable<Error> errorIterable = mockBatchJobMongoDA.getBatchJobErrors(BATCHJOBID, RESOURCEID, FaultType.TYPE_ERROR, RESULTLIMIT);

        int iterationCount = START_INDEX;

        for (Error error : errorIterable) {
            assertOnErrorIterableValues(error, iterationCount);
            iterationCount++;
        }

        // Check we queried the db once.
        verify(mockMongoTemplate, times(1)).find((Query) any(), eq(Error.class), eq(BATCHJOB_ERROR_COLLECTION));
    }

    private List<Error> createErrorsFromIndex(int errorStartIndex, int numberOfErrors) {
        List<Error> errors = new ArrayList<Error>();

        for (int errorIndex = errorStartIndex; errors.size() < numberOfErrors; errorIndex++) {
            errors.add(new Error(BATCHJOBID, BatchJobStageType.EDFI_PARSER_PROCESSOR.getName(),
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
        assertEquals(error.getStageName(), BatchJobStageType.EDFI_PARSER_PROCESSOR.getName());
        assertEquals(error.getResourceId(), "resourceid" + iterationCount);
        assertEquals(error.getSourceIp(), "sourceIp" + iterationCount);
        assertEquals(error.getHostname(), "hostname" + iterationCount);
        assertEquals(error.getRecordIdentifier(), "recordId" + iterationCount);
        assertEquals(error.getSeverity(), FaultType.TYPE_ERROR.getName());
        assertEquals(error.getErrorType(), "errorType" + iterationCount);
        assertEquals(error.getErrorDetail(), "errorDetail" + iterationCount);

    }

    @Test
    public void testUpsertRecordHash() {
        // Capture mongoTemplate.save()!
        /**
         * DBAnswer implementation.
         *
         */
        class DBAnswer implements Answer<Object> {
            RecordHash savedRecordHash = null;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String method = invocation.getMethod().getName();
                Object[] args = invocation.getArguments();
                if (method.equals("save")) {
                    savedRecordHash = (RecordHash) args[0];
                    return null;
                } else if (method.equals("findOne")) {
                    if (savedRecordHash == null) {
                        return null;
                    }
                    return new BasicDBObject(savedRecordHash.toKVMap());
                } else if (method.equals("insert")) {
                    savedRecordHash = new RecordHash((Map<String, Object>) args[0]);
                    return null;
                } else if (method.equals("update")) {
                    savedRecordHash = new RecordHash((Map<String, Object>) args[1]);
                    return null;
                }
                else {
                    return null;
                }
            }
        }

            DBAnswer dbAnswer = new DBAnswer();
            // doAnswer(dbAnswer).when(mockMongoTemplate).save(anyObject(), eq("recordHash"));
            doAnswer(dbAnswer).when(mockMongoTemplate).findOne(any(Query.class), any(Class.class), eq("recordHash"));

            when(mockMongoTemplate.getCollection(eq(RECORD_HASH_COLLECTION))).thenReturn(mockedCollection);
            when(mockedCollection.insert(any(BasicDBObject.class))).thenAnswer(dbAnswer);
            when(mockedCollection.update(any(DBObject.class), any(BasicDBObject.class))).thenAnswer(dbAnswer);

            // insert a record not in the db
            String testTenantId = "TestTenant";
            String testRecordHashId = "0123456789abcdef0123456789abcdef01234567_id";

            // Record should not be in the db
            System.out.println("Checking RecordHash reference...");
            RecordHash rh = mockBatchJobMongoDA.findRecordHash(testTenantId, testRecordHashId);
            if (rh != null) {
                System.out.println(ToStringBuilder.reflectionToString(rh, ToStringStyle.MULTI_LINE_STYLE));
            } else {
                System.out.println("...RecordHash is null");
            }
            System.out.println("...Assert value of RecordHash...");
            Assert.assertNull(rh);

            mockBatchJobMongoDA.insertRecordHash(testRecordHashId, "fedcba9876543210fedcba9876543210fedcba98");
            long savedTimestamp =  dbAnswer.savedRecordHash.getUpdated();
            String savedId        =  dbAnswer.savedRecordHash.getId();
            String savedHash      =  dbAnswer.savedRecordHash.getHash();

            //introduce delay between calls so that recordHash timestamp changes.
            try{Thread.sleep(5); } catch (Exception e){e.printStackTrace();}

            // second call to findRecordHash should return non-null since Record is already in db.
            rh = mockBatchJobMongoDA.findRecordHash(testTenantId, testRecordHashId);
            Assert.assertNotNull(rh);

            mockBatchJobMongoDA.updateRecordHash(rh, "aaacba9876543210fedcba9876543210fedcba98");
            long updatedTimestamp = dbAnswer.savedRecordHash.getUpdated();
            String updatedId        = dbAnswer.savedRecordHash.getId();
            String updatedHash      = dbAnswer.savedRecordHash.getHash();
            Assert.assertTrue(savedId.equals(updatedId));

            // The timestamp on the recordHash should have changed after the second call, and the create time should be the same
            Assert.assertTrue(savedTimestamp < updatedTimestamp);
    }

    @Test
    public void testUpdateFileEntryWorkNote() {
        DBObject obj1 = new BasicDBObject();
        List<String> files1 = new ArrayList<String>();
        files1.add("StudentProgram.xml");
        obj1.put("files", files1);
        DBObject obj2 = new BasicDBObject();
        obj2.put("files", new ArrayList<String>());

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("fileEntryLatch")).thenReturn(collection);
        Mockito.when(collection.findAndModify(Mockito.any(DBObject.class), Mockito.any(DBObject.class), Mockito.any(DBObject.class),
                Mockito.anyBoolean(), Mockito.any(DBObject.class), Mockito.anyBoolean(),Mockito.anyBoolean())).thenReturn(obj1, obj2);

        boolean result =  mockBatchJobMongoDA.updateFileEntryLatch(BATCHJOBID, "StudentParent.xml");
        Assert.assertFalse(result);

        result =  mockBatchJobMongoDA.updateFileEntryLatch(BATCHJOBID, "StudentProgram.xml");
        Assert.assertTrue(result);
    }

    public void testFileLatch() {
        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("fileEntryLatch")).thenReturn(collection);
        Mockito.when(collection
                .insert(Matchers.any(DBObject.class), Matchers.any(WriteConcern.class))).thenReturn(null);

        List<String> fileEntries = new ArrayList<String>();
        fileEntries.add("test1.xml");
        fileEntries.add("test2.xml");
        Assert.assertTrue(mockBatchJobMongoDA.createFileLatch(BATCHJOBID, fileEntries));
   }

    @Test
    public void testError(){
        Error error = new Error(null, null, null, null, null, null, new Date(), null, null, null);

        Assert.assertNotNull("BatchJobId should not be null!", error.getBatchJobId());
        Assert.assertNotNull("StageName should not be null", error.getStageName());
        Assert.assertNotNull("ResourceId should not be null", error.getResourceId());
        Assert.assertNotNull("SourceIp should not be null", error.getSourceIp());
        Assert.assertNotNull("Hostname should not be null", error.getHostname());
        Assert.assertNotNull("RecordIdentifier should not be null", error.getRecordIdentifier());
        Assert.assertNotNull("getSeverity should not be null", error.getSeverity());
        Assert.assertNotNull("ErrorType should not be null", error.getErrorType());
        Assert.assertNotNull("ErrorDetail should not be null", error.getErrorDetail());
        Assert.assertNotNull("Timestamp should not be null", error.getTimestamp());

        error.setBatchJobId(null);
        error.setStageName(null);
        error.setResourceId(null);
        error.setSourceIp(null);
        error.setHostname(null);
        error.setRecordIdentifier(null);
        error.setSeverity(null);
        error.setErrorType(null);
        error.setErrorDetail(null);
        error.setTimestamp(null);

        Assert.assertNotNull("BatchJobId should not be null!", error.getBatchJobId());
        Assert.assertNotNull("StageName should not be null", error.getStageName());
        Assert.assertNotNull("ResourceId should not be null", error.getResourceId());
        Assert.assertNotNull("SourceIp should not be null", error.getSourceIp());
        Assert.assertNotNull("Hostname should not be null", error.getHostname());
        Assert.assertNotNull("RecordIdentifier should not be null", error.getRecordIdentifier());
        Assert.assertNotNull("getSeverity should not be null", error.getSeverity());
        Assert.assertNotNull("ErrorType should not be null", error.getErrorType());
        Assert.assertNotNull("ErrorDetail should not be null", error.getErrorDetail());
           Assert.assertNotNull("Timestamp should not be null", error.getTimestamp());
    }

}
