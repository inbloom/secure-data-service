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
package org.slc.sli.ingestion.routes;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 *
 * @author npandey
 *
 */

@Component
public final class BatchJobManager {

    @Autowired
    private BatchJobDAO batchJobDAO;

    public void prepareTenantContext(Exchange exchange) {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        String tenantId = null;
        if (workNote != null) {
            NewBatchJob currentJob = batchJobDAO.findBatchJobById(workNote.getBatchJobId());
            tenantId = currentJob==null ? null : currentJob.getTenantId();
        }
        TenantContext.setTenantId(tenantId);
    }

    public boolean isDryRun(Exchange exchange) {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        String jobId = workNote.getBatchJobId();
        return batchJobDAO.isDryRun(jobId);
    }

    public boolean hasErrors(Exchange exchange) {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        return workNote.hasErrors();
    }

    public boolean isEligibleForDeltaPurge(Exchange exchange) {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        String jobId = workNote.getBatchJobId();
        String deltaMode =  batchJobDAO.getDuplicateDetectionMode(jobId);

        if(deltaMode != null && isDeltaPurgeMode(deltaMode)) {
            return true;
        }

        return false;
    }

    private boolean isDeltaPurgeMode(String mode) {
        if((mode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DISABLE) || mode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_RESET))) {
            return true;
        }
            return false;
    }
}
