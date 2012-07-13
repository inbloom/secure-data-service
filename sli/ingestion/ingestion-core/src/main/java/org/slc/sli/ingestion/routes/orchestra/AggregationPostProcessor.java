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

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 *
 * @author dduran
 *
 */
@Component
public class AggregationPostProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AggregationPostProcessor.class);

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void process(Exchange exchange) throws Exception {

        LOG.info("Aggregation completed for current tier. Will now remove entities in tier from processing pool.");
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        String jobId = workNote.getBatchJobId();
        @SuppressWarnings("unchecked")

        boolean isEmpty = removeWorkNoteFromRemainingEntities(jobId, workNote);

        exchange.getIn().setHeader("jobId", jobId);

        if (isEmpty) {
            LOG.info("Processing pool is now empty, continue out of orchestra routes.");

            exchange.getIn().setHeader("processedAllStagedEntities", true);
            workNote = WorkNote.createSimpleWorkNote(jobId);
            exchange.getIn().setBody(workNote);
        }
    }

    private boolean removeWorkNoteFromRemainingEntities(String jobId, WorkNote workNote) {
        List<String> finishedEntities = batchJobDAO.getPersistedWorkNotes(jobId);
        boolean isEmpty = false;
        for (String recordType : finishedEntities) {
            isEmpty = batchJobDAO.removeStagedEntityForJob(recordType, jobId);

            LOG.info("removed EdfiEntity from processing pool: {}", recordType);
        }

        return isEmpty;
    }

}
