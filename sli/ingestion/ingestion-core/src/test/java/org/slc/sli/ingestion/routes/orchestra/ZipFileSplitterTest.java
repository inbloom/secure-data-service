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
import java.util.List;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 * @author tke
 *
 */
public class ZipFileSplitterTest {

    private ZipFileSplitter  zipFileSplitter = Mockito.mock(ZipFileSplitter.class);
    private BatchJobDAO batchJobDAO = Mockito.mock(BatchJobDAO.class);
    ControlFile controlFile = Mockito.mock(ControlFile.class);

    @Test
    public void test() {

        List<IngestionFileEntry> files = new ArrayList<IngestionFileEntry>();
        files.add(new IngestionFileEntry("/", null, null, "test1.xml", null));
        files.add(new IngestionFileEntry("/", null, null, "test2.xml", null));

        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader("jobId")).thenReturn("1");
        Mockito.when(message.getHeader("BatchJobId")).thenReturn("test");
        Mockito.when(message.getHeader("controlFile")).thenReturn(controlFile);
        Mockito.when(controlFile.getFileEntries()).thenReturn(files);

        Mockito.doCallRealMethod().when(zipFileSplitter).splitZipFile(exchange);
        Mockito.doCallRealMethod().when(zipFileSplitter).setBatchJobDAO(Matchers.any(BatchJobDAO.class));
        Mockito.when(batchJobDAO.createFileLatch(Matchers.anyString(), Matchers.any(List.class))).thenReturn(true);

        zipFileSplitter.setBatchJobDAO(batchJobDAO);
        List<FileEntryWorkNote> res = zipFileSplitter.splitZipFile(exchange);


        Assert.assertEquals(res.size(), 2);
    }

}
