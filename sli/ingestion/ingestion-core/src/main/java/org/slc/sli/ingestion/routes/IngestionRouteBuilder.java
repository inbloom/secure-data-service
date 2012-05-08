package org.slc.sli.ingestion.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.LandingZoneManager;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.nodes.NodeInfo;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.JobReportingProcessor;
import org.slc.sli.ingestion.processors.MaestroOutboundProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.PurgeProcessor;
import org.slc.sli.ingestion.processors.TransformationProcessor;
import org.slc.sli.ingestion.processors.XmlFileProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.routes.orchestra.AggregationPostProcessor;
import org.slc.sli.ingestion.routes.orchestra.OrchestraPreProcessor;
import org.slc.sli.ingestion.routes.orchestra.WorkNoteAggregator;
import org.slc.sli.ingestion.tenant.TenantPopulator;

/**
 * Ingestion route builder.
 *
 * @author okrook
 *
 */
@Component
public class IngestionRouteBuilder extends SpringRouteBuilder {

    @Autowired
    ZipFileProcessor zipFileProcessor;

    @Autowired
    ControlFilePreProcessor controlFilePreProcessor;

    @Autowired
    ControlFileProcessor ctlFileProcessor;

    @Autowired
    EdFiProcessor edFiProcessor;

    @Autowired
    PurgeProcessor purgeProcessor;

    @Autowired(required = true)
    PersistenceProcessor persistenceProcessor;

    @Autowired
    TransformationProcessor transformationProcessor;

    @Autowired
    private XmlFileProcessor xmlFileProcessor;

    @Autowired
    private MaestroOutboundProcessor maestroOutputProcessor;

    @Autowired
    private OrchestraPreProcessor orchestraPreProcessor;

    @Autowired
    private AggregationPostProcessor aggregationPostProcessor;

    @Autowired
    JobReportingProcessor jobReportingProcessor;

    @Autowired
    LandingZoneManager landingZoneManager;

    @Autowired
    TenantPopulator tenantPopulator;

    @Value("${sli.ingestion.queue.workItem.queueURI}")
    private String workItemQueue;

    @Value("${sli.ingestion.queue.workItem.concurrentConsumers}")
    private int concurrentConsumers;

    @Value("${sli.ingestion.tenant.loadDefaultTenants}")
    private boolean loadDefaultTenants;

    @Autowired
    private NodeInfo nodeInfo;

    @Value("${sli.ingestion.queue.maestro.queueURI}")
    private String maestroQueue;


    @Value("${sli.ingestion.queue.pit.queueURI}")
    private String pitQueue;

    @Override
    public void configure() throws Exception {

        log.info( "Configuring node {} for node type {}", nodeInfo.getUUID(), nodeInfo.getNodeType() );
        String workItemQueueUri = workItemQueue + "?concurrentConsumers=" + concurrentConsumers;


        for (LocalFileSystemLandingZone lz : landingZoneManager.getLandingZones()) {
            configureLandingZonePollers(workItemQueueUri, lz);
        }

        configureCommonExtractRoute(workItemQueueUri);

        // TODO: configure based on nodetype

        String maestroQueueUri = maestroQueue + "?concurrentConsumers=" + concurrentConsumers;

        String pitNodeQueueUri = pitQueue + "?concurrentConsumers=" + concurrentConsumers;

        buildMaestroRoutes(maestroQueueUri, pitNodeQueueUri);

        configurePitNodes(pitNodeQueueUri );

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

        // routeId: postExtract
        // we enter here after EdFiProcessor. everything has been staged.
        // TODO: fix endpoint
        from("seda:postExtract")
            .routeId("postExtract")
            .process(orchestraPreProcessor)
            .choice()
                .when(header("stagedEntitiesEmpty").isEqualTo(true))
                    .to("direct:stop")
                .otherwise()
                    .to(maestroQueueUri);

        // uses custom bean to split WorkNotes and send to processors
        from(maestroQueueUri)
            .routeId("splitter")
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Maestro splitting WorkNotes.")
            .split()
                .method("WorkNoteSplitter", "split")
            .setHeader("IngestionMessageType", constant(MessageType.DATA_TRANSFORMATION.name()))
            .to(pitNodeQueueUri);

        // aggregates messages correlated by batchJobId, with completion size equal to the number of messages the splitter created (CamelSplitSize)
        // we enter here after PersistenceProcessor.
        // aggregationPostProcessor is only invoked once the completion predicate (we're using completionSize) evaluates to true.
        // we then route back to splitter or stop if finished.
        // TODO: fix endpoint
        from("seda:postPersist")
            .routeId("aggregator")
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Maestro aggregating WorkNotes.")
            .aggregate(simple("${body.getBatchJobId}"), new WorkNoteAggregator())
                .completionSize(simple("${property.CamelSplitSize}"))
            .process(aggregationPostProcessor)
            .choice()
                .when(header("processedAllStagedEntities").isEqualTo(true))
                    .to("direct:stop")
                .otherwise()
                    .to(maestroQueueUri);
    }

    /**
     * The starting points of ingestion processing, file pollers for .zip and .ctl files and routing accordingly.
     *
     * @param workItemQueueUri
     * @param lz
     */
    private void configureLandingZonePollers(String workItemQueueUri, LocalFileSystemLandingZone lz) {

        String inboundDir = lz.getDirectory().getAbsolutePath();
        log.info("Configuring route for landing zone: {} ", inboundDir);
        // routeId: ctlFilePoller
        from("file:" + inboundDir + "?include=^(.*)\\." + FileFormat.CONTROL_FILE.getExtension() + "$" + "&move="
                        + inboundDir + "/.done/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}" + "&moveFailed="
                        + inboundDir + "/.error/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}" + "&readLock=changed")
            .routeId("ctlFilePoller-" + inboundDir)
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing file.")
            .process(controlFilePreProcessor)
            .to(workItemQueueUri);

        // routeId: zipFilePoller
        from("file:" + inboundDir + "?include=^(.*)\\." + FileFormat.ZIP_FILE.getExtension() + "$&preMove="
                        + inboundDir + "/.done&moveFailed=" + inboundDir + "/.error" + "&readLock=changed")
            .routeId("zipFilePoller-" + inboundDir)
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing zip file.")
            .process(zipFileProcessor)
            .choice()
                .when(header("hasErrors").isEqualTo(true))
                    .to("direct:stop")
                .otherwise()
                    .process(controlFilePreProcessor)
                    .to(workItemQueueUri);
    }

    /**
     * The common route will get us through the extract phase. When complete, all data will be staged in
     * NeutralRecord format in mongodb.
     *
     * @param workItemQueueUri
     */
    private void configureCommonExtractRoute(String workItemQueueUri) {

        // routeId: extraction
        from(workItemQueueUri)
            .routeId("extraction")
            .choice()
                .when(header("IngestionMessageType").isEqualTo(MessageType.BATCH_REQUEST.name()))
                    .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing control file.")
                    .process(ctlFileProcessor)
                    .to("direct:assembledJobs")

                .when(header("IngestionMessageType").isEqualTo(MessageType.PURGE.name()))
                    .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Performing Purge Operation.")
                    .process(purgeProcessor)
                    .to("direct:stop")

                .when(header("IngestionMessageType").isEqualTo(MessageType.CONTROL_FILE_PROCESSED.name()))
                    .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing xml file.")
                    .process(xmlFileProcessor)
                    .to(workItemQueueUri)

                .when(header("IngestionMessageType").isEqualTo(MessageType.XML_FILE_PROCESSED.name()))
                    .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Job Pipeline for file.")
                    .process(edFiProcessor)
                    .to("seda:postExtract");

        // routeId: assembledJobs
        from("direct:assembledJobs")
            .routeId("assembledJobs")
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Dispatching jobs for file.")
            .choice()
                .when(header("hasErrors").isEqualTo(true))
                    .to("direct:stop")
                .otherwise()
                    .to(workItemQueueUri);

        // routeId: jobReporting
        from("direct:jobReporting")
            .routeId("jobReporting")
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Reporting on jobs for file.")
            .process(jobReportingProcessor);

        // end of routing
        from("direct:stop")
            .routeId("stop")
            .wireTap("direct:jobReporting")
            .log("end of job: " + header("jobId").toString())
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - File processed.")
            .stop();
    }

    /**
     * The TransformPersist route will handle transformation of staged NeutralRecords and persist
     * SLI entities to mongodb.
     *
     * @param pitNodeQueueUri
     */
    private void configurePitNodes(String pitNodeQueueUri) {

        // routeId: pitNodes
        from(pitNodeQueueUri)
            .routeId("pitNodes")
            .choice()
                .when(header("IngestionMessageType").isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                    .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Data transformation.")
                    .process(transformationProcessor)
                    .to(pitNodeQueueUri)

                .when(header("IngestionMessageType").isEqualTo(MessageType.PERSIST_REQUEST.name()))
                    .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Persisiting data for file.")
                    .log("persist: jobId: " + header("jobId").toString())
                    .choice()
                        .when(header("dry-run").isEqualTo(true))
                            .log("job has dry-run specified; data will not be published")
                            .to("direct:stop")
                        .otherwise()
                            .log("persisting data now!")
                            .process(persistenceProcessor)
                            .to("seda:postPersist")

                .when(header("IngestionMessageType").isEqualTo(MessageType.ERROR.name()))
                    .log("Error: ${header.ErrorMessage}")
                    .to("direct:stop");
    }

}
