package org.slc.sli.ingestion.processors;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;

import org.slc.sli.ingestion.BatchJobLogger;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;

/**
 * Transforms body from ControlFile to ControlFileDescriptor type.
 *
 * @author bsuzuki
 *
 */
public class JobReportingProcessor implements Processor {

    private LandingZone landingZone;

    public JobReportingProcessor(LandingZone lz) {
        this.landingZone = lz;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        // TODO get job from the batch job db
        NewBatchJob job = exchange.getIn().getBody(NewBatchJob.class);

        Logger jobLogger = BatchJobLogger.createLoggerForJob(job, landingZone);

        // add output as lines
        jobLogger.info("jobId: " + job.getId());

        // TODO BatchJobUtils these errors need to be pulled form the db
        for (ResourceEntry resourceEntry : job.getFileEntries()) {
            String id = "[file] " + resourceEntry.getResourceName();
            jobLogger.info(id + " (" + resourceEntry.getResourceFormat()
                    + "/" + resourceEntry.getResourceType() + ")");
            
            Integer numProcessed = resourceEntry.getRecordCount();
            if (numProcessed != null) {
                jobLogger.info(id + " records considered: "
                    + numProcessed);
            }

            Long numFailed = exchange.getProperty(resourceEntry.getResourceName()
                    + ".records.failed", Long.class);
            if (numFailed != null) {
                jobLogger.info(id + " records failed: "
                    + numFailed);
            }
        }

        Map<String, String> properties = job.getBatchProperties();
        for (Map.Entry<String, String> property : properties.entrySet()) {
             jobLogger.info("[configProperty] " + property.getKey() + ": "
                    + property.getValue());
        }

        // TODO get an error list from the db
//      IngestionMongoLogger ingestionMongoLogger = IngestionMongoLogger.getBatchJobSingleton();
//      JobLogStatus status = ingestionMongoLogger.getErrorsReport(job);

//        FaultsReport fr = job.getFaultsReport();
//        
//        for (Fault fault : fr.getFaults()) {
//            if (fault.isError()) {
//                jobLogger.error(fault.getMessage());
//            } else {
//                jobLogger.warn(fault.getMessage());
//            }
//        }
//
//        if (fr.hasErrors()) {
//            jobLogger.info("Not all records were processed completely due to errors.");
//            // TODO job.setStatus(TYPE_COMPLETEDWITHERRORS);
//        } else {
//            jobLogger.info("All records processed successfully.");
//            // TODO job.setStatus(TYPE_COMPLETEDSUCCESSFULLY);
//        }
//
//        // This header is set in PersistenceProcessor
//        if (exchange.getProperty("records.processed") != null) {
//            jobLogger.info("Processed " + exchange.getProperty("records.processed") + " records.");
//        }

        // clean up after ourselves
        ((ch.qos.logback.classic.Logger) jobLogger).detachAndStopAllAppenders();
        
        // TODO BatchJobUtils cleanup 
//        IngestionMongoLogger ingestionMongoLogger = IngestionMongoLogger.getBatchJobSingleton();
//        JobLogStatus status = ingestionMongoLogger.saveBatchJob(job);
    }

}
