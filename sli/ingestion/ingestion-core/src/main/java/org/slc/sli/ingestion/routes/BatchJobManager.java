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

import org.slc.sli.ingestion.WorkNote;
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

    public boolean isDryRun(Exchange exchange) {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        String jobId = workNote.getBatchJobId();
        return batchJobDAO.isDryRun(jobId);
    }

    public boolean hasErrors(Exchange exchange) {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        return workNote.hasErrors();
    }

    public boolean isDuplicateDetection(Exchange exchange) {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        String jobId = workNote.getBatchJobId();
        return batchJobDAO.isDuplicateDetection(jobId);
    }
}
