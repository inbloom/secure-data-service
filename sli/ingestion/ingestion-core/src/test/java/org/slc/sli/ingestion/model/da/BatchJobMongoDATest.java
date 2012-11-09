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
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.queues.MessageType;
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
    private static final int DUP_KEY_CODE = 11000;
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
                "hostname", "recordId", BatchJobUtils.getCurrentTimeStamp(), FaultType.TYPE_ERROR.getName(),
                "errorType", "errorDetail");
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

    @Test
    public void testTrueCountdownTransformationLatch() {
        BasicDBObject result = new BasicDBObject();
        result.put("count", 0);

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("transformationLatch")).thenReturn(collection);

        Mockito.when(
                collection.findAndModify(Mockito.any(DBObject.class), Mockito.any(DBObject.class),
                        Mockito.any(DBObject.class), Mockito.anyBoolean(), Mockito.any(DBObject.class),
                        Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(result);

        Assert.assertTrue(mockBatchJobMongoDA.countDownLatch(MessageType.DATA_TRANSFORMATION.name(), BATCHJOBID,
                "student"));
    }

    @Test
    public void testTrueCountdownPersistenceLatch() {
        BasicDBObject result = new BasicDBObject();

        Map<String, Object> student = new HashMap<String, Object>();
        student.put("count", 0);

        List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();
        entities.add(student);

        result.put("entities", entities);

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("persistenceLatch")).thenReturn(collection);

        Mockito.when(
                collection.findAndModify(Mockito.any(DBObject.class), Mockito.any(DBObject.class),
                        Mockito.any(DBObject.class), Mockito.anyBoolean(), Mockito.any(DBObject.class),
                        Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(result);

        Assert.assertTrue(mockBatchJobMongoDA.countDownLatch(MessageType.PERSIST_REQUEST.name(), BATCHJOBID, "student"));
    }

    @Test
    public void testFalseCountdownTransformationLatch() {
        BasicDBObject result = new BasicDBObject();
        result.put("count", 1);

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("transformationLatch")).thenReturn(collection);

        Mockito.when(
                collection.findAndModify(Mockito.any(DBObject.class), Mockito.any(DBObject.class),
                        Mockito.any(DBObject.class), Mockito.anyBoolean(), Mockito.any(DBObject.class),
                        Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(result);

        Assert.assertFalse(mockBatchJobMongoDA.countDownLatch(MessageType.DATA_TRANSFORMATION.name(), BATCHJOBID,
                "student"));
    }

    @Test
    public void testFalseCountdownPersistenceLatch() {
        BasicDBObject result = new BasicDBObject();

        Map<String, Object> student = new HashMap<String, Object>();
        student.put("count", 1);

        List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();
        entities.add(student);

        result.put("entities", entities);

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("persistenceLatch")).thenReturn(collection);

        Mockito.when(
                collection.findAndModify(Mockito.any(DBObject.class), Mockito.any(DBObject.class),
                        Mockito.any(DBObject.class), Mockito.anyBoolean(), Mockito.any(DBObject.class),
                        Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(result);

        Assert.assertFalse(mockBatchJobMongoDA.countDownLatch(MessageType.PERSIST_REQUEST.name(), BATCHJOBID, "student"));
    }

    @Test
    public void testSetPersistenceLatch() {

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("persistenceLatch")).thenReturn(collection);

        Mockito.when(
                collection.findAndModify(Mockito.any(DBObject.class), Mockito.any(DBObject.class),
                        Mockito.any(DBObject.class), Mockito.anyBoolean(), Mockito.any(DBObject.class),
                        Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(null);

        mockBatchJobMongoDA.setPersistenceLatchCount(BATCHJOBID, "student", 1);

        Mockito.verify(collection, Mockito.atMost(1)).findAndModify(Mockito.any(DBObject.class),
                Mockito.any(DBObject.class), Mockito.any(DBObject.class), Mockito.anyBoolean(),
                Mockito.any(DBObject.class), Mockito.anyBoolean(), Mockito.anyBoolean());

    }

    @Test
    public void testCreateTransformationLatch() {
        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("transformationLatch")).thenReturn(collection);

        Mockito.when(collection.insert(Mockito.any(DBObject.class), Mockito.any(WriteConcern.class))).thenReturn(null);

        Assert.assertTrue(mockBatchJobMongoDA.createTransformationLatch(BATCHJOBID, "student", 1));
    }

    @Test
    public void testExceptionCreateTransformationLatch() {
        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("transformationLatch")).thenReturn(collection);
        MongoException exception = new MongoException(DUP_KEY_CODE, "DuplicateKeyException");
        Mockito.when(collection.insert(Mockito.any(DBObject.class), Mockito.any(WriteConcern.class))).thenThrow(
                exception);

        Assert.assertFalse(mockBatchJobMongoDA.createTransformationLatch(BATCHJOBID, "student", 1));
    }

    @Test
    public void testCreatePersistanceWorkNoteCountdownLatch() {

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("persistenceLatch")).thenReturn(collection);

        Mockito.when(collection.insert(Mockito.any(DBObject.class), Mockito.any(WriteConcern.class))).thenReturn(null);

        List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();

        Assert.assertNotNull(mockBatchJobMongoDA.createPersistanceLatch(entities, BATCHJOBID));
    }

    @Test
    public void testExceptionSetStagedEntitiesForJob() {
        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("stagedEntities")).thenReturn(collection);
        MongoException exception = Mockito.mock(MongoException.DuplicateKey.class);
        Mockito.when(collection.insert(Mockito.any(DBObject.class), Mockito.any(WriteConcern.class))).thenThrow(
                exception);

        Mockito.when(exception.getCode()).thenReturn(DUP_KEY_CODE);
        mockBatchJobMongoDA.setStagedEntitiesForJob(new HashSet<IngestionStagedEntity>(), "student");

        Mockito.verify(exception, times(1)).getCode();
    }

    @Test
    public void testSetStagedEntitiesForJob() {

        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(mockMongoTemplate.getCollection("stagedEntities")).thenReturn(collection);
        Mockito.when(collection.insert(Mockito.any(DBObject.class))).thenReturn(null);

        mockBatchJobMongoDA.setStagedEntitiesForJob(new HashSet<IngestionStagedEntity>(), "student");

        Mockito.verify(collection).insert(Mockito.any(DBObject.class), Mockito.any(WriteConcern.class));
    }

    @Test
    public void testRemoveStagedEntityForJob() {

        Map<String, Boolean> entitiesMap = new HashMap<String, Boolean>();
        entitiesMap.put("assessment", Boolean.TRUE);
        entitiesMap.put("student", Boolean.TRUE);

        BasicDBObject result = new BasicDBObject();
        result.put("entities", entitiesMap);

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("stagedEntities")).thenReturn(collection);

        Mockito.when(
                collection.findAndModify(Mockito.any(DBObject.class), Mockito.any(DBObject.class),
                        Mockito.any(DBObject.class), Mockito.anyBoolean(), Mockito.any(DBObject.class),
                        Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(result);

        Assert.assertTrue(mockBatchJobMongoDA.markStagedEntityComplete(BATCHJOBID, "student"));
    }

    private List<Error> createErrorsFromIndex(int errorStartIndex, int numberOfErrors) {
        List<Error> errors = new ArrayList<Error>();

        for (int errorIndex = errorStartIndex; errors.size() < numberOfErrors; errorIndex++) {
            errors.add(new Error(BATCHJOBID, BatchJobStageType.EDFI_PROCESSOR.getName(), "resourceid" + errorIndex,
                    "sourceIp" + errorIndex, "hostname" + errorIndex, "recordId" + errorIndex, BatchJobUtils
                            .getCurrentTimeStamp(), FaultType.TYPE_ERROR.getName(), "errorType" + errorIndex,
                    "errorDetail" + errorIndex));
        }

        return errors;
    }

    @Test
    public void testExceptionAttemptTentantLockForJobRetry() {
        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("tenantJobLock")).thenReturn(collection);
        MongoException exception = Mockito.mock(MongoException.class);
        MongoException duplicateKey = Mockito.mock(MongoException.DuplicateKey.class);

        Mockito.when(collection.insert(Mockito.any(DBObject.class), Mockito.any(WriteConcern.class))).thenThrow(
                exception, exception, duplicateKey);

        Mockito.when(exception.getCode()).thenReturn(11222);
        Mockito.when(duplicateKey.getCode()).thenReturn(11000);

        Assert.assertFalse(mockBatchJobMongoDA.attemptTentantLockForJob(BATCHJOBID, "student"));

        Mockito.verify(collection, times(3)).insert(Mockito.any(DBObject.class), Mockito.any(WriteConcern.class));
    }

    @Test
    public void testExceptionAttemptTentantLockForJob() {
        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("tenantJobLock")).thenReturn(collection);
        MongoException exception = Mockito.mock(MongoException.class);
        Mockito.when(collection.insert(Mockito.any(DBObject.class), Mockito.any(WriteConcern.class))).thenThrow(
                exception);

        Mockito.when(exception.getCode()).thenReturn(11000);

        Assert.assertFalse(mockBatchJobMongoDA.attemptTentantLockForJob(BATCHJOBID, "student"));
    }

    @Test
    public void testAttemptTentantLockForJob() {
        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mockMongoTemplate.getCollection("tenantJobLock")).thenReturn(collection);

        Mockito.when(collection.insert(Mockito.any(DBObject.class), Mockito.any(WriteConcern.class))).thenReturn(null);

        Assert.assertTrue(mockBatchJobMongoDA.attemptTentantLockForJob(BATCHJOBID, "student"));
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
