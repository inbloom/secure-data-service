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

package org.slc.sli.ingestion.processors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.SLIWorkNote;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ProcessorSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * Performs purging of data in mongodb based on the tenant id.
 *
 * @author npandey
 *
 */
@Component
public class PurgeProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.PURGE_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Purges tenant's ingested data from sli database";

    private static final Logger LOGGER = LoggerFactory.getLogger(PurgeProcessor.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    private List<String> excludeCollections;

    private ReportStats reportStats = null;

    @Autowired
    @Value("${sli.sandbox.enabled}")
    private boolean sandboxEnabled;

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

        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        String batchJobId = getBatchJobId(exchange);
        if (batchJobId != null) {

            reportStats = new SimpleReportStats();

            NewBatchJob newJob = null;
            try {
                newJob = batchJobDAO.findBatchJobById(batchJobId);

                TenantContext.setTenantId(newJob.getTenantId());

                String tenantId = newJob.getTenantId();
                if (tenantId == null) {
                    handleNoTenantId(batchJobId);
                } else {
                    purgeForTenant(exchange, newJob, tenantId);
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

    private void purgeForTenant(Exchange exchange, NewBatchJob job, String tenantId) {

        Query searchTenantId = new Query();

        TenantContext.setIsSystemCall(false);
        Set<String> collectionNames = mongoTemplate.getCollectionNames();

        Iterator<String> iter = collectionNames.iterator();
        String collectionName;
        while (iter.hasNext()) {
            collectionName = iter.next();
            if (!isExcludedCollection(collectionName)) {
                // Remove edorgs and apps if in sandbox mode or purge-keep-edorgs was not specified.
                if (collectionName.equalsIgnoreCase("educationOrganization")) {
                    if (sandboxEnabled || (job.getProperty(AttributeType.PURGE_KEEP_EDORGS.getName()) == null)) {
                        cleanApplicationEdOrgs(searchTenantId);
                        removeTenantCollection(searchTenantId, collectionName);
                    }
                } else if (collectionName.equalsIgnoreCase("applicationAuthorization")) {
                    if (sandboxEnabled || (job.getProperty(AttributeType.PURGE_KEEP_EDORGS.getName()) == null)) {
                        removeTenantCollection(searchTenantId, collectionName);
                    }
                } else {
                    removeTenantCollection(searchTenantId, collectionName);
                }
            }
        }

        batchJobDAO.removeRecordHashByTenant(tenantId);
        exchange.setProperty("purge.complete", "Purge process completed successfully.");
        LOGGER.info("Purge process complete.");

    }

    @SuppressWarnings("unchecked")
    private void cleanApplicationEdOrgs(Query searchTenantId) {

        TenantContext.setIsSystemCall(false);
        List<Entity> edorgs = mongoTemplate.find(searchTenantId, Entity.class, "educationOrganization");

        TenantContext.setIsSystemCall(true);
        List<Entity> apps = mongoTemplate.findAll(Entity.class, "application");

        List<String> edorgids = new ArrayList<String>();
        for (Entity edorg : edorgs) {
            edorgids.add(edorg.getEntityId());
        }

        List<String> authedEdorgs;
        for (Entity app : apps) {
            authedEdorgs = (List<String>) app.getBody().get("authorized_ed_orgs");
            if (authedEdorgs != null) {
                for (String id : edorgids) {
                    if (authedEdorgs.contains(id)) {
                        authedEdorgs.remove(id);
                    }
                }

                app.getBody().put("authorized_ed_orgs", authedEdorgs);

                TenantContext.setIsSystemCall(true);
                mongoTemplate.save(app, "application");
            }
        }
    }

    private boolean isExcludedCollection(String collectionName) {
        for (String excludedCollectionName : excludeCollections) {
            if (collectionName.equals(excludedCollectionName)) {
                return true;
            }
        }
        return false;
    }

    private void removeTenantCollection(Query searchTenantId, String collectionName) {
        TenantContext.setIsSystemCall(false);
        mongoTemplate.remove(searchTenantId, collectionName);
    }

    private void handleNoTenantId(String batchJobId) {
        databaseMessageReport.error(reportStats, new ProcessorSource(BATCH_JOB_STAGE.getName()),
                CoreMessageCode.CORE_0035);
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        exchange.setProperty("purge.complete", "Errors encountered during purge process");

        databaseMessageReport.error(reportStats, new ProcessorSource(BATCH_JOB_STAGE.getName()),
                CoreMessageCode.CORE_0036, exception.toString());
    }

    private String getBatchJobId(Exchange exchange) {
        String batchJobId = null;

        SLIWorkNote workNote = exchange.getIn().getBody(SLIWorkNote.class);
        if (workNote != null) {
            batchJobId = workNote.getBatchJobId();
        }
        return batchJobId;
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOGGER.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }
}
