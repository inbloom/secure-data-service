package org.slc.sli.ingestion.routes;

import static org.apache.camel.builder.PredicateBuilder.or;

import java.io.File;
import java.util.Enumeration;

import ch.qos.logback.classic.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobLogger;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired(required = true)
    PersistenceProcessor persistenceProcessor;

    @Autowired
    LocalFileSystemLandingZone lz;

    @Autowired
    LocalFileSystemLandingZone tempLz;

    @Override
    public void configure() throws Exception {

        String inboundDir = lz.getDirectory().getPath();

        // routeId: ctlFilePoller
        from(
                "file:" + inboundDir + "?include=^(.*)\\.ctl$"
                        + "&move=" + inboundDir + "/.done/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}"
                        + "&moveFailed=" + inboundDir + "/.error/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}")
                .routeId("ctlFilePoller")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "Processing file ${file:name} ${id}")
                .process(new ControlFilePreProcessor(lz))
                .to("seda:CtrlFilePreProcessor");

        // routeId: ctlFilePreprocessor
        from("seda:CtrlFilePreProcessor")
                .routeId("ctlFilePreprocessor")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "Processing control file ${file:name} ${id}")
                .process(ctlFileProcessor)
                .to("seda:assembledJobs");


        // routeId: zipFilePoller
        from(
                "file:" + inboundDir + "?include=^(.*)\\.zip$&preMove="
                        + inboundDir + "/.done&moveFailed=" + inboundDir
                        + "/.error")
                .routeId("zipFilePoller")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "Processing zip file ${file:name} ${id}")
                .process(zipFileProcessor)
                .choice()
                    .when(body().isInstanceOf(BatchJob.class))
                    .to("seda:assembledJobs")
                .otherwise()
                    .process(new Processor() {

                        // set temporary path to where the files were unzipped
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            File ctlFile = exchange.getIn().getBody(File.class);
                            tempLz.setDirectory(ctlFile.getParentFile());
                        }
                    }).process(new ControlFilePreProcessor(tempLz))
                    .to("seda:CtrlFilePreProcessor");

        // routeId: jobDispatch
        from("seda:assembledJobs")
                .routeId("jobDispatch")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "Dispathing jobs for file ${file:name} ${id}")
                .choice()
                    .when(header("hasErrors").isEqualTo(true))
                    .to("direct:stop")
                .otherwise()
                    .to("seda:acceptedJobs");

        // routeId: jobReporting
        from("direct:jobReporting")
                .routeId("jobReporting")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "Reporting on jobs for file ${file:name} ${id}")
                .process(new Processor() {

                    @Override
                    public void process(Exchange exchange) throws Exception {

                        // get job from exchange
                        BatchJob job = exchange.getIn().getBody(BatchJob.class);

                        Logger jobLogger = BatchJobLogger.createLoggerForJob(job, lz);

                        // add output as lines
                        jobLogger.info("jobId: " + job.getId());
                        
                        for (IngestionFileEntry fileEntry : job.getFiles()) {
                            jobLogger.info("[file] " + fileEntry.getFileName()
                                    + " (" + fileEntry.getFileFormat() + "/"
                                    + fileEntry.getFileType() + ")");
                        }

                        Enumeration names = job.propertyNames();
                        while (names.hasMoreElements()) {
                            String key = (String) names.nextElement();
                            jobLogger.info("[configProperty] " + key + ": "
                                    + job.getProperty(key));
                        }

                        FaultsReport fr = job.getFaultsReport();

                        for (Fault fault: fr.getFaults()) {
                            if (fault.isError()) {
                                jobLogger.error(fault.getMessage());
                            } else {
                                jobLogger.warn(fault.getMessage());
                            }
                        }

                        if (fr.hasErrors()) {
                            jobLogger.info("Job rejected due to errors");
                        } else {
                            jobLogger.info("Job ready for processing");
                        }

                        jobLogger.info("Ingested " + exchange.getProperty("records.processed") + " records into datastore.");
                        
                        // clean up after ourselves
                        jobLogger.detachAndStopAllAppenders();
                    }

                });

        // routeId: jobPipeline
        from("seda:acceptedJobs")
                .routeId("jobPipeline")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "Job Pipeline for file ${file:name} ${id}")
                .process(edFiProcessor)
                .to("seda:persist");

        // routeId: persistencePipeline
        from("seda:persist")
                .routeId("persistencePipeline")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "Persisiting data for file ${file:name} ${id}")
                .log("persist: jobId: " + header("jobId").toString())
                .choice()
                    .when(or(header("dry-run").isEqualTo(true), header("hasErrors").isEqualTo(true)))
                        .log("job has errors or dry-run specified; data will not be published")
                        .to("direct:stop")
                    .otherwise()
                        .log("publishing data now!")
                        .process(persistenceProcessor)
                        .to("direct:stop");

        // end of routing
        from("direct:stop")
                .routeId("stop")
                .wireTap("direct:jobReporting")
                .log("end of job: " + header("jobId").toString())
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "File ${file:name} ${id} processed")
                .stop();
    }

}
