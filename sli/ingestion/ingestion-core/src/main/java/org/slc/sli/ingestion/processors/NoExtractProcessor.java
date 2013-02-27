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

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 * Processor for no extract route used for performance measuring
 *
 * @author dduran
 *
 */
@Component
public class NoExtractProcessor implements Processor {

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void process(Exchange exchange) throws Exception {

        File file = exchange.getIn().getBody(File.class);
        String batchJobId = file.getName().substring(0, file.getName().indexOf(".noextract"));
        exchange.getIn().setHeader("BatchJobId", batchJobId);

        NewBatchJob job = new NewBatchJob(batchJobId);
        TenantContext.setTenantId(job.getTenantId());


        job.setStatus(BatchJobStatusType.RUNNING.getName());
        job.setSourceId(file.getParentFile().getAbsolutePath() + File.separator);
        batchJobDAO.saveBatchJob(job);

        WorkNote workNote = WorkNote.createSimpleWorkNote(batchJobId);
        exchange.getIn().setBody(workNote, WorkNote.class);

    }
}
