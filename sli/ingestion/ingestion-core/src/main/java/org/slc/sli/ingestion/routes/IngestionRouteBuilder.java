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

package org.slc.sli.ingestion.routes;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.nodes.IngestionNodeType;
import org.slc.sli.ingestion.nodes.NodeInfo;
import org.slc.sli.ingestion.processors.CommandProcessor;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.JobReportingProcessor;
import org.slc.sli.ingestion.processors.LandingZoneProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.PurgeProcessor;
import org.slc.sli.ingestion.processors.TenantProcessor;
import org.slc.sli.ingestion.processors.TransformationProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.routes.orchestra.AggregationPostProcessor;
import org.slc.sli.ingestion.routes.orchestra.OrchestraPreProcessor;
import org.slc.sli.ingestion.routes.orchestra.WorkNoteLatch;
import org.slc.sli.ingestion.tenant.TenantPopulator;
import org.slc.sli.ingestion.validation.IndexValidationException;
import org.slc.sli.ingestion.validation.IndexValidatorExecutor;



/**
 * Ingestion route builder.
 *
 * @author okrook
 *
 */
@Component
public class IngestionRouteBuilder extends SpringRouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(IngestionRouteBuilder.class);

    @Autowired
    private LandingZoneProcessor landingZoneProcessor;

    @Autowired
    private ZipFileProcessor zipFileProcessor;

    @Autowired
    private ControlFilePreProcessor controlFilePreProcessor;

    @Autowired
    private ControlFileProcessor ctlFileProcessor;

    @Autowired
    private PurgeProcessor purgeProcessor;

    @Autowired(required = true)
    private PersistenceProcessor persistenceProcessor;

    @Autowired
    private TransformationProcessor transformationProcessor;

    @Autowired
    private OrchestraPreProcessor orchestraPreProcessor;

    @Autowired
    private AggregationPostProcessor aggregationPostProcessor;

    @Autowired
    private JobReportingProcessor jobReportingProcessor;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private TenantProcessor tenantProcessor;

    @Autowired
    private TenantPopulator tenantPopulator;

    @Autowired
    private BatchJobManager batchJobManager;

    @Autowired
    private NodeInfo nodeInfo;

    @Autowired
    private IndexValidatorExecutor indexValidatorExecutor;

    @Value("${sli.ingestion.queue.workItem.queueURI}")
    private String workItemQueue;

    @Value("${sli.ingestion.queue.workItem.concurrentConsumers}")
    private String workItemConsumers;

    @Value("${sli.ingestion.queue.landingZone.queueURI}")
    private String landingZoneQueue;

    @Value("${sli.ingestion.queue.landingZone.concurrentConsumers}")
    private String landingZoneConsumers;

    @Value("${sli.ingestion.topic.command}")
    private String commandTopicUri;

    @Value("${sli.ingestion.queue.parser.queueURI}")
    private String parserQueue;

    @Value("${sli.ingestion.queue.parser.concurrentConsumers}")
    private String parserConsumers;

    @Value("${sli.ingestion.queue.parser.uriOptions}")
    private String parserUriOptions;

    @Value("${sli.ingestion.tenant.tenantPollingRepeatInterval}")
    private String tenantPollingRepeatInterval;

    @Value("${sli.ingestion.tenant.loadDefaultTenants}")
    private boolean loadDefaultTenants;

    public static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

    // Spring's dependency management can confuse camel due to some circular dependencies. Removing
    // this constructor, even if it doesn't look like it will change things, may affect loading
    // order and cause ingestion to fail to start on certain JVMs
    @Autowired
    public IngestionRouteBuilder(TenantProcessor tenantProcessor) {
        super();
    }

    @Override
    public void configure() throws Exception {
        LOG.info("Configuring node {} for node type {}", nodeInfo.getUUID(), nodeInfo.getNodeType());
        LOG.info("Checking Indexes");
        ReportStats indexValidationStats = indexValidatorExecutor.checkNonTenantIndexes();
        if(indexValidationStats.hasErrors()){
            LOG.error("Required indexes are missing! Check log for details of missing indexes");
            throw new IndexValidationException("Required indexes are missing!");
        }

        String landingZoneQueueUri = landingZoneQueue + "?concurrentConsumers=" + landingZoneConsumers;
        String workItemQueueUri = workItemQueue + "?concurrentConsumers=" + workItemConsumers;
        String parserQueueUri = parserQueue + "?concurrentConsumers=" + parserConsumers + parserUriOptions;

        if (IngestionNodeType.MAESTRO.equals(nodeInfo.getNodeType())
                || IngestionNodeType.STANDALONE.equals(nodeInfo.getNodeType())) {

            LOG.info("configuring routes for maestro node");

            if (loadDefaultTenants) {
                // populate the tenant collection with a default set of tenants
                tenantPopulator.populateDefaultTenants();
            }

            buildLzDropFileRoute(landingZoneQueueUri, workItemQueueUri);

            buildExtractionRoutes(workItemQueueUri, parserQueueUri);

            configureTenantPollingTimerRoute();

            this.addRoutesToCamelContext(camelContext);
        }

        from(this.commandTopicUri)
            .bean(batchJobManager, "prepareTenantContext")
            .bean(this.lookup(CommandProcessor.class));
    }

    /**
     * The buildLzDropFileRoute route defines the path from the landingzone through control file processing
     *
     * @param landingZoneQueueUri
     * @param workItemQueueUri
     */
    private void buildLzDropFileRoute(String landingZoneQueueUri, String workItemQueueUri) {

        // routeId: lzDropFile
        from(landingZoneQueueUri).routeId("lzDropFile")
            .bean(batchJobManager, "prepareTenantContext")
            .log(LoggingLevel.INFO, "CamelRouting", "Landing Zone message detected. Routing to LandingZoneProcessor.")
            .to("direct:processLandingZone");

        // routeId: processLandingZone
        from("direct:processLandingZone").routeId("processLandingZone")
            .process(landingZoneProcessor)
            .bean(batchJobManager, "prepareTenantContext")
            .choice().when()
                .method(batchJobManager, "hasErrors")
                .log(LoggingLevel.WARN, "CamelRouting", "Invalid landing zone detected.")
                .to("direct:stop")
            .otherwise()
                .log(LoggingLevel.INFO, "CamelRouting", "Landing zone is valid. Routing to ZipFileProcessor.")
                .to("direct:processZipFile");

        // routeId: processZipFile
        from("direct:processZipFile").routeId("processZipFile")
            .bean(batchJobManager, "prepareTenantContext")
            .process(zipFileProcessor)
            .choice().when()
                .method(batchJobManager, "hasErrors")
                .log(LoggingLevel.WARN, "CamelRouting", "Invalid zip file detected.").to("direct:stop")
            .otherwise()
                .log(LoggingLevel.INFO, "CamelRouting", "No errors in zip file. Routing to ControlFilePreProcessor.")
                .to("direct:processControlFilePre");


        // routeId: processControlFilePre
        from("direct:processControlFilePre").routeId("processControlFilePre")
            .bean(batchJobManager, "prepareTenantContext")
            .process(controlFilePreProcessor)
            .choice().when()
                .method(batchJobManager, "hasErrors")
                .log(LoggingLevel.WARN, "CamelRouting", "Failed to pre-process control file.").to("direct:stop")
            .otherwise()
                .log(LoggingLevel.INFO, "CamelRouting", "Pre-processed control file.")
                .to(workItemQueueUri);

    }

    /**
     * The common route will get us through the extract and persistence phases. When complete, all data will be
     * written to the mongodb.
     *
     * @param workItemQueueUri
     * @param parserQueueUri
     */
    private void buildExtractionRoutes(String workItemQueueUri, String parserQueueUri) {

        // routeId: extraction
        from(workItemQueueUri).routeId("extraction")
                .bean(batchJobManager, "prepareTenantContext").choice()
                .when(header(INGESTION_MESSAGE_TYPE).isEqualTo(MessageType.ERROR.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Error in processing. Routing to stop.").to("direct:stop")

                .when(header(INGESTION_MESSAGE_TYPE).isEqualTo(MessageType.BATCH_REQUEST.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to ControlFileProcessor.").process(ctlFileProcessor)
                .to("direct:assembledJobs")

                .when(header(INGESTION_MESSAGE_TYPE).isEqualTo(MessageType.PURGE.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Purge command. Routing to PurgeProcessor.")
                .process(purgeProcessor).to("direct:stop")

                .when(header(INGESTION_MESSAGE_TYPE).isEqualTo(MessageType.CONTROL_FILE_PROCESSED.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to zipFileSplitter.")
                .choice()
                .when().method(batchJobManager, "isEligibleForDeltaPurge")
                .beanRef("deltaHashPurgeProcessor")
                .endChoice()
                .split().method("ZipFileSplitter", "splitZipFile")
                .log(LoggingLevel.INFO, "CamelRouting", "Zip file split").to(parserQueueUri);

        from(parserQueueUri).routeId("edFiParser")
            .bean(batchJobManager, "prepareTenantContext")
            .log(LoggingLevel.INFO, "File entry received. Processing: ${body}")
            .beanRef("edFiParserProcessor")
            .to("direct:fileEntryLatch");

        from("direct:deltaFilter").routeId("deltaFilter")
            .bean(batchJobManager, "prepareTenantContext")
            .log(LoggingLevel.INFO, "CamelRouting", "Batch of ${body.neutralRecords.size} is recieved for delta processing")
            .beanRef("deltaFilterProcessor")
            .choice()
                .when(header(INGESTION_MESSAGE_TYPE).isEqualTo(MessageType.PERSIST_REQUEST.name()))
                .choice()
                    .when()
                    .method(batchJobManager, "isDryRun")
                        .log(LoggingLevel.INFO, "CamelRouting", "Dry-run specified. Bypassing persistence.")
                    .otherwise()
                        .log(LoggingLevel.INFO, "CamelRouting", "Routing to PersistenceProcessor.")
                        .to("direct:persister");

        from("direct:persister").routeId("persister")
                .bean(batchJobManager, "prepareTenantContext")
                .log(LoggingLevel.INFO, "CamelRouting", "Batch of ${body.neutralRecords.size} is recieved to persist")
                .beanRef("persistenceProcessor");

        from("direct:staging").routeId("staging")
            .bean(batchJobManager, "prepareTenantContext")
            .log(LoggingLevel.INFO, "CamelRouting", "Batch of ${body.neutralRecords.size} is recieved to stage")
            .beanRef("stagingProcessor");

        // file entry Latch
        from("direct:fileEntryLatch").routeId("fileEntryLatch")
            .bean(batchJobManager, "prepareTenantContext")
            .log(LoggingLevel.INFO, "Removing file entry from latch. Processing: ${body}")
            .choice()
                .when().method("fileEntryLatch", "lastFileProcessed")
                    .log(LoggingLevel.INFO, "CamelRouting", "FileEntryWorkNote latch opened.")
                    .to("direct:stop");

        // routeId: assembledJobs
        from("direct:assembledJobs").routeId("assembledJobs")
            .bean(batchJobManager, "prepareTenantContext").choice().when()
                .method(batchJobManager, "hasErrors")
                .log(LoggingLevel.INFO, "CamelRouting", "Error in processing. Routing to stop.").to("direct:stop")
                .otherwise().to(workItemQueueUri);

        // end of routing
        from("direct:stop").routeId("stop")
                .bean(batchJobManager, "prepareTenantContext")
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to JobReportingProcessor.")
                .process(jobReportingProcessor).log(LoggingLevel.INFO, "CamelRouting", "Stop. Job routing complete.")
                .stop();
    }

    public void configureTenantPollingTimerRoute() {
        from(
                "quartz://tenantPollingTimer?trigger.fireNow=true&trigger.repeatCount=-1&trigger.repeatInterval="
                        + tenantPollingRepeatInterval).setBody()
                .simple("TenantPollingTimer fired: ${header.firedTime}").process(tenantProcessor);
    }

    @PostConstruct
    public void post() throws Exception {
        this.addRoutesToCamelContext(camelContext);
    }
}
