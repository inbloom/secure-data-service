package org.slc.sli.ingestion.processors;

import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobLogger;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NewBatchJobLogger;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.model.da.BatchJobMongoDAStatus;
import org.slc.sli.ingestion.queues.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms body from ControlFile to ControlFileDescriptor type.
 * 
 * @author bsuzuki
 * 
 */
public class JobReportingProcessor implements Processor {
    
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(JobReportingProcessor.class);
    
    private LandingZone landingZone;
    
    public JobReportingProcessor(LandingZone lz) {
        this.landingZone = lz;
    }
    
    @Override
    public void process(Exchange exchange) throws Exception {
        
        processExistingBatchJob(exchange);
        
        // TODO we are doing both in parallel for now, but will replace the existing once testing is
        // done
        // this writes to a newJobxxx.txt output file in the lz
        // processUsingNewBatchJob(exchange);
    }
    
    public void processExistingBatchJob(Exchange exchange) throws Exception {
        
        Job job = exchange.getIn().getBody(BatchJob.class); // existing impl
        
        Logger jobLogger = BatchJobLogger.createLoggerForJob(job.getId(), landingZone);  // existing
                                                                                        // impl
        
        // add output as lines
        jobLogger.info("jobId: " + job.getId());   // existing impl
        
        for (IngestionFileEntry fileEntry : job.getFiles()) {
            String id = "[file] " + fileEntry.getFileName();
            jobLogger.info(id + " (" + fileEntry.getFileFormat() + "/" + fileEntry.getFileType() + ")");
            Long numProcessed = exchange.getProperty(fileEntry.getFileName() + ".records.processed", Long.class);
            if (numProcessed != null) {
                jobLogger.info(id + " records considered: " + numProcessed);
            }
            
            Long numPassed = exchange.getProperty(fileEntry.getFileName() + ".records.passed", Long.class);
            if (numProcessed != null) {
                jobLogger.info(id + " records ingested successfully: " + numPassed);
            }
            
            Long numFailed = exchange.getProperty(fileEntry.getFileName() + ".records.failed", Long.class);
            if (numProcessed != null) {
                jobLogger.info(id + " records failed: " + numFailed);
            }
        }
        
        // existing impl to write out properties
        Enumeration<?> names = job.propertyNames();
        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            jobLogger.info("[configProperty] " + key + ": " + job.getProperty(key));
        }
        
        FaultsReport fr = job.getFaultsReport();
        
        // TODO BatchJobUtils these errors need to be pulled from the db, write the DA interface to
        // get the faults
        for (Fault fault : fr.getFaults()) {
            if (fault.isError()) {
                jobLogger.error(fault.getMessage());
            } else {
                jobLogger.warn(fault.getMessage());
            }
        }
        
        if (exchange.getProperty("purge.complete") != null) {
            jobLogger.info(exchange.getProperty("purge.complete").toString());
        } else {
            if (fr.hasErrors()) {
                jobLogger.info("Not all records were processed completely due to errors.");
            } else {
                jobLogger.info("All records processed successfully.");
            }
        }
        
        // This header is set in PersistenceProcessor
        // TODO get this from the persistence processor
        if (exchange.getProperty("records.processed") != null) {
            jobLogger.info("Processed " + exchange.getProperty("records.processed") + " records.");
        }
        
        // clean up after ourselves
        ((ch.qos.logback.classic.Logger) jobLogger).detachAndStopAllAppenders();
        
    }
    
    private void processUsingNewBatchJob(Exchange exchange) throws Exception {
        
        // get job from the batch job db
        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {
            exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
        }
        BatchJobDAO batchJobDAO = new BatchJobMongoDA();
        NewBatchJob job = batchJobDAO.findBatchJobById(batchJobId);
        
        Stage stage = new Stage();
        stage.setStageName(BatchJobStageType.JOB_REPORTING_PROCESSING.getName());
        stage.startStage();
        
        Logger jobLogger;
        // TODO uncomment below remove NewBatchJobLogger class and call below on switch over
        // used to avoid acceptance tests failing
        // jobLogger = BatchJobLogger.createLoggerForJob(job.getId(), landingZone);
        jobLogger = NewBatchJobLogger.createLoggerForJob(job.getId(), landingZone);
        jobLogger.info("jobId: " + job.getId());
        
        // based on the PersistenceProcessor counts
        int totalProcessed = 0;
        int totalErrors = 0;
        
        // new batch job impl writes out persistence stage resource metrics
        List<Metrics> metrics = job.getStageMetrics(BatchJobStageType.PERSISTENCE_PROCESSING);
        if (metrics != null) {
            for (Metrics metric : metrics) {
                ResourceEntry resourceEntry = job.getResourceEntry(metric.getResourceId());
                if (resourceEntry == null) {
                    jobLogger.error("The resource referenced by metric by resourceId " + metric.getResourceId()
                            + " is not defined for this job");
                    continue;
                }
                
                String id = "[file] " + resourceEntry.getResourceName();
                jobLogger.info(id + " (" + resourceEntry.getResourceFormat() + "/" + resourceEntry.getResourceType()
                        + ")");
                
                Long numProcessed = metric.getRecordCount();
                Long numFailed = metric.getErrorCount();
                Long numPassed = metric.getRecordCount() - numFailed;
                
                jobLogger.info(id + " records considered: " + numProcessed);
                jobLogger.info(id + " records ingested successfully: " + numPassed);
                jobLogger.info(id + " records failed: " + numFailed);
                
                totalProcessed += numProcessed;
                totalErrors += numFailed;
            }
        }
        
        // write properties
        if (job.getBatchProperties() != null) {
            for (Entry<String, String> entry : job.getBatchProperties().entrySet()) {
                jobLogger.info("[configProperty] " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        BatchJobMongoDAStatus status = BatchJobMongoDA.findBatchJobErrors(job.getId());
        if (status != null && status.isSuccess()) {
            
            // TODO handle large numbers of errors
            @SuppressWarnings("unchecked")
            List<Error> errors = (List<Error>) status.getResult();
            for (Error error : errors) {
                if (error.getSeverity().equals(FaultType.TYPE_ERROR.getName())) {
                    jobLogger.error(error.getSourceIp() + " " + error.getHostname() + " " + error.getStageName() + " "
                            + error.getResourceId() + " " + error.getRecordIdentifier() + " " + error.getErrorDetail());
                } else if (error.getSeverity().equals(FaultType.TYPE_WARNING.getName())) {
                    jobLogger.warn(error.getSourceIp() + " " + error.getHostname() + " " + error.getStageName() + " "
                            + error.getResourceId() + " " + error.getRecordIdentifier() + " " + error.getErrorDetail());
                }
                
            }
        }
        
        if (totalErrors == 0) {
            jobLogger.info("All records processed successfully.");
            job.setStatus(BatchJobStatusType.COMPLETED_SUCCESSFULLY.getName());
        } else {
            jobLogger.info("Not all records were processed completely due to errors.");
            job.setStatus(BatchJobStatusType.COMPLETED_WITH_ERRORS.getName());
        }
        
        jobLogger.info("Processed " + totalProcessed + " records.");
        
        // clean up after ourselves
        ((ch.qos.logback.classic.Logger) jobLogger).detachAndStopAllAppenders();
        
        stage.stopStage();
        job.getStages().add(stage);
        batchJobDAO.saveBatchJob(job);
    }
    
}
