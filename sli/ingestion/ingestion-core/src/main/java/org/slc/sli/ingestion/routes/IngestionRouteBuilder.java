package org.slc.sli.ingestion.routes;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.JobReportingProcessor;
import org.slc.sli.ingestion.processors.NeutralRecordsMergeProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.PurgeProcessor;
import org.slc.sli.ingestion.processors.TransformationProcessor;
import org.slc.sli.ingestion.processors.XmlFileProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * Ingestion route builder.
 *
 * @author okrook
 *
 */
@Component
public class IngestionRouteBuilder extends SpringRouteBuilder {

    @Autowired
    EdFiProcessor edFiProcessor;

    @Autowired
    ControlFileProcessor ctlFileProcessor;

    @Autowired
    ZipFileProcessor zipFileProcessor;

    @Autowired
    PurgeProcessor purgeProcessor;

    @Autowired(required = true)
    PersistenceProcessor persistenceProcessor;

    @Autowired
    TransformationProcessor transformationProcessor;

    @Autowired
    NeutralRecordsMergeProcessor nrMergeProcessor;

    @Autowired
    XmlFileProcessor xmlFileProcessor;

    @Autowired
    LocalFileSystemLandingZone lz;

    @Autowired
    LocalFileSystemLandingZone tempLz;

    @Value("${sli.ingestion.queue.workItem.queueURI}")
    private String workItemQueue;

    @Value("${sli.ingestion.queue.workItem.concurrentConsumers}")
    private int concurrentConsumers;

    @Override
    public void configure() throws Exception {
        String workItemQueueUri = workItemQueue + "?concurrentConsumers=" + concurrentConsumers;

        String inboundDir = lz.getDirectory().getPath();

        // routeId: ctlFilePoller
        from(
                "file:" + inboundDir + "?include=^(.*)\\.ctl$"
                        + "&move=" + inboundDir + "/.done/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}"
                        + "&moveFailed=" + inboundDir + "/.error/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}"
                        + "&readLock=changed")
                .routeId("ctlFilePoller")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing file.")
                .process(new ControlFilePreProcessor(lz))
                .to(workItemQueueUri);

        // routeId: zipFilePoller
        from(
                "file:" + inboundDir + "?include=^(.*)\\.zip$&preMove="
                        + inboundDir + "/.done&moveFailed=" + inboundDir
                        + "/.error"
                        + "&readLock=changed")
                .routeId("zipFilePoller")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing zip file.")
                .process(zipFileProcessor)
                .choice()
                .when(body().isInstanceOf(BatchJob.class))
                    .to("direct:assembledJobs")
                .otherwise()
                    .process(new Processor() {

                        // set temporary path to where the files were unzipped
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            File ctlFile = exchange.getIn().getBody(File.class);
                            tempLz.setDirectory(ctlFile.getParentFile());
                        }
                    }).process(new ControlFilePreProcessor(tempLz))
                    .to(workItemQueueUri);

       // routeId: workItemRoute -> main ingestion route: ctlFileProcessor -> xmlFileProcessor -> edFiProcessor -> persistenceProcessor
        from(workItemQueueUri)
            .routeId("workItemRoute")
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
                .to(workItemQueueUri)
            .when(header("IngestionMessageType").isEqualTo(MessageType.DATA_TRANSFORMATION.name()))
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Data transformation.")
                .process(transformationProcessor)
                .to(workItemQueueUri)
// Call to NeutralRecordMergeProcessor has been deprecated.
//            .when(header("IngestionMessageType").isEqualTo(MessageType.MERGE_REQUEST.name()))
//                .process(nrMergeProcessor)
//                .to(workItemQueueUri)
            .when(header("IngestionMessageType").isEqualTo(MessageType.PERSIST_REQUEST.name()))
                .to("direct:persist")
            .when(header("IngestionMessageType").isEqualTo(MessageType.ERROR.name()))
                .log("Error: ${header.ErrorMessage}")
                .to("direct:stop")
            .otherwise()
                .to("direct:stop");


        // routeId: jobDispatch
        from("direct:assembledJobs")
                .routeId("jobDispatch")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Dispatching jobs for file.")
                .choice()
                .when(header("hasErrors").isEqualTo(true))
                    .to("direct:stop")
                .otherwise()
                    .to(workItemQueueUri);

        // routeId: persistencePipeline
        from("direct:persist")
                .routeId("persistencePipeline")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Persisiting data for file.")
                .log("persist: jobId: " + header("jobId").toString())
                .choice()
                .when(header("dry-run").isEqualTo(true))
                    .log("job has errors or dry-run specified; data will not be published")
                    .to("direct:stop")
                .otherwise()
                    .log("publishing data now!")
                    .process(persistenceProcessor)
                    .to("direct:stop");

        // routeId: jobReporting
        from("direct:jobReporting")
                .routeId("jobReporting")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Reporting on jobs for file.")
                .process(new JobReportingProcessor(lz));

        // end of routing
        from("direct:stop")
                .routeId("stop")
                .wireTap("direct:jobReporting")
                .log("end of job: " + header("jobId").toString())
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - File processed.")
                .stop();

    }

}
