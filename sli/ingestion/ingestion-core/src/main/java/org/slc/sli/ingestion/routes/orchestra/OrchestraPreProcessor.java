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

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.EdfiEntity;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 *
 * @author dduran
 *
 */
@Component
public class OrchestraPreProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(OrchestraPreProcessor.class);

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Override
    public void process(Exchange exchange) throws Exception {

        RangedWorkNote workNote = exchange.getIn().getBody(RangedWorkNote.class);

        String jobId = workNote.getBatchJobId();
        exchange.getIn().setHeader("jobId", jobId);

        TenantContext.setJobId(jobId);

        LOG.info("Looking up staged entities for batch job: {}", jobId);

        Set<String> stagedCollectionNames = neutralRecordMongoAccess.getRecordRepository().getStagedCollectionsForJob();

        Set<IngestionStagedEntity> stagedEntities = constructStagedEntities(stagedCollectionNames);

        if (stagedEntities.size() > 0) {
            batchJobDAO.setStagedEntitiesForJob(stagedEntities, jobId);
        } else {
            exchange.getIn().setHeader("stagedEntitiesEmpty", true);
        }
    }

    private Set<IngestionStagedEntity> constructStagedEntities(Set<String> stagedCollectionNames) {

        Set<IngestionStagedEntity> stagedEntities = new HashSet<IngestionStagedEntity>();
        for (String stagedCollection : stagedCollectionNames) {

            EdfiEntity stagedEdfiEntity = EdfiEntity.fromEntityName(stagedCollection);
            if (stagedEdfiEntity != null) {

                IngestionStagedEntity ingestionStagedEntity = new IngestionStagedEntity(stagedCollection,
                        stagedEdfiEntity);

                stagedEntities.add(ingestionStagedEntity);

            } else {
                LOG.warn("Unrecognized collection: {} dropping it on the floor", stagedCollection);
            }
        }
        LOG.debug("staged entities for job: {}", stagedEntities);
        return stagedEntities;
    }

}
