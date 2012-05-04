package org.slc.sli.ingestion.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.LandingZoneManager;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.nodes.IngestionNodeType;
import org.slc.sli.ingestion.nodes.NodeInfo;
import org.slc.sli.ingestion.processor.MaestroOutboundProcessor;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.JobReportingProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.PitInboundProcessor;
import org.slc.sli.ingestion.processors.PurgeProcessor;
import org.slc.sli.ingestion.processors.TransformationProcessor;
import org.slc.sli.ingestion.processors.XmlFileProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.tenant.TenantPopulator;

/**
 * Ingestion route builder.
 *
 * @author okrook
 *
 */
@Component
public class IngestionRouteBuilder extends SpringRouteBuilder {

    private static final String WORK_ITEM_ROUTE = "workItemRoute";

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
    private PitInboundProcessor pitInboundProcessor;

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

    @Value("${sli.ingestion.queue.maestroPit.queueURI")
    private String symphonyQueue;

    private String symphonyQueueUri;

    @Override
    public void configure() throws Exception {

        String workItemQueueUri = workItemQueue + "?concurrentConsumers=" + concurrentConsumers;

        symphonyQueueUri = symphonyQueue + "?concurrentConsumers=" + concurrentConsumers;

        if (IngestionNodeType.MAESTRO.equals(nodeInfo.getNodeType())) {
            buildMaestroRoutes(workItemQueueUri);
        } else if (IngestionNodeType.PIT.equals(nodeInfo.getNodeType())) {
            buildPitRoutes(workItemQueueUri);
            configureCommonRoute(workItemQueueUri);
        }

        else {

            if (loadDefaultTenants) {
                // populate the tenant collection with a default set of tenants
                tenantPopulator.populateDefaultTenants();
            }

            configureCommonRoute(workItemQueueUri);
            configureSingleNodeRoute(workItemQueueUri);

            // configure ctlFilePoller and zipFilePoller per landing zone
            for (LocalFileSystemLandingZone lz : landingZoneManager.getLandingZones()) {
                configureRoutePerLandingZone(workItemQueueUri, lz);
            }
        }
    }

    /**
     * Pit routes should:
     * 1. Take an external work item that the maestro posted from a JMS endpoint
     * 2. Transform and persist the items in the sheet music
     * 3. Posts a message back to the queue saying "I'm done, here's what I did"
     *
     * Don't delete items from staging! Maestro will do this!
     *
     * @param workItemQueueUri
     */
    private void buildPitRoutes(String workItemQueueUri) {

        from(symphonyQueueUri).routeId(WORK_ITEM_ROUTE)
        .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Inbound request for pit to play.")
        .process(pitInboundProcessor).to(workItemQueueUri);
    }

    /**
     * The maestro routes should:
     * 1. Process the inbound file until persisting to the staging DB
     * 2. Create notes that can be posted to the symphony queue
     * 3. Wait for pit nodes to be done
     * 4. Aggregate pit node job status into final status
     *
     * @param workItemQueueUri
     */
    private void buildMaestroRoutes(String workItemQueueUri) {



        for (LocalFileSystemLandingZone lz : landingZoneManager.getLandingZones()) {
            configureRoutePerLandingZone(workItemQueueUri, lz);
        }

        // Copy and paste - yikes.
        configureCommonRoute(workItemQueueUri);


        // Maestro route is creating work items that can be distributed to pit nodes.

        from(workItemQueueUri).routeId(WORK_ITEM_ROUTE).choice()
            .when(header("IngestionMessageType").isEqualTo(MessageType.DATA_STAGED.name()))
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Maestor Creating Music Sheets.")
            .process(maestroOutputProcessor).to(symphonyQueueUri);

    }

    private void configureRoutePerLandingZone(String workItemQueueUri, LocalFileSystemLandingZone lz) {
        String inboundDir = lz.getDirectory().getAbsolutePath();
        log.info("Configuring route for landing zone: {} ", inboundDir);
        // routeId: ctlFilePoller
        from(
                "file:" + inboundDir + "?include=^(.*)\\." + FileFormat.CONTROL_FILE.getExtension() + "$" + "&move="
                        + inboundDir + "/.done/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}" + "&moveFailed="
                        + inboundDir + "/.error/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}" + "&readLock=changed")
                .routeId("ctlFilePoller-" + inboundDir)
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing file.")
                .process(controlFilePreProcessor).to(workItemQueueUri);

        // routeId: zipFilePoller
        from(
                "file:" + inboundDir + "?include=^(.*)\\." + FileFormat.ZIP_FILE.getExtension() + "$&preMove="
                        + inboundDir + "/.done&moveFailed=" + inboundDir + "/.error" + "&readLock=changed")
                .routeId("zipFilePoller-" + inboundDir)
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing zip file.")
                .process(zipFileProcessor).choice().when(header("hasErrors").isEqualTo(true)).to("direct:stop")
                .otherwise().process(controlFilePreProcessor).to(workItemQueueUri);
    }

    private void configureCommonRoute(String workItemQueueUri) {
        // routeId: workItemRoute -> main ingestion route: ctlFileProcessor -> xmlFileProcessor ->
        // edFiProcessor -> persistenceProcessor
        from(workItemQueueUri)
                .routeId(WORK_ITEM_ROUTE)
                .choice()
                .when(header("IngestionMessageType").isEqualTo(MessageType.BATCH_REQUEST.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing control file.")
                .process(ctlFileProcessor)
                .to("direct:assembledJobs")
                .when(header("IngestionMessageType").isEqualTo(MessageType.PURGE.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor",
                        "- ${id} - ${file:name} - Performing Purge Operation.").process(purgeProcessor)
                .to("direct:stop")
                .when(header("IngestionMessageType").isEqualTo(MessageType.CONTROL_FILE_PROCESSED.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing xml file.")
                .process(xmlFileProcessor).to(workItemQueueUri)
                .when(header("IngestionMessageType").isEqualTo(MessageType.XML_FILE_PROCESSED.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Job Pipeline for file.")
                .process(edFiProcessor).to(workItemQueueUri);

    }

    /** I think this will need to go and use a seda queue for developer testing. */
    private void configureSingleNodeRoute(String workItemQueueUri) {

        from(workItemQueueUri).routeId(WORK_ITEM_ROUTE)
        .choice().when(header("IngestionMessageType").isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Data transformation.")
                .process(transformationProcessor).to(workItemQueueUri)
                .when(header("IngestionMessageType").isEqualTo(MessageType.PERSIST_REQUEST.name()))
                .to("direct:persist").when(header("IngestionMessageType").isEqualTo(MessageType.ERROR.name()))
                .log("Error: ${header.ErrorMessage}").to("direct:stop").otherwise().to("direct:stop");

        // routeId: jobDispatch
        from("direct:assembledJobs")
                .routeId("jobDispatch")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Dispatching jobs for file.")
                .choice().when(header("hasErrors").isEqualTo(true)).to("direct:stop").otherwise().to(workItemQueueUri);


        configurePersistenceRoute();

        // routeId: jobReporting
        from("direct:jobReporting")
                .routeId("jobReporting")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor",
                        "- ${id} - ${file:name} - Reporting on jobs for file.").process(jobReportingProcessor);

        // end of routing
        from("direct:stop").routeId("stop").wireTap("direct:jobReporting")
                .log("end of job: " + header("jobId").toString())
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - File processed.").stop();
    }

    private void configurePersistenceRoute() {

        // routeId: persistencePipeline

        from("direct:persist")
                .routeId("persistencePipeline")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Persisiting data for file.")
                .log("persist: jobId: " + header("jobId").toString()).choice().when(header("dry-run").isEqualTo(true))
                .log("job has errors or dry-run specified; data will not be published").to("direct:stop").otherwise()
                .log("publishing data now!").process(persistenceProcessor).to("direct:stop");
    }

}
