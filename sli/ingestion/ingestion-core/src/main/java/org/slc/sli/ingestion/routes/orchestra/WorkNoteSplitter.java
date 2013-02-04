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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 * WorkNote splitter to be used from camel
 *
 * @author dduran
 *
 */
@Component
public class WorkNoteSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(WorkNoteSplitter.class);

    public static final String BATCH_JOB_STAGE_DESC = "Splits the work that can be processed in parallel";

    @Autowired
    private SplitStrategy balancedTimestampSplitStrategy;

    @Autowired
    private BatchJobDAO batchJobDAO;

    /**
     * Splits the work that can be processed in parallel next round into individual WorkNotes.
     *
     * @param exchange
     * @return list of WorkNotes that camel will iterate over, issuing each as a new message
     * @throws IllegalStateException
     */
    public List<RangedWorkNote> splitTransformationWorkNotes(Exchange exchange) {

        Stage stage = Stage.createAndStartStage(BatchJobStageType.WORKNOTE_SPLITTER, BATCH_JOB_STAGE_DESC);

        String jobId = null;
        List<RangedWorkNote> workNoteList = null;
        try {
            jobId = exchange.getIn().getBody(WorkNote.class).getBatchJobId();
            TenantContext.setJobId(jobId);
            LOG.info("orchestrating splitting for job: {}", jobId);

            workNoteList = createNextTierWorkNotes(jobId);

        } finally {
            if (jobId != null) {
                stage.stopStage();
                batchJobDAO.saveBatchJobStage(jobId, stage);
            }
        }

        return workNoteList;
    }

    private List<RangedWorkNote> createNextTierWorkNotes(String jobId) {
        List<RangedWorkNote> workNoteList;
        Set<IngestionStagedEntity> stagedEntities = batchJobDAO.getStagedEntitiesForJob(jobId);

        if (stagedEntities.size() == 0) {
            throw new IllegalStateException(
                    "stagedEntities is empty at splitting stage. should have been redirected prior to this point.");
        }

        Set<IngestionStagedEntity> nextTierEntities = IngestionStagedEntity.cleanse(stagedEntities);

        workNoteList = createWorkNotes(nextTierEntities, jobId);
        return workNoteList;
    }

    private List<RangedWorkNote> createWorkNotes(Set<IngestionStagedEntity> stagedEntities, String jobId) {
        LOG.info("creating WorkNotes for processable entities: {}", stagedEntities);
        List<Map<String, Object>> defaultPersistenceLatch = new ArrayList<Map<String, Object>>();

        List<RangedWorkNote> workNoteList = new ArrayList<RangedWorkNote>();
        for (IngestionStagedEntity stagedEntity : stagedEntities) {

            List<RangedWorkNote> workNotesForEntity = balancedTimestampSplitStrategy.splitForEntity(stagedEntity);

            batchJobDAO.createTransformationLatch(jobId, stagedEntity.getCollectionNameAsStaged(),
                    workNotesForEntity.size());

            Map<String, Object> entity = new HashMap<String, Object>();
            entity.put("count", 1);
            entity.put("type", stagedEntity.getCollectionNameAsStaged());
            defaultPersistenceLatch.add(entity);

            workNoteList.addAll(workNotesForEntity);
        }

        batchJobDAO.createPersistanceLatch(defaultPersistenceLatch, jobId);

        LOG.info("{} total WorkNotes created and ready for splitting for current tier.", workNoteList.size());
        return workNoteList;
    }

    public List<RangedWorkNote> splitPersistanceWorkNotes(Exchange exchange) {
        RangedWorkNote workNote = exchange.getIn().getBody(RangedWorkNote.class);
        IngestionStagedEntity stagedEntity = workNote.getIngestionStagedEntity();
        List<RangedWorkNote> workNoteList = new ArrayList<RangedWorkNote>();

        String jobId = exchange.getIn().getBody(WorkNote.class).getBatchJobId();
        TenantContext.setJobId(jobId);

        LOG.debug("Splitting out (pass-through) list of WorkNotes: {}", workNoteList);
        List<RangedWorkNote> workNotesForEntity = balancedTimestampSplitStrategy.splitForEntity(stagedEntity);
        batchJobDAO
                .setPersistenceLatchCount(jobId, stagedEntity.getCollectionNameAsStaged(), workNotesForEntity.size());

        workNoteList.addAll(workNotesForEntity);
        return workNoteList;
    }

}
