package org.slc.sli.ingestion.processors;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;

/**
 * Performs purging of data in mongodb based on the tenant id.
 *
 * @author npandey
 *
 */
@Component
public class PurgeProcessor implements Processor, MessageSourceAware {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.PURGE_PROCESSOR;
    private static Logger logger = LoggerFactory.getLogger(PurgeProcessor.class);

    private static final String METADATA_BLOCK = "metaData";

    private static final String TENANT_ID = "tenantId";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BatchJobDAO batchJobDAO;

    private List<String> excludeCollections;

    private MessageSource messageSource;

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
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        String batchJobId = getBatchJobId(exchange);
        if (batchJobId != null) {

            NewBatchJob newJob = null;
            try {
                newJob = batchJobDAO.findBatchJobById(batchJobId);

                String tenantId = newJob.getProperty(TENANT_ID);
                if (tenantId == null) {

                    handleNoTenantId(batchJobId);
                } else {

                    purgeForTenant(exchange, tenantId);
                }

            } catch (Exception exception) {
                handleProcessingExceptions(exchange, batchJobId, exception);
            } finally {
                if (newJob != null) {
                    BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                    batchJobDAO.saveBatchJob(newJob);
                }
            }

        } else {
            missingBatchJobIdError(exchange);
        }
    }

    private void purgeForTenant(Exchange exchange, String tenantId) {

        Query searchTenantId = new Query();
        searchTenantId.addCriteria(Criteria.where(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey()).is(
                tenantId));

        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        Iterator<String> iter = collectionNames.iterator();
        String collectionName;
        while (iter.hasNext()) {
            collectionName = iter.next();
            if (isExcludedCollection(collectionName)) {
                continue;
            }
            mongoTemplate.remove(searchTenantId, collectionName);
        }
        exchange.setProperty("purge.complete", "Purge process completed successfully.");
        logger.info("Purge process complete.");

    }

    private boolean isExcludedCollection(String collectionName) {
        for (String excludedCollectionName : excludeCollections) {
            if (collectionName.equals(excludedCollectionName)) {
                return true;
            }
        }
        return false;
    }

    private void handleNoTenantId(String batchJobId) {
        String noTenantMessage = MessageSourceHelper.getMessage(messageSource, "PURGEPROC_ERR_MSG1");
        logger.info(noTenantMessage);

        Error error = Error.createIngestionError(batchJobId, null, BatchJobStageType.PURGE_PROCESSOR.getName(), null,
                null, null, FaultType.TYPE_WARNING.getName(), null, noTenantMessage);
        batchJobDAO.saveError(error);
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        exchange.setProperty("purge.complete", "Errors encountered during purge process");
        LogUtil.error(logger, "Error processing batch job " + batchJobId, exception);

        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, null, BatchJobStageType.PURGE_PROCESSOR.getName(),
                    null, null, null, FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);
        }
    }

    private String getBatchJobId(Exchange exchange) {
        String batchJobId = null;

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        if (workNote != null) {
            batchJobId = workNote.getBatchJobId();
        }
        return batchJobId;
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        logger.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }
}
