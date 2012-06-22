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


package org.slc.sli.ingestion.processors;

import java.util.HashSet;
import java.util.Set;

import junitx.util.PrivateAccessor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 *
 * @author npandey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class PurgeProcessorTest {

    private static final String BATCHJOBID = "MT.ctl-1234235235";
    @InjectMocks
    @Autowired
    private PurgeProcessor purgeProcessor;

    @Mock
    private BatchJobDAO mockBatchJobDAO;

    @Mock
    private MongoTemplate mongoTemplate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNoTenantId() throws Exception {

        WorkNote mockWorkNote = Mockito.mock(WorkNote.class);
        Mockito.when(mockWorkNote.getBatchJobId()).thenReturn(BATCHJOBID);

        Exchange ex = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(ex.getIn()).thenReturn(message);
        Mockito.when(message.getBody(WorkNote.class)).thenReturn(mockWorkNote);

        NewBatchJob job = new NewBatchJob();
        Mockito.when(mockBatchJobDAO.findBatchJobById(BATCHJOBID)).thenReturn(job);

        Logger log = Mockito.mock(org.slf4j.Logger.class);
        PrivateAccessor.setField(purgeProcessor, "logger", log);

        purgeProcessor.process(ex);
        Mockito.verify(log, Mockito.atLeastOnce()).info("TenantId missing. No purge operation performed.");
    }

    @Test
    public void testPurging() throws Exception {

        WorkNote mockWorkNote = Mockito.mock(WorkNote.class);
        Mockito.when(mockWorkNote.getBatchJobId()).thenReturn(BATCHJOBID);

        Exchange ex = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(ex.getIn()).thenReturn(message);
        Mockito.when(message.getBody(WorkNote.class)).thenReturn(mockWorkNote);

        NewBatchJob job = new NewBatchJob();
        job.setProperty("tenantId", "SLI");
        Mockito.when(mockBatchJobDAO.findBatchJobById(BATCHJOBID)).thenReturn(job);

        Set<String> collectionNames = new HashSet<String>();
        collectionNames.add("student");
        collectionNames.add("teacher");

        Mockito.when(mongoTemplate.getCollectionNames()).thenReturn(collectionNames);

        purgeProcessor.process(ex);

        Mockito.verify(mongoTemplate, Mockito.atLeastOnce()).remove(Mockito.any(Query.class), Mockito.eq("student"));
    }

    @Test
    public void testPurgingSystemCollections() throws Exception {

        WorkNote mockWorkNote = Mockito.mock(WorkNote.class);
        Mockito.when(mockWorkNote.getBatchJobId()).thenReturn(BATCHJOBID);

        Exchange ex = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(ex.getIn()).thenReturn(message);
        Mockito.when(message.getBody(WorkNote.class)).thenReturn(mockWorkNote);

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
