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

package org.slc.sli.ingestion.routes.orchestra.parsing;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;

/**
 * Tests the FileEntryLatch
 * @author ablum
 *
 */
public class FileEntryLatchTest {
    private static final String FILE_ENTRY_LATCH = "fileEntryLatch";

    @InjectMocks
    FileEntryLatch fileEntryLatch = new FileEntryLatch();

    @Mock
    BatchJobMongoDA batchJobDAO;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(batchJobDAO.updateFileEntryLatch(Mockito.anyString(), Mockito.anyString())).thenReturn(false, true);
    }

    @Test
    public void testReceive() throws Exception {
        Exchange exchange =  new DefaultExchange(new DefaultCamelContext());
        IngestionFileEntry entry = new IngestionFileEntry("/", FileFormat.EDFI_XML, FileType.XML_STUDENT_PROGRAM, "fileName", "111");
        FileEntryWorkNote workNote = new FileEntryWorkNote("batchJobId", "SLI", entry);

        exchange.getIn().setBody(workNote, FileEntryWorkNote.class);

        fileEntryLatch.receive(exchange);

        Assert.assertEquals(false, exchange.getIn().getHeader("fileEntryLatchOpened"));

        fileEntryLatch.receive(exchange);

        Assert.assertEquals(true, exchange.getIn().getHeader("fileEntryLatchOpened"));
    }

}
