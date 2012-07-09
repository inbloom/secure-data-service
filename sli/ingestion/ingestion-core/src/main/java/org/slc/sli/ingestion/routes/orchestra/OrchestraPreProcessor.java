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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.EdfiEntity;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 *
 * @author dduran
 *
 */
@Component
public class OrchestraPreProcessor implements Processor {

    //private static final Logger LOG = LoggerFactory.getLogger(OrchestraPreProcessor.class);

    @Autowired
    private StagedEntityTypeDAO stagedEntityTypeDAO;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Override
    public void process(Exchange exchange) throws Exception {

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        String jobId = workNote.getBatchJobId();
        exchange.getIn().setHeader("jobId", jobId);

        info("Looking up staged entities for batch job: {}", jobId);

        Set<String> stagedCollectionNames = neutralRecordMongoAccess.getRecordRepository().getStagedCollectionsForJob(
                jobId);

        // ******
        // Ensure ordering
        // EdOrgs can only be processes if SEA->LEA->School
        // rearrangeData(jobId);

        Set<IngestionStagedEntity> stagedEntities = constructStagedEntities(stagedCollectionNames);

        if (stagedEntities.size() > 0) {
            stagedEntityTypeDAO.setStagedEntitiesForJob(stagedEntities, jobId);
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
                warn("Unrecognized collection: {} dropping it on the floor", stagedCollection);
            }
        }
        info("staged entities for job: {}", stagedEntities);
        return stagedEntities;
    }

    @SuppressWarnings({ "unchecked", "unused" })
    private void rearrangeData(String jobId) {
        Iterable<NeutralRecord> edorgs = neutralRecordMongoAccess.getRecordRepository().findAll(
                EdfiEntity.EDUCATION_ORGANIZATION.getEntityName());
        List<List<NeutralRecord>> recs = new ArrayList<List<NeutralRecord>>();

        recs.add(new ArrayList<NeutralRecord>());   // SEA
        recs.add(new ArrayList<NeutralRecord>());   // LEA
        recs.add(new ArrayList<NeutralRecord>());   // School

        for (NeutralRecord nr : edorgs) {
            List<String> categories = (List<String>) nr.getAttributes().get("organizationCategories");
            if (categories != null) {
                int index = -1;
                if (categories.contains("State Education Agency")) {
                    index = 0;
                } else if (categories.contains("Local Education Agency")) {
                    index = 1;
                } else if (categories.contains("School")) {
                    index = 2;
                }

                recs.get(index).add(nr);
            }
        }

        neutralRecordMongoAccess.getRecordRepository().deleteAll(EdfiEntity.EDUCATION_ORGANIZATION.getEntityName());

        for (List<NeutralRecord> list : recs) {
            for (NeutralRecord nr : list) {
                neutralRecordMongoAccess.getRecordRepository().createForJob(nr, jobId);
            }
        }
    }

}
