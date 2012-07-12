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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.WorkNote;

/**
 *
 * @author dduran
 *
 */
@Component
public class AggregationPostProcessor implements Processor {

    @Autowired
    private StagedEntityTypeDAO stagedEntityTypeDAO;

    @Override
    public void process(Exchange exchange) throws Exception {

        info("Aggregation completed for current tier. Will now remove entities in tier from processing pool.");

        @SuppressWarnings("unchecked")
        List<WorkNote> workNoteList = exchange.getIn().getBody(List.class);

        String jobId = null;

        for (WorkNote workNote : workNoteList) {

            // all of these WorkNotes should have the same batchjobid, since this is the aggregation
            // criteria. grabbing it out of the first one will suffice.
            if (jobId == null) {
                jobId = workNote.getBatchJobId();
            }

            removeWorkNoteFromRemainingEntities(jobId, workNote);
        }
        exchange.getIn().setHeader("jobId", jobId);

        if (stagedEntityTypeDAO.getStagedEntitiesForJob(jobId).size() == 0) {
            info("Processing pool is now empty, continue out of orchestra routes.");

            exchange.getIn().setHeader("processedAllStagedEntities", true);
            WorkNote workNote = WorkNote.createSimpleWorkNote(jobId);
            exchange.getIn().setBody(workNote);
        }
    }

    private void removeWorkNoteFromRemainingEntities(String jobId, WorkNote workNote) {

        IngestionStagedEntity stagedEntityToRemove = workNote.getIngestionStagedEntity();

        if (stagedEntityTypeDAO.removeStagedEntityForJob(stagedEntityToRemove, jobId)) {
            info("removed EdfiEntity from processing pool: {}", stagedEntityToRemove.getCollectionNameAsStaged());
        }
    }

}
