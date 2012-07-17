package org.slc.sli.ingestion.routes.orchestra;

import static org.junit.Assert.*;

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
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.validation.EntityValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    public void testSplit() {
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader("jobId")).thenReturn("1");

        Set<IngestionStagedEntity> set = new HashSet<IngestionStagedEntity>();
        set.add(IngestionStagedEntity.createFromRecordType("student"));
        Mockito.when(mockBatchJobMongoDA.getStagedEntitiesForJob("1")).thenReturn(set);
        Mockito.when(mockBatchJobMongoDA.createWorkNoteCountdownLatch(MessageType.DATA_TRANSFORMATION.name(), "1","student", 0)).thenReturn(true);

        List<WorkNote> list = new ArrayList<WorkNote>();
        list.add(WorkNote.createBatchedWorkNote("", IngestionStagedEntity.createFromRecordType("student"), 0, 0, 0, 0));
        Mockito.when(balancedTimestampSplitStrategy.splitForEntity(IngestionStagedEntity.createFromRecordType("student"), "1")).thenReturn(list);

        List<WorkNote> result = workNoteSplitter.split(exchange);

        Assert.assertEquals(list, result);
        Mockito.verify(balancedTimestampSplitStrategy, Mockito.times(1)).splitForEntity(Mockito.any(IngestionStagedEntity.class), Mockito.anyString());
        Mockito.verify(mockBatchJobMongoDA, Mockito.times(1)).createWorkNoteCountdownLatch(Mockito.anyString(), Mockito.anyString(),Mockito.anyString(), Mockito.anyInt());
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateException() {
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader("jobId")).thenReturn("1");

        Set<IngestionStagedEntity> set = new HashSet<IngestionStagedEntity>();
        Mockito.when(mockBatchJobMongoDA.getStagedEntitiesForJob("1")).thenReturn(set);

        workNoteSplitter.split(exchange);
    }

    @Test
    public void testPassThroughSplit() {

        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader("IngestionMessageType")).thenReturn(MessageType.PERSIST_REQUEST.name());

        Mockito.when(message.getBody(WorkNote.class)).thenReturn(WorkNote.createBatchedWorkNote("", IngestionStagedEntity.createFromRecordType("student"), 0, 0, 0, 0));

        Mockito.when(message.getHeader("jobId")).thenReturn("1");

        Mockito.when(mockBatchJobMongoDA.createWorkNoteCountdownLatch(MessageType.DATA_TRANSFORMATION.name(), "1","student", 0)).thenReturn(true);

        List<WorkNote> list = new ArrayList<WorkNote>();
        list.add(WorkNote.createBatchedWorkNote("", IngestionStagedEntity.createFromRecordType("student"), 0, 0, 0, 0));
        Mockito.when(balancedTimestampSplitStrategy.splitForEntity(IngestionStagedEntity.createFromRecordType("student"), "1")).thenReturn(list);

        List<WorkNote> result = workNoteSplitter.passThroughSplit(exchange);

        Assert.assertEquals(list, result);
        Mockito.verify(balancedTimestampSplitStrategy, Mockito.times(1)).splitForEntity(Mockito.any(IngestionStagedEntity.class), Mockito.anyString());
        Mockito.verify(mockBatchJobMongoDA, Mockito.times(1)).createWorkNoteCountdownLatch(Mockito.anyString(), Mockito.anyString(),Mockito.anyString(), Mockito.anyInt());


    }

}
