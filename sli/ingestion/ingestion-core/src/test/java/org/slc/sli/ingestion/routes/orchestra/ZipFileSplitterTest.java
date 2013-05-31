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
package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import org.slc.sli.ingestion.ControlFileWorkNote;
import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordAccess;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
/**
 * @author tke
 *
 */
public class ZipFileSplitterTest {

    private ZipFileSplitter  zipFileSplitter = Mockito.mock(ZipFileSplitter.class);
    private BatchJobDAO batchJobDAO = Mockito.mock(BatchJobDAO.class);
    ControlFileWorkNote controlFileWorkNote = Mockito.mock(ControlFileWorkNote.class);
    ControlFile controlFile = Mockito.mock(ControlFile.class);
    NewBatchJob newBatchJob = Mockito.mock(NewBatchJob.class);
    private NeutralRecordAccess neutralRecordMongoAccess = Mockito.mock(NeutralRecordAccess.class);
    private String jobId = "111111111-222222222-33333333-444444444";

    @Test
    public void test() {

        List<IngestionFileEntry> files = new ArrayList<IngestionFileEntry>();
        files.add(new IngestionFileEntry("/", null, null, "test1.xml", null));
        files.add(new IngestionFileEntry("/", null, null, "test2.xml", null));

        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader("jobId")).thenReturn(jobId);
        Mockito.when(message.getHeader("BatchJobId")).thenReturn(jobId);
        Mockito.when(message.getBody(WorkNote.class)).thenReturn(controlFileWorkNote);
        Mockito.when(controlFileWorkNote.getBatchJobId()).thenReturn(jobId);

        Mockito.doCallRealMethod().when(zipFileSplitter).splitZipFile(exchange);
        Mockito.doCallRealMethod().when(zipFileSplitter).setBatchJobDAO(Matchers.any(BatchJobDAO.class));
        Mockito.doCallRealMethod().when(zipFileSplitter).setNeutralRecordMongoAccess(neutralRecordMongoAccess);
        Mockito.when(batchJobDAO.createFileLatch(Matchers.anyString(), Matchers.any(List.class))).thenReturn(true);
        Mockito.when(batchJobDAO.findBatchJobById(Matchers.anyString())).thenReturn(newBatchJob);
        Mockito.when(newBatchJob.getFiles()).thenReturn(files);

        zipFileSplitter.setBatchJobDAO(batchJobDAO);
        zipFileSplitter.setNeutralRecordMongoAccess(neutralRecordMongoAccess);
        List<FileEntryWorkNote> res = zipFileSplitter.splitZipFile(exchange);

        Mockito.verify(neutralRecordMongoAccess, Mockito.times(1)).ensureIndexes();

        Assert.assertEquals(res.size(), 2);
    }

}
