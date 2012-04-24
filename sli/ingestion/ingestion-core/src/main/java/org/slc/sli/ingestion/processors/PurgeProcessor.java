package org.slc.sli.ingestion.processors;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * Performs purging of data in mongodb based on the tenant id.
 *
 * @author npandey
 *
 */
@Component
public class PurgeProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(PurgeProcessor.class);

    private static final String METADATA_BLOCK = "metaData";

    private static final String TENANT_ID = "tenantId";

    private BatchJobDAO batchJobDAO;

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<String> excludeCollections;

    public PurgeProcessor() {
        this.batchJobDAO = new BatchJobMongoDA();
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<String> getExcludeCollections() {
        return excludeCollections;
    }

    public void setExcludeCollections(List<String> excludeCollections) {
        this.excludeCollections = excludeCollections;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        String batchJobId = getBatchJobId(exchange);
        if (batchJobId != null) {

            NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);

            Stage stage = startAndGetStage(newJob);

            batchJobDAO.saveBatchJob(newJob);

            String tenantId = newJob.getProperty(TENANT_ID);
            if (tenantId == null) {
                LOG.info("TenantId missing. No purge operation performed.");
                BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.PURGE_PROCESSOR,
                        FaultType.TYPE_WARNING.getName(), null, "No tenant specified. No purge will be done.");
            } else {
                purgeForTenant(exchange, tenantId);
            }

            stage.stopStage();
            batchJobDAO.saveBatchJob(newJob);

        } else {
            missingBatchJobIdError(exchange);
        }
    }

    private void purgeForTenant(Exchange exchange, String tenantId) {

        Query searchTenantId = new Query();
        searchTenantId.addCriteria(Criteria.where(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey()).is(
                tenantId));
        try {
            Set<String> collectionNames = mongoTemplate.getCollectionNames();
            Iterator<String> iter = collectionNames.iterator();
            String collectionName;
            while (iter.hasNext()) {
                collectionName = iter.next();
                if (isSystemCollection(collectionName)) {
                    continue;
                }
                mongoTemplate.remove(searchTenantId, collectionName);
            }
            exchange.setProperty("purge.complete", "Purge process completed successfully.");
            LOG.info("Purge process complete.");

        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            exchange.setProperty("purge.complete", "Purge process complete.");
            LOG.error("Exception:", exception);

            String batchJobId = getBatchJobId(exchange);
            if (batchJobId != null) {
                BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.PURGE_PROCESSOR,
                        FaultType.TYPE_ERROR.getName(), null, exception.toString());
            }
        }
    }

    private boolean isSystemCollection(String collectionName) {
        for (String excludedCollectionName : excludeCollections) {
            if (collectionName.equals(excludedCollectionName)) {
                return true;
            }
        }
        return false;
    }

    private Stage startAndGetStage(NewBatchJob newJob) {
        Stage stage = new Stage();
        newJob.getStages().add(stage);
        stage.setStageName(BatchJobStageType.PURGE_PROCESSOR.getName());
        stage.startStage();
        return stage;
    }

    private String getBatchJobId(Exchange exchange) {
        return exchange.getIn().getHeader("BatchJobId", String.class);
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    public BatchJobDAO getBatchJobDAO() {
        return batchJobDAO;
    }

    public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
        this.batchJobDAO = batchJobDAO;
    }

}
