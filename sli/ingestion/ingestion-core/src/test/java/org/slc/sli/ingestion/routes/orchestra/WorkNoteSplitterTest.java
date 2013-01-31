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

package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.queues.MessageType;

/**
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class WorkNoteSplitterTest {
    @InjectMocks
    @Autowired
    WorkNoteSplitter workNoteSplitter;

    @Mock
    BatchJobMongoDA mockBatchJobMongoDA;

    @Mock
    SplitStrategy balancedTimestampSplitStrategy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSplitTransformationWorkNotes() {
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        WorkNote workNote = Mockito.mock(WorkNote.class);

        Mockito.when(workNote.getBatchJobId()).thenReturn("1");
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getBody(WorkNote.class)).thenReturn(workNote);

        Set<IngestionStagedEntity> set = new HashSet<IngestionStagedEntity>();
        set.add(IngestionStagedEntity.createFromRecordType("student"));
        Mockito.when(mockBatchJobMongoDA.getStagedEntitiesForJob("1")).thenReturn(set);
        Mockito.when(mockBatchJobMongoDA.createTransformationLatch("1", "student", 0)).thenReturn(true);

        List<RangedWorkNote> list = new ArrayList<RangedWorkNote>();
        list.add(RangedWorkNote.createBatchedWorkNote("", IngestionStagedEntity.createFromRecordType("student"), 0, 0, 0, 0));
        Mockito.when(balancedTimestampSplitStrategy.splitForEntity(IngestionStagedEntity.createFromRecordType("student"))).thenReturn(list);

        List<RangedWorkNote> result = workNoteSplitter.splitTransformationWorkNotes(exchange);

        Assert.assertEquals(list, result);
        Mockito.verify(balancedTimestampSplitStrategy, Mockito.times(1)).splitForEntity(Mockito.any(IngestionStagedEntity.class));
        Mockito.verify(mockBatchJobMongoDA, Mockito.times(1)).createTransformationLatch(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateException() {
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        WorkNote workNote = Mockito.mock(WorkNote.class);

        Mockito.when(workNote.getBatchJobId()).thenReturn("1");
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getBody(WorkNote.class)).thenReturn(workNote);

        Set<IngestionStagedEntity> set = new HashSet<IngestionStagedEntity>();
        Mockito.when(mockBatchJobMongoDA.getStagedEntitiesForJob("1")).thenReturn(set);

        workNoteSplitter.splitTransformationWorkNotes(exchange);
    }

    @Test
    public void testSplitPersistanceWorkNotes() {

        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        WorkNote workNote = Mockito.mock(WorkNote.class);

        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(workNote.getBatchJobId()).thenReturn("1");
        Mockito.when(message.getBody(WorkNote.class)).thenReturn(workNote);

        Mockito.when(message.getHeader("IngestionMessageType")).thenReturn(MessageType.PERSIST_REQUEST.name());

        Mockito.when(message.getBody(RangedWorkNote.class)).thenReturn(RangedWorkNote.createBatchedWorkNote("", IngestionStagedEntity.createFromRecordType("student"), 0, 0, 0, 0));




        List<RangedWorkNote> list = new ArrayList<RangedWorkNote>();
        list.add(RangedWorkNote.createBatchedWorkNote("", IngestionStagedEntity.createFromRecordType("student"), 0, 0, 0, 0));
        Mockito.when(balancedTimestampSplitStrategy.splitForEntity(IngestionStagedEntity.createFromRecordType("student"))).thenReturn(list);

        List<RangedWorkNote> result = workNoteSplitter.splitPersistanceWorkNotes(exchange);

        Assert.assertEquals(list, result);
        Mockito.verify(balancedTimestampSplitStrategy, Mockito.times(1)).splitForEntity(Mockito.any(IngestionStagedEntity.class));
        Mockito.verify(mockBatchJobMongoDA, Mockito.times(1)).setPersistenceLatchCount(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
    }

}
