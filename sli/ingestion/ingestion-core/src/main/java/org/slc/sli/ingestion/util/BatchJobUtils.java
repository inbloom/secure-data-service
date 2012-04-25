package org.slc.sli.ingestion.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 * Utilities for BatchJob
 *
 * @author dduran
 *
 */
public class BatchJobUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BatchJobUtils.class);

    private static final String TIMESTAMPPATTERN = "yyyy-MM-dd:HH-mm-ss";
    private static final FastDateFormat FORMATTER = FastDateFormat.getInstance(TIMESTAMPPATTERN);

    private static InetAddress localhost;
    static {
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Given camel exchange, return batch job using state manager specified in system properties.
     * This should be refactored to be an interface with different implementations.
     *
     * @param exchange
     * @return
     */
    public static Job getBatchJobUsingStateManager(Exchange exchange) {

        Job batchJob = null;

        if ("mongodb".equals(System.getProperty("state.manager"))) {

            // Get the batch job ID from the exchange
            String batchJobId = exchange.getIn().getBody(String.class);
            LOG.info("pulling BatchJob {} from mongodb", batchJobId);

            // TODO usually we will want to startStage(batchJobId, this.getClass().getName()) before
            // getting the job from the db

            // TODO: get batch job from db based on jobId. something like:
            // batchJob = dataAccess.getBatchJob(batchJobId);

        } else {
            LOG.info("pulling BatchJob from camel exchange");
            batchJob = exchange.getIn().getBody(BatchJob.class);

        }
        return batchJob;
    }

    /**
     * Save BatchJob using state maanger specified in system properties.
     * This should be refactored to be an interface with different implementations.
     *
     * @param job
     */

    public static void saveBatchJobUsingStateManager(Job job) {

        if ("mongodb".equals(System.getProperty("state.manager"))) {

            LOG.info("saving BatchJob {} to mongodb", job.getId());
            // TODO usually we will want to set the stage stopTimeStamp before saving the job to db
            // TODO: save batch job to db

        } else {
            // nothing
            LOG.info("camel exchange references updated BatchJob {}", job.getId());
        }
    }

    /**
     * Create a batch job with startTimeStamp of now in the db
     *
     * @param batchJobId
     */
    public static void createBatchJob(String batchJobId) {
        if ("mongodb".equals(System.getProperty("state.manager"))) {

            LOG.info("start a db managed batch job");
            // TODO: create a batch job with startTime now in the db

        }
    }

    /**
     * Complete a batch job setting stopTimeStamp to now in the db
     *
     * @param batchJobId
     */
    public static void completeBatchJob(String batchJobId) {
        if ("mongodb".equals(System.getProperty("state.manager"))) {

            LOG.info("complete a db managed batch job");
            // TODO: update a batch job with with status "completed" and startTime now in the db

        }
    }

    /**
     * Create a stage in the db with startTimeStamp of now
     *
     * @param batchJobId
     * @param stageName
     */
    public static void beginStage(Job job, String stageName) {
        if ("mongodb".equals(System.getProperty("state.manager"))) {

            LOG.info("started a stage in the db managed batch job");
            // TODO: create a stage field with status "running" and startTime of now in the db
        }
    }

    /**
     * Update the stage field in the db to have stopTimeStamp of now
     *
     * @param batchJob
     * @param stageName
     */
    public static void endStage(Job job, String stageName) {
        if ("mongodb".equals(System.getProperty("state.manager"))) {

            LOG.info("stopped the stage in the db managed batch job");
            // TODO: update the stage status to "completed" and the stopTimeStamp to now in the db

        }
    }

    /**
     * Create a metric with startTime of current system time
     *
     * @param batchJobId
     * @param stageName
     * @param fileId
     */
    public static void startMetric(String batchJobId, String stageName, String fileId) {
        if ("mongodb".equals(System.getProperty("state.manager"))) {

            LOG.info("created a metric");
            // TODO: create a metric with startTimeStamp of now in the db

        }
    }

    /**
     * Update the metric in the db with the current time as stopTime and the specified record and
     * error counts
     *
     * @param batchJobId
     * @param stageName
     * @param fileId
     * @param recordCount
     * @param errorCount
     */
    public static void stopMetric(String batchJobId, String stageName, String fileId, int recordCount, int errorCount) {
        if ("mongodb".equals(System.getProperty("state.manager"))) {

            LOG.info("stopped a metric");
            // TODO: update the metric with the stopTimeStamp of now and counts as specified in the
            // db

        }
    }

    public static String getHostAddress() {
        return localhost.getHostAddress();
    }

    public static String getHostName() {
        return localhost.getHostName();
    }

    public static String getCurrentTimeStamp() {
        String timeStamp = FORMATTER.format(System.currentTimeMillis());
        return timeStamp;
    }

    public static void writeErrorsWithDAO(String batchId, BatchJobStageType stage, FaultsReport errorReport,
            BatchJobDAO batchJobDAO) {
        if (errorReport.hasErrors()) {
            String severity;
            List<Fault> faults = errorReport.getFaults();
            for (Fault fault : faults) {
                if (fault.isError()) {
                    severity = FaultType.TYPE_ERROR.getName();
                } else if (fault.isWarning()) {
                    severity = FaultType.TYPE_WARNING.getName();
                } else {
                    // TODO consider adding this to FaultType
                    severity = "UNKNOWN";
                }
                Error error = Error.createIngestionError(batchId, stage.getName(), null, null, null, null, severity,
                        null, fault.getMessage());
                batchJobDAO.saveError(error);
            }
        }
    }

}
