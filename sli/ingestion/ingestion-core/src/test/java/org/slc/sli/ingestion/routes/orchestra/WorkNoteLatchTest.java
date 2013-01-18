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
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.queues.MessageType;

/**
 *
 * @author ablum
 *
 */
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
        Mockito.when(mockBatchJobMongoDA.countDownLatch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader("IngestionMessageType")).thenReturn(MessageType.PERSIST_REQUEST.name());
        Mockito.when(message.getBody(RangedWorkNote.class)).thenReturn(RangedWorkNote.createBatchedWorkNote("", IngestionStagedEntity.createFromRecordType("student"), 0, 0, 0, 0));

        workNoteLatch.receive(exchange);

        Mockito.verify(message).setHeader("latchOpened", true);
    }

}
