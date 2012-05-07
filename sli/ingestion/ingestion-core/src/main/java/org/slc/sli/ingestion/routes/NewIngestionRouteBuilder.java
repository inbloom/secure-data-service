package org.slc.sli.ingestion.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.LandingZoneManager;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.JobReportingProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.PurgeProcessor;
import org.slc.sli.ingestion.processors.TransformationProcessor;
import org.slc.sli.ingestion.processors.XmlFileProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.tenant.TenantPopulator;

/**
 * Ingestion route builder.
 *
 * @author ifaybyshev
 *
 */
public class NewIngestionRouteBuilder extends SpringRouteBuilder {

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

    @Override
    public void configure() throws Exception {
        String workItemQueueUri = workItemQueue + "?concurrentConsumers=" + concurrentConsumers;

        //routing for zip and control file polling
        configureRoutingForLZPollers(workItemQueueUri);

        //routing for operations necessary for staging data
        configureRoutingForDataStaging(workItemQueueUri);

        //splitting entities based on level
        configureRoutingForSplittingPerDataLevel(workItemQueueUri);

        //routing for transforming and persisting per entity level
        configureRoutingForTransformingAndPersisting(workItemQueueUri);

        //tenant-level purge support
        configureRoutingForPurge(workItemQueueUri);

        //end-point for the routing (stop)
        configureRoutingForTerminating(workItemQueueUri);

    }

    public void configureRoutingForSplittingPerDataLevel(String queueUri) {
        from("direct:splitJobIntoSubMessages")
            .routeId("LEVEL_SPLITTER")
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - Processing message for splitting.")
            .split().method("entityLevelSplitter", "splitAndDistributeTopLevelEntities")
            .to("seda:splitMessageQueue")
            .end();

        from("seda:splitMessageQueue")
            .routeId("SPLIT_MESSAGE_PROCESSOR")
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - Processing split sub-message.")
            .to("direct:postExtract");

        from("seda:aggregateSplitMessagesQueue")
            .routeId("MESSAGE_AGGREGATOR")
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - Processing sub-message aggregation.")
            //.aggregate(simple("${body.getBatchJobId}"), new SplitMessageAggregationStrategy())
            .aggregate(header("BatchJobId"), new SplitMessageAggregationStrategy())
                //.completionPredicate(body().contains("END"))
                .completionSize(3)
            .to("seda:messagePostAggregationQueue");

        from("seda:messagePostAggregationQueue")
            .routeId("MESSAGE_AGGREGATED")
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - Post-processing message aggregation.")
            /*
            .choice()
                .when(header("IngestionMessageType").isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                    .to("direct:splitJobIntoSubMessages")
                .otherwise()
                    .to("direct:stop");
            */
            .to("direct:stop");

    }

    public void configureRoutingForTransformingAndPersisting(String queueUri) {
        from("direct:postExtract")
        .routeId("POST_EXTRACT")
        .choice()
            .when(header("IngestionMessageType").isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Data transformation.")
                .process(transformationProcessor)
                .to("direct:persist");

        from("direct:persist")
        .routeId("persistencePipeline")
        .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Persisiting data for file.")
        .log("persist: jobId: " + header("jobId").toString())
        .choice()
            .when(header("dry-run").isEqualTo(true))
                .log("job has errors or dry-run specified; data will not be published")
                .to("seda:aggregateSplitMessagesQueue")
            .otherwise()
                .log("publishing data now!")
                .process(persistenceProcessor)
                .to("seda:aggregateSplitMessagesQueue");
    }

    public void configureRoutingForDataStaging(String queueUri) {
        from(queueUri)
        .routeId("DATA_STAGING_ROUTE")
        .choice()
            //control file processing
            .when(header("IngestionMessageType").isEqualTo(MessageType.BATCH_REQUEST.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing control file.")
                .process(ctlFileProcessor)
                .to("direct:assembledJobs")

            //xml file processing (xml reference resolution)
            .when(header("IngestionMessageType").isEqualTo(MessageType.CONTROL_FILE_PROCESSED.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing xml file.")
                .process(xmlFileProcessor)
                .to(queueUri)

            //edfi parsing + staging data
            .when(header("IngestionMessageType").isEqualTo(MessageType.XML_FILE_PROCESSED.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Job Pipeline for file.")
                .process(edFiProcessor)
                //.to(queueUri);
                .to("direct:splitJobIntoSubMessages");

        from("direct:assembledJobs")
            .routeId("JOB_DISPATCH")
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Dispatching jobs for file.")
            .choice()
            .when(header("hasErrors").isEqualTo(true))
                .to("direct:stop")
            .otherwise()
                .to(queueUri);
    }

    public void configureRoutingForLZPollers(String queueUri) {
        if (loadDefaultTenants) {
            // populate the tenant collection with a default set of tenants
            tenantPopulator.populateDefaultTenants();
        }

        // configure ctlFilePoller and zipFilePoller per landing zone
        for (LocalFileSystemLandingZone lz : landingZoneManager.getLandingZones()) {

            String inboundDir = lz.getDirectory().getAbsolutePath();

            log.info("Configuring route for landing zone: {} ", inboundDir);

            // routeId: ctlFilePoller
            from("file:" + inboundDir + "?include=^(.*)\\." + FileFormat.CONTROL_FILE.getExtension() + "$" + "&move="
                            + inboundDir + "/.done/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}" + "&moveFailed="
                            + inboundDir + "/.error/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}" + "&readLock=changed")
                .routeId("ctlFilePoller-" + inboundDir)
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing file.")
                .process(controlFilePreProcessor)
                .to(queueUri);

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
                        .to(queueUri);
        }
    }

    public void configureRoutingForTerminating(String queueUri) {
        from("direct:stop")
            .routeId("stop")
            .wireTap("direct:jobReporting")
            .log("end of job: " + header("jobId").toString())
            .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - File processed.")
            .stop();
    }

    public void configureRoutingForPurge(String queueUri) {
        /*
        from(queueUri)
        .routeId("DATA_PURGE_ROUTE")
        .choice()
            .when(header("IngestionMessageType").isEqualTo(MessageType.PURGE.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Performing Purge Operation.")
                .process(purgeProcessor)
                .to("direct:stop");
        */
    }

}
