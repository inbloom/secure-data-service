package org.slc.sli.ingestion.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.landingzone.LandingZoneManager;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.nodes.IngestionNodeType;
import org.slc.sli.ingestion.nodes.NodeInfo;
import org.slc.sli.ingestion.processors.CommandProcessor;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.JobReportingProcessor;
import org.slc.sli.ingestion.processors.NoExtractProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.PurgeProcessor;
import org.slc.sli.ingestion.processors.TenantProcessor;
import org.slc.sli.ingestion.processors.TransformationProcessor;
import org.slc.sli.ingestion.processors.XmlFileProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.routes.orchestra.AggregationPostProcessor;
import org.slc.sli.ingestion.routes.orchestra.OrchestraPreProcessor;
import org.slc.sli.ingestion.routes.orchestra.WorkNoteAggregator;
import org.slc.sli.ingestion.tenant.TenantPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private OrchestraPreProcessor orchestraPreProcessor;
    
    @Autowired
    private AggregationPostProcessor aggregationPostProcessor;
    
    @Autowired
    JobReportingProcessor jobReportingProcessor;
    
    @Autowired
    NoExtractProcessor noExtractProcessor;
    
    @Autowired
    LandingZoneManager landingZoneManager;
    
    @Autowired
    CamelContext camelContext;
    
    @Autowired
    private final TenantProcessor tenantProcessor;
    
    @Autowired
    private final TenantPopulator tenantPopulator;
    
    @Autowired
    private NodeInfo nodeInfo;
    
    @Value("${sli.ingestion.queue.workItem.queueURI}")
    private String workItemQueue;
    
    @Value("${sli.ingestion.queue.workItem.concurrentConsumers}")
    private String workItemConsumers;
    
    @Value("${sli.ingestion.queue.maestro.queueURI}")
    private String maestroQueue;
    
    @Value("${sli.ingestion.queue.maestro.concurrentConsumers}")
    private String maestroConsumers;
    
    @Value("${sli.ingestion.queue.maestro.uriOptions}")
    private String maestroUriOptions;
    
    @Value("${sli.ingestion.queue.pit.queueURI}")
    private String pitQueue;
    
    @Value("${sli.ingestion.queue.pit.concurrentConsumers}")
    private String pitConsumers;
    
    @Value("${sli.ingestion.queue.pit.uriOptions}")
    private String pitUriOptions;
    
    @Value("${sli.ingestion.topic.command}")
    private String commandTopicUri;
    
    private final int concurrentConsumers;
    private final boolean loadDefaultTenants;
    private final String tenantPollingRepeatInterval;
    
    @Autowired
    public IngestionRouteBuilder(TenantProcessor tenantProcessor, TenantPopulator tenantPopulator,
            @Value("${sli.ingestion.queue.workItem.queueURI}") String workItemQueue,
            @Value("${sli.ingestion.queue.workItem.concurrentConsumers}") int concurrentConsumers,
            @Value("${sli.ingestion.tenant.loadDefaultTenants}") boolean loadDefaultTenants,
            @Value("${sli.ingestion.tenant.tenantPollingRepeatInterval}") String tenantPollingRepeatInterval) {
        super();
        this.tenantProcessor = tenantProcessor;
        this.tenantPopulator = tenantPopulator;
        this.workItemQueue = workItemQueue;
        this.concurrentConsumers = concurrentConsumers;
        this.loadDefaultTenants = loadDefaultTenants;
        this.tenantPollingRepeatInterval = tenantPollingRepeatInterval;
    }
    
    @Override
    public void configure() throws Exception {
        LOG.info("Configuring node {} for node type {}", nodeInfo.getUUID(), nodeInfo.getNodeType());
        
        String workItemQueueUri = workItemQueue + "?concurrentConsumers=" + workItemConsumers;
        String maestroQueueUri = maestroQueue + "?concurrentConsumers=" + maestroConsumers + maestroUriOptions;
        String pitNodeQueueUri = pitQueue + "?concurrentConsumers=" + pitConsumers + pitUriOptions;
        
        if (IngestionNodeType.MAESTRO.equals(nodeInfo.getNodeType())
                || IngestionNodeType.STANDALONE.equals(nodeInfo.getNodeType())) {
            
            LOG.info("configuring routes for maestro node");
            
            if (loadDefaultTenants) {
                // populate the tenant collection with a default set of tenants
                tenantPopulator.populateDefaultTenants();
            }
            
            for (LocalFileSystemLandingZone lz : landingZoneManager.getLandingZones()) {
                configureLandingZonePollers(workItemQueueUri, lz);
            }
            
            buildExtractionRoutes(workItemQueueUri);
            
            buildMaestroRoutes(maestroQueueUri, pitNodeQueueUri);
            
            configureTenantPollingTimerRoute();
            tenantProcessor.setWorkItemQueueUri(getWorkItemQueueUri());
            this.addRoutesToCamelContext(camelContext);
        }
        
        if (IngestionNodeType.PIT.equals(nodeInfo.getNodeType())
                || IngestionNodeType.STANDALONE.equals(nodeInfo.getNodeType())) {
            
            LOG.info("configuring routes for pit node");
            
            buildPitRoutes(pitNodeQueueUri, maestroQueueUri);
        }
        
        from(this.commandTopicUri).bean(this.lookup(CommandProcessor.class));
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
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - Entering Maestro orchestration.")
                .process(orchestraPreProcessor).choice().when(header("stagedEntitiesEmpty").isEqualTo(true))
                .to("direct:stop").otherwise().to("direct:transformationSplitter");
        
        // transformationSplitter
        // split WorkNotes into separate Exchanges and drop into the pit node queue.
        from("direct:transformationSplitter")
                .routeId("transformationSplitter")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor",
                        "- ${id} - ${file:name} - Maestro deriving and splitting WorkNotes for transformation.")
                .split().method("WorkNoteSplitter", "split")
                .setHeader("IngestionMessageType", constant(MessageType.DATA_TRANSFORMATION.name()))
                .to(pitNodeQueueUri);
        
        // persistenceSplitter
        // act as a pass-through, create separate Exchanges for the list of WorkNotes in the
        // incoming exchange
        // and drop into the pit node queue.
        from("direct:persistenceSplitter")
                .routeId("persistenceSplitter")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor",
                        "- ${id} - Maestro pass-through-splitting WorkNotes for persistance.").split()
                .method("WorkNoteSplitter", "passThroughSplit")
                .setHeader("IngestionMessageType", constant(MessageType.PERSIST_REQUEST.name())).to(pitNodeQueueUri);
        
        // aggregationSwitch
        // a switch to route 'completed' WorkNotes from the maestro queue (coming from pits) to the
        // correct aggregator.
        from(maestroQueueUri).routeId("aggregationSwitch").choice()
                .when(header("IngestionMessageType").isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                .to("direct:transformationAggregator")
                .when(header("IngestionMessageType").isEqualTo(MessageType.PERSIST_REQUEST.name()))
                .to("direct:persistenceAggregator");
        
        // transformationAggregator
        // aggregates WorkNotes based on their IngestionStagedEntity + BatchJobId.
        // the aggregation completion size is pulled from an Exchange header, where it is set by
        // WorkNoteAggregator.
        // the completion size should be the number of batches created for this
        // IngestionStagedEntity.
        from("direct:transformationAggregator")
                .routeId("transformationAggregator")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor",
                        "- ${id} - Maestro aggregating WorkNotes after transformations.")
                .aggregate(simple("${body.getIngestionStagedEntity}${body.getBatchJobId}"), new WorkNoteAggregator())
                .completionSize(simple("${in.header.workNoteByEntityCount}")).to("direct:persistenceSplitter");
        
        // persistenceAggregator
        // aggregates WorkNotes based on their BatchJobId.
        // the aggregation completion size is pulled from an Exchange header, where it is set by
        // WorkNoteAggregator.
        // the completion size should be the total number of WorkNotes created for this 'tier'.
        // unless we've processed all staged entities, route back to transformationSplitter for next
        // 'tier.'
        from("direct:persistenceAggregator")
                .routeId("persistenceAggregator")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor",
                        "- ${id} - Maestro aggregating WorkNotes after persistances.")
                .aggregate(simple("${body.getBatchJobId}"), new WorkNoteAggregator())
                .completionSize(simple("${in.header.totalWorkNoteCount}")).process(aggregationPostProcessor).choice()
                .when(header("processedAllStagedEntities").isEqualTo(true)).to("direct:stop").otherwise()
                .to("direct:transformationSplitter");
    }
    
    /**
     * The starting points of ingestion processing, file pollers for .zip and .ctl files and routing
     * accordingly.
     * 
     * @param workItemQueueUri
     * @param lz
     */
    private void configureLandingZonePollers(String workItemQueueUri, LocalFileSystemLandingZone lz) {
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
        
        from(
                "file:" + inboundDir + "?include=^(.*)\\.noextract$" + "&move=" + inboundDir
                        + "/.done/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}" + "&moveFailed=" + inboundDir
                        + "/.error/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}" + "&readLock=changed")
                .routeId("noextract-" + inboundDir)
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing file.")
                .process(noExtractProcessor).to("direct:postExtract");
    }
    
    /**
     * The common route will get us through the extract phase. When complete, all data will be
     * staged in
     * NeutralRecord format in mongodb.
     * 
     * @param workItemQueueUri
     */
    private void buildExtractionRoutes(String workItemQueueUri) {
        
        // routeId: extraction
        from(workItemQueueUri)
                .routeId("extraction")
                .choice()
                .when(header("IngestionMessageType").isEqualTo(MessageType.ERROR.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor",
                        "- ${id} - ${file:name} - Error in processing, directing to stop.")
                .to("direct:stop")
                
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
                .process(edFiProcessor).to("direct:postExtract");
        
        // routeId: assembledJobs
        from("direct:assembledJobs")
                .routeId("assembledJobs")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Dispatching jobs for file.")
                .choice().when(header("hasErrors").isEqualTo(true)).to("direct:stop").otherwise().to(workItemQueueUri);
        
        // routeId: persistencePipeline
        from("direct:persist")
                .routeId("persistencePipeline")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Persisiting data for file.")
                .log("persist: jobId: " + header("jobId").toString()).choice()
                .when(header(AttributeType.DRYRUN.getName()).isEqualTo(true))
                .log("job has errors or dry-run specified; data will not be published").to("direct:stop").otherwise()
                .log("publishing data now!").process(persistenceProcessor).to("direct:stop");
        
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
    
    /**
     * The TransformPersist route will handle transformation of staged NeutralRecords and persist
     * SLI entities to mongodb.
     * 
     * @param pitNodeQueueUri
     */
    private void buildPitRoutes(String pitNodeQueueUri, String maestroQueueUri) {
        
        // routeId: pitNodes
        from(pitNodeQueueUri)
                .routeId("pitNodes")
                .choice()
                .when(header("IngestionMessageType").isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Data transformation.")
                .process(transformationProcessor)
                .to(maestroQueueUri)
                
                .when(header("IngestionMessageType").isEqualTo(MessageType.PERSIST_REQUEST.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Persisiting data for file.")
                .log("persist: jobId: " + header("jobId").toString()).choice().when(header("dry-run").isEqualTo(true))
                .log("job has dry-run specified; data will not be published").to("direct:stop").otherwise()
                .log("persisting data now!").process(persistenceProcessor).to(maestroQueueUri)
                
                .when(header("IngestionMessageType").isEqualTo(MessageType.ERROR.name()))
                .log("Error: ${header.ErrorMessage}").to("direct:stop");
    }
    
    /**
     * Access the workItemQueueUri.
     */
    public String getWorkItemQueueUri() {
        return workItemQueue + "?concurrentConsumers=" + concurrentConsumers;
    }
    
    public void configureTenantPollingTimerRoute() {
        from(
                "quartz://tenantPollingTimer?trigger.fireNow=true&trigger.repeatCount=-1&trigger.repeatInterval="
                        + tenantPollingRepeatInterval).setBody()
                .simple("TenantPollingTimer fired: ${header.firedTime}").process(tenantProcessor);
    }
}
