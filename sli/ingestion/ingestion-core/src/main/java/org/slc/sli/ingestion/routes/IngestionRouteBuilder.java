package org.slc.sli.ingestion.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.landingzone.LandingZoneManager;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.JobReportingProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.PurgeProcessor;
import org.slc.sli.ingestion.processors.TenantProcessor;
import org.slc.sli.ingestion.processors.TransformationProcessor;
import org.slc.sli.ingestion.processors.XmlFileProcessor;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.tenant.TenantPopulator;

/**
 * Ingestion route builder.
 *
 * @author okrook
 *
 */
@Component
public class IngestionRouteBuilder extends SpringRouteBuilder implements InitializingBean {

    @Autowired
    private ControlFileProcessor ctlFileProcessor;

    @Autowired
    private EdFiProcessor edFiProcessor;

    @Autowired
    private PurgeProcessor purgeProcessor;

    @Autowired
    private PersistenceProcessor persistenceProcessor;

    @Autowired
    private TransformationProcessor transformationProcessor;

    @Autowired
    private XmlFileProcessor xmlFileProcessor;

    @Autowired
    private JobReportingProcessor jobReportingProcessor;

    @Autowired
    private final TenantProcessor tenantProcessor;

    @Autowired
    private LandingZoneManager landingZoneManager;

    @Autowired
    private CamelContext camelContext;

    private final TenantPopulator tenantPopulator;

    private final String workItemQueue;

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
        String workItemQueueUri = getWorkItemQueueUri();

        if (loadDefaultTenants) {
            // populate the tenant collection with a default set of tenants
            tenantPopulator.populateDefaultTenants();
        }

        configureTenantPollingTimerRoute();
        configureCommonRoute(workItemQueueUri);
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
                .simple("TenantPollingTimer fired: ${header.firedTime}")
                //.log(LoggingLevel.DEBUG, "Job.PerformanceMonitor", "TenantPollingTimer fired: ${header.firedTime}")
                .process(tenantProcessor);
    }

    private void configureCommonRoute(String workItemQueueUri) {
        // routeId: workItemRoute -> main ingestion route: ctlFileProcessor -> xmlFileProcessor ->
        // edFiProcessor -> persistenceProcessor
        from(workItemQueueUri)
                .routeId("workItemRoute")
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
                .process(edFiProcessor).to(workItemQueueUri)
                .when(header("IngestionMessageType").isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
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

        // routeId: persistencePipeline
        from("direct:persist")
                .routeId("persistencePipeline")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Persisiting data for file.")
                .log("persist: jobId: " + header("jobId").toString()).choice().when(header("dry-run").isEqualTo(true))
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

    @Override
    public void afterPropertiesSet() throws Exception {
        tenantProcessor.setWorkItemQueueUri(getWorkItemQueueUri());
        this.addRoutesToCamelContext(camelContext);
    }

}
