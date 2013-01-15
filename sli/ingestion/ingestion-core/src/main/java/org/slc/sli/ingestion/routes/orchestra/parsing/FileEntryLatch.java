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

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 *
 * @author ablum
 *
 */
@Component
public class FileEntryLatch {

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Handler
    public void receive(Exchange exchange) throws Exception {
        exchange.getIn().setHeader("fileEntryLatchOpened", false);

        FileEntryWorkNote workNote = exchange.getIn().getBody(FileEntryWorkNote.class);

        TenantContext.setJobId(workNote.getBatchJobId());

        if (batchJobDAO.updateFileEntryLatch(workNote.getBatchJobId(), workNote.getFileEntry().getFileName())) {

            exchange.getIn().setHeader("fileEntryLatchOpened", true);

        }
    }
}
