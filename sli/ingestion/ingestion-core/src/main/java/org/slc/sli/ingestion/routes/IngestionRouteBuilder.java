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
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.JobReportingProcessor;
import org.slc.sli.ingestion.processors.LandingZoneProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.PurgeProcessor;
import org.slc.sli.ingestion.processors.TenantProcessor;
import org.slc.sli.ingestion.processors.TransformationProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.impl.LoggingMessageReport;
import org.slc.sli.ingestion.routes.orchestra.AggregationPostProcessor;
import org.slc.sli.ingestion.routes.orchestra.OrchestraPreProcessor;
import org.slc.sli.ingestion.routes.orchestra.WorkNoteLatch;
import org.slc.sli.ingestion.routes.orchestra.parsing.FileEntryLatch;
import org.slc.sli.ingestion.tenant.TenantPopulator;
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
    private EdFiProcessor edFiProcessor;

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
    private IndexValidatorExecutor indexValidatorExecutor;
    
    @Autowired
    private NodeInfo nodeInfo;

    @Value("${sli.ingestion.queue.workItem.queueURI}")
    private String workItemQueue;

    @Value("${sli.ingestion.queue.workItem.concurrentConsumers}")
    private String workItemConsumers;

    @Value("${sli.ingestion.queue.landingZone.queueURI}")
    private String landingZoneQueue;

    @Value("${sli.ingestion.queue.landingZone.concurrentConsumers}")
    private String landingZoneConsumers;

    @Value("${sli.ingestion.queue.maestro.queueURI}")
    private String maestroQueue;

    @Value("${sli.ingestion.queue.maestro.consumerQueueURI}")
    private String maestroConsumerQueue;

    @Value("${sli.ingestion.queue.maestro.concurrentConsumers}")
    private String maestroConsumers;

    @Value("${sli.ingestion.queue.maestro.uriOptions}")
    private String maestroUriOptions;

    @Value("${sli.ingestion.queue.pit.queueURI}")
    private String pitQueue;

    @Value("${sli.ingestion.queue.pit.consumerQueueURI}")
    private String pitConsumerQueue;

    @Value("${sli.ingestion.queue.pit.concurrentConsumers}")
    private String pitConsumers;

    @Value("${sli.ingestion.queue.pit.uriOptions}")
    private String pitUriOptions;

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

    private static final String HAS_ERRORS = "hasErrors";

    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

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

        String landingZoneQueueUri = landingZoneQueue + "?concurrentConsumers=" + landingZoneConsumers;
        String workItemQueueUri = workItemQueue + "?concurrentConsumers=" + workItemConsumers;
        String maestroQueueUri = maestroQueue + "?concurrentConsumers=" + maestroConsumers + maestroUriOptions;
        String maestroConsumerQueueUri = maestroConsumerQueue + "?concurrentConsumers=" + maestroConsumers + maestroUriOptions;
        String pitNodeQueueUri = pitQueue + "?concurrentConsumers=" + pitConsumers + pitUriOptions;
        String pitConsumerNodeQueueUri = pitConsumerQueue + "?concurrentConsumers=" + pitConsumers + pitUriOptions;
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

            buildMaestroRoutes(maestroConsumerQueueUri, pitNodeQueueUri);

            configureTenantPollingTimerRoute();

            this.addRoutesToCamelContext(camelContext);
        }

        if (IngestionNodeType.PIT.equals(nodeInfo.getNodeType())
                || IngestionNodeType.STANDALONE.equals(nodeInfo.getNodeType())) {

            LOG.info("configuring routes for pit node");

            buildPitRoutes(pitConsumerNodeQueueUri, maestroQueueUri);
        }

        from(this.commandTopicUri).bean(this.lookup(CommandProcessor.class));
    }

    /**
     * The TransformPersist route will handle transformation of staged NeutralRecords and persist
     * SLI entities to mongodb.
     *
     * @param pitNodeQueueUri
     */
    private void buildLzDropFileRoute(String landingZoneQueueUri, String workItemQueueUri) {

        // routeId: lzDropFile
        from(landingZoneQueueUri).routeId("lzDropFile")
            .log(LoggingLevel.INFO, "CamelRouting", "Landing Zone message detected. Routing to LandingZoneProcessor.")
            .to("direct:processLandingZone");

        // routeId: processLandingZone
        from("direct:processLandingZone").routeId("processLandingZone")
            .process(landingZoneProcessor)
            .choice().when()
                .method(batchJobManager, "hasErrors")
                .log(LoggingLevel.WARN, "CamelRouting", "Invalid landing zone detected.").to("direct:stop")
            .otherwise()
                .log(LoggingLevel.INFO, "CamelRouting", "Landing zone is valid. Routing to ZipFileProcessor.")
                .to("direct:processZipFile");

        // routeId: processZipFile
        from("direct:processZipFile").routeId("processZipFile")
            .process(zipFileProcessor)
            .choice().when()
                .method(batchJobManager, "hasErrors")
                .log(LoggingLevel.WARN, "CamelRouting", "Invalid zip file detected.").to("direct:stop")
            .otherwise()
                .log(LoggingLevel.INFO, "CamelRouting", "No errors in zip file. Routing to ControlFilePreProcessor.")
                .to("direct:processControlFilePre");


        // routeId: processControlFilePre
        from("direct:processControlFilePre").routeId("processControlFilePre")
            .process(controlFilePreProcessor)
            .choice().when()
                .method(batchJobManager, "hasErrors")
                .log(LoggingLevel.WARN, "CamelRouting", "Failed to pre-process control file.").to("direct:stop")
            .otherwise()
                .log(LoggingLevel.INFO, "CamelRouting", "Pre-processed control file.")
                .to(workItemQueueUri);

    }

    /**
     * The maestro routes should:
     * 1. Process the inbound file until persisting to the staging DB
     * 2. Create notes that can be posted to the symphony queue
     * 3. Wait for pit nodes to be done
     * 4. Aggregate pit node job status into final status
     *
     * @param maestroQueueUri
     * @param pitNodeQueueUri
     */
    private void buildMaestroRoutes(String maestroQueueUri, String pitNodeQueueUri) {

        // postExtract
        // we enter here after EdFiProcessor. everything has been staged.
        from("direct:postExtract").routeId("postExtract")
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to Maestro orchestration.")
                .process(orchestraPreProcessor).choice().when(header("stagedEntitiesEmpty").isEqualTo(true))
                .to("direct:stop").otherwise().to("direct:transformationSplitter");

        // transformationSplitter
        // split WorkNotes into separate Exchanges and drop into the pit node queue.
        from("direct:transformationSplitter").routeId("transformationSplitter")
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to WorkNoteSplitter for transformation splitting.")
                .split().method("WorkNoteSplitter", "splitTransformationWorkNotes")
                .setHeader(INGESTION_MESSAGE_TYPE, constant(MessageType.DATA_TRANSFORMATION.name()))
                .to(pitNodeQueueUri);

        // persistenceSplitter
        // act as a pass-through, create separate Exchanges for the list of WorkNotes in the
        // incoming exchange
        // and drop into the pit node queue.
        from("direct:persistenceSplitter").routeId("persistenceSplitter")
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to WorkNoteSplitter for persistence splitting.")
                .split().method("WorkNoteSplitter", "splitPersistanceWorkNotes")
                .setHeader(INGESTION_MESSAGE_TYPE, constant(MessageType.PERSIST_REQUEST.name())).to(pitNodeQueueUri);

        // workNoteLatch
        from(maestroQueueUri).routeId("workNoteLatch")
                .log(LoggingLevel.INFO, "CamelRouting", "Maestro message received. Processing: ${body}")
                .bean(this.lookup(WorkNoteLatch.class))
                .choice().when(header("latchOpened").isEqualTo(true))
                    .log(LoggingLevel.INFO, "CamelRouting", "WorkNote latch opened.")
                    .choice().when(header(INGESTION_MESSAGE_TYPE).isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                        .to("direct:persistenceSplitter")
                    .when(header(INGESTION_MESSAGE_TYPE).isEqualTo(MessageType.PERSIST_REQUEST.name()))
                        .process(aggregationPostProcessor)
                        .choice().when(header("processedAllStagedEntities").isEqualTo(true))
                            .to("direct:stop")
                        .otherwise()
                            .to("direct:transformationSplitter");

    }

    /**
     * The common route will get us through the extract phase. When complete, all data will be
     * staged in
     * NeutralRecord format in mongodb.
     *
     * @param workItemQueueUri
     */
    private void buildExtractionRoutes(String workItemQueueUri, String parserQueueUri) {

        // routeId: extraction
        from(workItemQueueUri).routeId("extraction").choice()
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
                .split().method("ZipFileSplitter", "splitZipFile")
                .log(LoggingLevel.INFO, "CamemRoutring", "Zip file split").to(parserQueueUri);

        from(parserQueueUri).routeId("edFiProcessor")
            .log(LoggingLevel.INFO, "CamelRouting", "File entry received. Processing: ${body}")
            .process(edFiProcessor).to("direct:fileEntryLatch");

        // file entry Latch
        from("direct:fileEntryLatch").routeId("fileEntryLatch")
                .bean(this.lookup(FileEntryLatch.class))
                .choice().when(header("fileENtryLatchOpened").isEqualTo(true))
                    .log(LoggingLevel.INFO, "CamelRouting", "FileEntryWorkNote latch opened.")
                    .to("direct:postExtract");

        // routeId: assembledJobs
        from("direct:assembledJobs").routeId("assembledJobs").choice().when()
                .method(batchJobManager, "hasErrors")
                .log(LoggingLevel.INFO, "CamelRouting", "Error in processing. Routing to stop.").to("direct:stop")
                .otherwise().to(workItemQueueUri);

        // end of routing
        from("direct:stop").routeId("stop").log(LoggingLevel.INFO, "CamelRouting", "Routing to JobReportingProcessor.")
                .process(jobReportingProcessor).log(LoggingLevel.INFO, "CamelRouting", "Stop. Job routing complete.")
                .stop();
    }

    /**
     * The TransformPersist route will handle transformation of staged NeutralRecords and persist
     * SLI entities to mongodb.
     *
     * @param pitNodeQueueUri
     */
    private void buildPitRoutes(String pitNodeQueueUri, String maestroQueueUri) {

        // routeId: pitNodes
        from(pitNodeQueueUri).routeId("pitNodes")
            .log(LoggingLevel.INFO, "CamelRouting", "Pit message received: ${body}")
            .choice()
            .when(header(INGESTION_MESSAGE_TYPE).isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to TransformationProcessor.")
                .process(transformationProcessor)
                .log(LoggingLevel.INFO, "CamelRouting", "TransformationProcessor complete. Routing back to Maestro.")
                .to(maestroQueueUri)

                .when(header(INGESTION_MESSAGE_TYPE).isEqualTo(MessageType.PERSIST_REQUEST.name()))
                .choice()
                .when()
                    .method(batchJobManager, "isDryRun")
                    .log(LoggingLevel.INFO, "CamelRouting", "Dry-run specified. Routing back to Maestro.")
                    .to(maestroQueueUri)
                .otherwise()
                    .log(LoggingLevel.INFO, "CamelRouting", "Routing to PersistenceProcessor.")
                    .process(persistenceProcessor)
                    .log(LoggingLevel.INFO, "CamelRouting", "PersistenceProcessor complete. Routing back to Maestro.")
                    .to(maestroQueueUri);
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
