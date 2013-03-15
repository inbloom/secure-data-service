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

package org.slc.sli.ingestion.processors;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junitx.util.PrivateAccessor;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;

/**
 *
 * @author npandey
 *
 */
public class PurgeProcessorTest {

    private static final String BATCHJOBID = "MT.ctl-1234235235";

    private PurgeProcessor purgeProcessor;

    @Mock
    private BatchJobDAO mockBatchJobDAO;

    @Mock
    private MongoTemplate mongoTemplate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        purgeProcessor = new PurgeProcessor();

        purgeProcessor.setBatchJobDAO(mockBatchJobDAO);
        purgeProcessor.setMongoTemplate(mongoTemplate);
        purgeProcessor.setSandboxEnabled(false);

        List<String> exclude = Collections.emptyList();

        purgeProcessor.setExcludeCollections(exclude);
    }

    @Test
    public void testNoTenantId() throws Exception {

        RangedWorkNote workNote = RangedWorkNote.createSimpleWorkNote(BATCHJOBID);

        Exchange ex = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(ex.getIn()).thenReturn(message);
        Mockito.when(message.getBody(WorkNote.class)).thenReturn(workNote);

        NewBatchJob job = new NewBatchJob();
        Mockito.when(mockBatchJobDAO.findBatchJobById(BATCHJOBID)).thenReturn(job);

        AbstractMessageReport messageReport = Mockito.mock(AbstractMessageReport.class);
        purgeProcessor.setMessageReport(messageReport);

        purgeProcessor.process(ex);
        Mockito.verify(messageReport, Mockito.atLeastOnce()).error(Matchers.any(ReportStats.class),
                Matchers.any(Source.class), Matchers.eq(CoreMessageCode.CORE_0035));
    }

    @Test
    public void testPurging() throws Exception {

        RangedWorkNote workNote = RangedWorkNote.createSimpleWorkNote(BATCHJOBID);

        PrivateAccessor.setField(purgeProcessor, "purgeBatchSize", 2);

        Exchange ex = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(ex.getIn()).thenReturn(message);
        Mockito.when(message.getBody(WorkNote.class)).thenReturn(workNote);

        NewBatchJob job = new NewBatchJob();
        job.setTenantId("SLI");
        Mockito.when(mockBatchJobDAO.findBatchJobById(BATCHJOBID)).thenReturn(job);

        Set<String> collectionNames = new HashSet<String>();
        collectionNames.add("student");

        Mockito.when(mongoTemplate.getCollectionNames()).thenReturn(collectionNames);

        DBCollection studentCollection = Mockito.mock(DBCollection.class);

        Mockito.when(mongoTemplate.getCollection(Mockito.eq("student"))).thenReturn(studentCollection);

        DBCursor cursor = Mockito.mock(DBCursor.class);

        DBCursor firstCursor = Mockito.mock(DBCursor.class);
        Mockito.when(firstCursor.size()).thenReturn(2);
        Mockito.when(firstCursor.hasNext()).thenReturn(true, true, false);
        Mockito.when(firstCursor.next()).thenReturn(new BasicDBObject("_id", "123"), new BasicDBObject("_id", "456"));

        DBCursor secondCursor = Mockito.mock(DBCursor.class);
        Mockito.when(secondCursor.size()).thenReturn(1);
        Mockito.when(secondCursor.hasNext()).thenReturn(true, false);
        Mockito.when(secondCursor.next()).thenReturn(new BasicDBObject("_id", "678"));

        DBCursor thirdCursor = Mockito.mock(DBCursor.class);
        Mockito.when(thirdCursor.size()).thenReturn(0);
        Mockito.when(thirdCursor.hasNext()).thenReturn(false);

        Mockito.when(studentCollection.find(Mockito.any(BasicDBObject.class), Mockito.any(BasicDBObject.class))).thenReturn(cursor);
        Mockito.when(cursor.limit(2)).thenReturn(firstCursor, secondCursor, thirdCursor);

        purgeProcessor.process(ex);

        Mockito.verify(studentCollection, Mockito.atLeast(2)).remove(Mockito.any(DBObject.class));
    }

    @Test
    public void testPurgingSystemCollections() throws Exception {

        RangedWorkNote workNote = RangedWorkNote.createSimpleWorkNote(BATCHJOBID);

        Exchange ex = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(ex.getIn()).thenReturn(message);
        Mockito.when(message.getBody(RangedWorkNote.class)).thenReturn(workNote);

        NewBatchJob job = new NewBatchJob();
        job.setProperty("tenantId", "SLI");
        Mockito.when(mockBatchJobDAO.findBatchJobById(BATCHJOBID)).thenReturn(job);

        Set<String> collectionNames = new HashSet<String>();
        collectionNames.add("system.js");
        collectionNames.add("system.indexes");

        Mockito.when(mongoTemplate.getCollectionNames()).thenReturn(collectionNames);

        purgeProcessor.process(ex);

        Mockito.verify(mongoTemplate, Mockito.never()).remove(Mockito.any(Query.class), Mockito.eq("system.js"));
    }
}
