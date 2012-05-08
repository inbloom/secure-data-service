package org.slc.sli.ingestion.routes.orchestra;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.ingestion.EdfiEntity;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author dduran
 *
 */
@Component
public class OrchestraPreProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(OrchestraPreProcessor.class);

    @Autowired
    private StagedEntityTypeDAO stagedEntityTypeDAO;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Override
    public void process(Exchange exchange) throws Exception {

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        String jobId = workNote.getBatchJobId();
        exchange.getIn().setHeader("jobId", jobId);

        Set<String> stagedCollectionNames = neutralRecordMongoAccess.getRecordRepository().getCollectionNamesForJob(
                jobId);

        Set<EdfiEntity> stagedEntities = constructStagedEntities(stagedCollectionNames);

        if (stagedEntities.size() > 0) {
            stagedEntityTypeDAO.setStagedEntitiesForJob(stagedEntities, jobId);
        } else {
            exchange.getIn().setHeader("stagedEntitiesEmpty", true);
        }
    }

    private Set<EdfiEntity> constructStagedEntities(Set<String> stagedCollectionNames) {

        Set<EdfiEntity> stagedEntities = new HashSet<EdfiEntity>();
        for (String stagedCollection : stagedCollectionNames) {

            EdfiEntity stagedEdfiEntity = EdfiEntity.fromEntityName(stagedCollection);
            if (stagedEdfiEntity != null) {
                stagedEntities.add(stagedEdfiEntity);
            } else {
                LOG.warn("Uncrecognized entity: {} dropping it on the floor", stagedCollection);
            }
        }
        LOG.info("staged entities for job: {}", stagedEntities);
        return stagedEntities;
    }

}
