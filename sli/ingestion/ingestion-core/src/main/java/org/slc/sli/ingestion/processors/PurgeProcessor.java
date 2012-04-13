package org.slc.sli.ingestion.processors;

import java.util.Iterator;
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
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 *Performs purging of data in mongodb based on the tenant id.
 *
 * @author npandey
 *
 */
@Component
public class PurgeProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(PurgeProcessor.class);

    private static final String METADATA_BLOCK = "metaData";

    private static final String TENANT_ID = "tenantId";

    @Autowired
    private MongoTemplate mongoTemplate;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        BatchJob job = BatchJobUtils.getBatchJobUsingStateManager(exchange);

        String tenantId = job.getProperty(TENANT_ID);

        if (tenantId == null) {

            LOG.info("TenantId missing. No purge operation performed.");

        } else {
            Query searchTenantId = new Query();
            searchTenantId.addCriteria(Criteria.where(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey()).is(
                    tenantId));
            try {
                Set<String> collectionNames = mongoTemplate.getCollectionNames();
                Iterator<String> iter = collectionNames.iterator();
                String collectionName;
                while (iter.hasNext()) {
                    collectionName = iter.next();
                    if (collectionName.equals("system.indexes") || collectionName.equals("system.js")) {
                        continue;
                    }
                    mongoTemplate.remove(searchTenantId, collectionName);
                }
                LOG.info("Purge process complete.");

            } catch (Exception exception) {
                exchange.getIn().setHeader("ErrorMessage", exception.toString());
                exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
                LOG.error("Exception:", exception);
            }
        }
    }

}
