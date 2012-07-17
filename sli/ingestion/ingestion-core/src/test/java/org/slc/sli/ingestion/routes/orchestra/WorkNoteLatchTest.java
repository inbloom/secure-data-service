package org.slc.sli.ingestion.routes.orchestra;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class WorkNoteLatchTest {

    @InjectMocks
    @Autowired
    WorkNoteLatch workNoteLatch;

    @Mock
    BatchJobMongoDA mockBatchJobMongoDA;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRecieve() throws Exception {
        Mockito.when(mockBatchJobMongoDA.countDownWorkNoteLatch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader("IngestionMessageType")).thenReturn(MessageType.PERSIST_REQUEST.name());
        Mockito.when(message.getBody(WorkNote.class)).thenReturn(WorkNote.createBatchedWorkNote("", IngestionStagedEntity.createFromRecordType("student"), 0, 0, 0, 0));

        workNoteLatch.receive(exchange);

        Mockito.verify(message).setHeader("latchOpened", true);
    }

}
