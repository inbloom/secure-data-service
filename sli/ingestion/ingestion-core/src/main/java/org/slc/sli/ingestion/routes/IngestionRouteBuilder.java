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

package org.slc.sli.ingestion.routes;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.nodes.IngestionNodeType;
import org.slc.sli.ingestion.nodes.NodeInfo;
import org.slc.sli.ingestion.processors.CommandProcessor;
import org.slc.sli.ingestion.processors.ConcurrentEdFiProcessor;
import org.slc.sli.ingestion.processors.ConcurrentXmlFileProcessor;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.JobReportingProcessor;
import org.slc.sli.ingestion.processors.LandingZoneProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.PurgeProcessor;
import org.slc.sli.ingestion.processors.TenantProcessor;
import org.slc.sli.ingestion.processors.TransformationProcessor;
import org.slc.sli.ingestion.processors.XmlFileProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.routes.orchestra.AggregationPostProcessor;
import org.slc.sli.ingestion.routes.orchestra.OrchestraPreProcessor;
import org.slc.sli.ingestion.routes.orchestra.WorkNoteLatch;
import org.slc.sli.ingestion.tenant.TenantPopulator;
import org.slc.sli.ingestion.validation.IndexValidator;

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
    private ConcurrentEdFiProcessor concurrentEdFiProcessor;

    @Autowired
    private PurgeProcessor purgeProcessor;

    @Autowired(required = true)
    private PersistenceProcessor persistenceProcessor;

    @Autowired
    private TransformationProcessor transformationProcessor;

    @Autowired
    private XmlFileProcessor xmlFileProcessor;

    @Autowired
    private ConcurrentXmlFileProcessor concurrentXmlFileProcessor;

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
    private NodeInfo nodeInfo;

    @Autowired
    private IndexValidator indexValidator;

    @Value("${sli.ingestion.processor.edfi}")
    private String edfiProcessorMode;

    @Value("${sli.ingestion.processor.xml}")
    private String xmlProcessorMode;

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

    @Value("${sli.ingestion.tenant.tenantPollingRepeatInterval}")
    private String tenantPollingRepeatInterval;

    @Value("${sli.ingestion.tenant.loadDefaultTenants}")
    private boolean loadDefaultTenants;

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

        boolean indexValidated = indexValidator.isValid(null, null);
        if (!indexValidated) {
            LOG.error("Indexes could not be verified, check the index file configurations are set");
        }

        String landingZoneQueueUri = landingZoneQueue + "?concurrentConsumers=" + landingZoneConsumers;
        String workItemQueueUri = workItemQueue + "?concurrentConsumers=" + workItemConsumers;
        String maestroQueueUri = maestroQueue + "?concurrentConsumers=" + maestroConsumers + maestroUriOptions;
        String maestroConsumerQueueUri = maestroConsumerQueue + "?concurrentConsumers=" + maestroConsumers + maestroUriOptions;
        String pitNodeQueueUri = pitQueue + "?concurrentConsumers=" + pitConsumers + pitUriOptions;
        String pitConsumerNodeQueueUri = pitConsumerQueue + "?concurrentConsumers=" + pitConsumers + pitUriOptions;

        if (IngestionNodeType.MAESTRO.equals(nodeInfo.getNodeType())
                || IngestionNodeType.STANDALONE.equals(nodeInfo.getNodeType())) {

            LOG.info("configuring routes for maestro node");

            if (loadDefaultTenants) {
                // populate the tenant collection with a default set of tenants
                tenantPopulator.populateDefaultTenants();
            }

            buildLzDropFileRoute(landingZoneQueueUri, workItemQueueUri);

            buildExtractionRoutes(workItemQueueUri);

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
            .log(LoggingLevel.INFO, "CamelRouting", "Landing Zone message detected.").process(landingZoneProcessor)
            .choice().when(header("hasErrors").isEqualTo(true))
                .to("direct:stop")
            .otherwise()
                .to("direct:processLzFile");

        // routeId: processLzFile
        from("direct:processLzFile").routeId("processLzFile").log(LoggingLevel.INFO, "CamelRouting", "Landing Zone is valid.")
            .choice().when(header("filePath").endsWith(".ctl"))
                .log(LoggingLevel.INFO, "CamelRouting", "Control file detected. Routing to ControlFilePreProcessor.")
                .process(controlFilePreProcessor)
                .choice().when(header("hasErrors").isEqualTo(true))
                    .to("direct:stop")
                .otherwise()
                    .to(workItemQueueUri).endChoice()
            .when(header("filePath").endsWith(".zip"))
                .log(LoggingLevel.INFO, "CamelRouting", "Zip file detected. Routing to ZipFileProcessor.")
                .process(zipFileProcessor)
                .choice().when(header("hasErrors").isEqualTo(true))
                    .to("direct:stop")
                .otherwise()
                    .log(LoggingLevel.INFO, "CamelRouting", "No errors in zip file. Routing to ControlFilePreProcessor.")
                    .process(controlFilePreProcessor)
                        .choice().when(header("hasErrors").isEqualTo(true))
                            .to("direct:stop")
                        .otherwise()
                            .to(workItemQueueUri).endChoice().endChoice()
            .otherwise()
                .log(LoggingLevel.WARN, "CamelRouting", "Unknown file type detected.").to("direct:stop");
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
                .setHeader("IngestionMessageType", constant(MessageType.DATA_TRANSFORMATION.name()))
                .to(pitNodeQueueUri);

        // persistenceSplitter
        // act as a pass-through, create separate Exchanges for the list of WorkNotes in the
        // incoming exchange
        // and drop into the pit node queue.
        from("direct:persistenceSplitter").routeId("persistenceSplitter")
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to WorkNoteSplitter for persistence splitting.")
                .split().method("WorkNoteSplitter", "splitPersistanceWorkNotes")
                .setHeader("IngestionMessageType", constant(MessageType.PERSIST_REQUEST.name())).to(pitNodeQueueUri);

        // workNoteLatch
        from(maestroQueueUri).routeId("workNoteLatch")
                .log(LoggingLevel.INFO, "CamelRouting", "Maestro message received. Processing: ${body}")
                .bean(this.lookup(WorkNoteLatch.class))
                .choice().when(header("latchOpened").isEqualTo(true))
                    .log(LoggingLevel.INFO, "CamelRouting", "WorkNote latch opened.")
                    .choice().when(header("IngestionMessageType").isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                        .to("direct:persistenceSplitter")
                    .when(header("IngestionMessageType").isEqualTo(MessageType.PERSIST_REQUEST.name()))
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
    private void buildExtractionRoutes(String workItemQueueUri) {

        Processor edfiProcessorToUse = edFiProcessor;
        if ("concurrent".equals(edfiProcessorMode)) {
            edfiProcessorToUse = concurrentEdFiProcessor;
        }

        Processor xmlFileProcessorToUse = xmlFileProcessor;
        if ("concurrent".equals(xmlProcessorMode)) {
            xmlFileProcessorToUse = concurrentXmlFileProcessor;
        }

        // routeId: extraction
        from(workItemQueueUri).routeId("extraction").choice()
                .when(header("IngestionMessageType").isEqualTo(MessageType.ERROR.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Error in processing. Routing to stop.").to("direct:stop")

                .when(header("IngestionMessageType").isEqualTo(MessageType.BATCH_REQUEST.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to ControlFileProcessor.").process(ctlFileProcessor)
                .to("direct:assembledJobs")

                .when(header("IngestionMessageType").isEqualTo(MessageType.PURGE.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Purge command. Routing to PurgeProcessor.")
                .process(purgeProcessor).to("direct:stop")

                .when(header("IngestionMessageType").isEqualTo(MessageType.CONTROL_FILE_PROCESSED.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to " + xmlProcessorMode + "XmlFileProcessor.")
                .process(xmlFileProcessorToUse).to(workItemQueueUri)

                .when(header("IngestionMessageType").isEqualTo(MessageType.XML_FILE_PROCESSED.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to " + edfiProcessorMode + "EdfiProcessor.")
                .process(edfiProcessorToUse).to("direct:postExtract");

        // routeId: assembledJobs
        from("direct:assembledJobs").routeId("assembledJobs").choice().when(header("hasErrors").isEqualTo(true))
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
                .log(LoggingLevel.DEBUG, "CamelRouting", "Pit message received: ${body}").choice()
                .when(header("IngestionMessageType").isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to TransformationProcessor.")
                .process(transformationProcessor)
                .log(LoggingLevel.INFO, "CamelRouting", "TransformationProcessor complete. Routing back to Maestro.")
                .to(maestroQueueUri)

                .when(header("IngestionMessageType").isEqualTo(MessageType.PERSIST_REQUEST.name()))
                .log(LoggingLevel.INFO, "CamelRouting", "Routing to PersistenceProcessor.")
                .log("persist: jobId: " + header("jobId").toString()).choice()
                .when(header(AttributeType.DRYRUN.getName()).isEqualTo(true))
                .log(LoggingLevel.INFO, "CamelRouting", "Dry-run specified. Routing back to Maestro.")
                .to(maestroQueueUri).otherwise().process(persistenceProcessor)
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
