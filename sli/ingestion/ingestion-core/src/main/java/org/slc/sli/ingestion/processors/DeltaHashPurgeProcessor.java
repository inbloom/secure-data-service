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

package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Resource;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;

/**
 *
 * @author npandey
 *
 */
public class DeltaHashPurgeProcessor extends IngestionProcessor<WorkNote, Resource> {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.DELTA_PROPERTY_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Process the duplicate detection prooperty";

    private static final Logger LOG = LoggerFactory.getLogger(DeltaHashPurgeProcessor.class);

    @Override
    public void process(Exchange exchange, ProcessorArgs<WorkNote> args) {
            String tenantId = TenantContext.getTenantId();

            potentiallyRemoveRecordHash(args.job, tenantId);
    }

    private void potentiallyRemoveRecordHash(NewBatchJob job, String tenantId) {
        String rhMode = job.getProperty(AttributeType.DUPLICATE_DETECTION.getName());
        if ((null != rhMode)
            && (rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DISABLE) || rhMode
                    .equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_RESET))) {
            LOG.info("@duplicate-detection mode '" + rhMode + "' given: resetting recordHash");
            batchJobDAO.removeRecordHashByTenant(tenantId);
        }
    }

    @Override
    protected BatchJobStageType getStage() {
        return BATCH_JOB_STAGE;
    }

    @Override
    protected String getStageDescription() {
        return BATCH_JOB_STAGE_DESC;
    }
}
