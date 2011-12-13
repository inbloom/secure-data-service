package org.slc.sli.ingestion.routes;

import java.io.File;
import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;

@Component
public class IngestionRouteBuilder extends SpringRouteBuilder {

    @Autowired
    EdFiProcessor xmlProcessor;

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
                "file:" + inboundDir + "?include=^(.*)\\.ctl$&move="
                        + inboundDir + "/.done&moveFailed=" + inboundDir
                        + "/.error").routeId("ctlFilePoller")
                .process(new ControlFilePreProcessor(lz))
                .to("seda:CtrlFilePreProcessor");

        // routeId: ctlFilePreprocessor
        from("seda:CtrlFilePreProcessor").process(ctlFileProcessor).to(
                "seda:assembledJobs");

        // routeId: zipFilePoller
        from(
                "file:" + inboundDir + "?include=^(.*)\\.zip$&move="
                        + inboundDir + "/.done&moveFailed=" + inboundDir
                        + "/.error")
                .routeId("zipFilePoller")
                .process(zipFileProcessor)
                .process(new Processor() {

                    // set temporary path to where the files were unzipped
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        File ctlFile = exchange.getIn().getBody(File.class);
                        tempLz.setDirectory(ctlFile.getParentFile());
                    }
                }).process(new ControlFilePreProcessor(tempLz))
                .choice()
                    .when(body().isInstanceOf(BatchJob.class))
                    .to("seda:assembledJobs")
                .otherwise()
                    .to("seda:CtrlFilePreProcessor");

        // routeId: jobDispatch
        from("seda:assembledJobs")
                .routeId("jobDispatch")
                .wireTap("direct:jobReporting")
                .choice()
                    .when(header("hasErrors").isEqualTo(true))
                    .stop()
                .otherwise()
                    .to("seda:acceptedJobs");

        // routeId: jobReporting
        from("direct:jobReporting")
                .routeId("jobReporting")
                .process(new Processor() {

                    @Override
                    public void process(Exchange exchange) throws Exception {

                        // get job from exchange
                        BatchJob job = exchange.getIn().getBody(BatchJob.class);

                        // create a log file
                        File logFile =
                                lz.createFile("job-" + job.getId() + ".log");

                        // create a holder for lines
                        ArrayList<String> lines = new ArrayList<String>();

                        // add output as lines
                        lines.add("jobId: " + job.getId());
                        for (Fault fault: job.getFaults()) {
                            lines.add(fault.toString());
                        }
                        if (job.hasErrors()) {
                            lines.add("job has been rejected due to errors");
                        }

                        // write lines to file
                        FileUtils.writeLines(logFile, lines);
                    }

                });

        // routeId: jobPipeline
        from("seda:acceptedJobs")
                .routeId("jobPipeline")
                .process(

        new Processor() {

            // TEMPORARY SOLUTION
            // inline implementation exists solely to convert the input of
            // type BatchJob to type File (the first file in that job).
            // really we'd like to keep BatchJob as the message content, but it
            // will require refactoring of all the downstream
            // processors/components.

            @Override
            public void process(Exchange exchange) throws Exception {
                BatchJob job = exchange.getIn().getBody(BatchJob.class);
                exchange.getOut().setBody(job.getFiles().get(0));

                // use message headers to relay any job config params however.
                exchange.getOut().setHeader("jobId", job.getId());
                exchange.getOut().setHeader("jobCreationDate",
                        job.getCreationDate());
                exchange.getOut().setHeader("hasErrors",
                        job.hasErrors());
                if (job.getProperty("dry-run") != null) {
                    exchange.getOut().setHeader("dry-run", true);
                }

            }

        })
                .process(xmlProcessor)
                .to("seda:persist");

        // routeId: persistencePipeline
        from("seda:persist")
                .routeId("persistencePipeline")
                .log("persist: jobId: " + header("jobId").toString())
                .choice()
                    .when(header("dry-run").isEqualTo(true))
                        .log("dry-run specified; data will not be published")
                    .otherwise()
                        .log("publishing data now!")
                        .process(persistenceProcessor);
    }

}
