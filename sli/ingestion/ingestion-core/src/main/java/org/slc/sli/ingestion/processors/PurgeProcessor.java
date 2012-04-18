package org.slc.sli.ingestion.processors;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 *Performs purging of data in mongodb based on the tenant id.
 *
 * @author npandey
 *
 */
@Component
public class PurgeProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(PurgeProcessor.class);

    private static final String METADATA_BLOCK = "metaData";

    private static final String TENANT_ID = "tenantId";

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<String> excludeCollections;

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

        Job job = BatchJobUtils.getBatchJobUsingStateManager(exchange);

        String tenantId = job.getProperty(TENANT_ID);

        if (tenantId == null) {
            job.getFaultsReport().error("TenantId is missing. No purge operation performed.", PurgeProcessor.class);
            exchange.setProperty("purge.complete", "Purge process complete.");
            logger.error("TenantId is missing. No purge operation performed.");
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
                    if(isSystemCollection(collectionName)) {
                        continue;
                    }
                    mongoTemplate.remove(searchTenantId, collectionName);
                }
                exchange.setProperty("purge.complete", "Purge process completed successfully.");
                logger.info("Purge process completed successfully.");

            } catch (Exception exception) {
                job.getFaultsReport().error(exception.toString(), PurgeProcessor.class);
                exchange.setProperty("purge.complete", "Purge process complete.");
                logger.error("Exception: {}", exception);
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

}
