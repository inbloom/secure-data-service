package org.slc.sli.ingestion.util;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.BatchJob;

/**
 * Utilities for BatchJob
 *
 * @author dduran
 *
 */
public class BatchJobUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BatchJobUtils.class);

    /**
     * Given camel exchange, return batch job using state manager specified in system properties.
     * This should be refactored to be an interface with different implementations.
     *
     * @param exchange
     * @return
     */
    public static BatchJob getBatchJobUsingStateManager(Exchange exchange) {

        BatchJob batchJob = exchange.getIn().getBody(BatchJob.class);

        if ("mongodb".equals(System.getProperty("state.manager"))) {

            LOG.info("pulling BatchJob {} from mongodb", batchJob.getId());

            // TODO: get batch job from db based on jobId. something like:
            // batchJob = dataAccess.getBatchJob(batchJob.getId();

        } else {
            LOG.info("pulling BatchJob from camel exchange");
        }
        return batchJob;
    }

    /**
     * Save BatchJob using state maanger specified in system properties.
     * This should be refactored to be an interface with different implementations.
     *
     * @param job
     */
    public static void saveBatchJobUsingStateManager(BatchJob job) {

        if ("mongodb".equals(System.getProperty("state.manager"))) {

            // TODO: save batch job to db
            LOG.info("saving BatchJob {} to mongodb", job.getId());
        } else {
            // nothing
            LOG.info("camel exchange references updated BatchJob", job.getId());
        }
    }

}
