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

package org.slc.sli.ingestion;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 *
 * @author slee
 *
 */
@Component
public class IngestionFileEntryLatch
{
    private static final String FILE_ENTRY_FILE_TYPE = "FileEntryWorkNoteFileType";
    private static final String FILE_ENTRY_FILE_NAME = "IngestionFileEntryFileName";
    private static final String FILE_ENTRY_BATCH_JOB_ID = "IngestionFileEntryBatchJobId";

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Handler
    public void receive(Exchange exchange) throws Exception {

        FileEntryWorkNote workNote = exchange.getIn().getBody(FileEntryWorkNote.class);
        String batchJobId = workNote.getBatchJobId();
        TenantContext.setJobId(batchJobId);

        exchange.getIn().setHeader(FILE_ENTRY_FILE_TYPE, workNote.getFileEntry().getFileType().getName());
        exchange.getIn().setHeader(FILE_ENTRY_FILE_NAME, workNote.getFileEntry().getFileName());
        exchange.getIn().setHeader(FILE_ENTRY_BATCH_JOB_ID, workNote.getFileEntry().getBatchJobId());
    }

}
