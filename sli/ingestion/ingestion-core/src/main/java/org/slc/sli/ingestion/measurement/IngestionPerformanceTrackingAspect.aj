package org.slc.sli.ingestion.measurement;

import org.apache.camel.Exchange;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.performance.PerformanceTrackingAspect;

/**
 * Aspect for performance measurement of ingestion operations
 *
 * @author ifaybyshev
 *
 */
@Aspect
@Component("IngestionPerformanceTrackingAspect")
public class IngestionPerformanceTrackingAspect extends PerformanceTrackingAspect {

    // setting ingestion job context: setting of batch job id
    @Pointcut("@annotation(org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext)")
    public void extractBatchJobIdToContext() {
    }

    @Before("extractBatchJobIdToContext() && args(exchange)")
    public void handleExtractBatchJobIdToContextPointcut(Exchange exchange) {
        log.debug("================== Assigned Ingestion Batch Id (Setter) = {}",
                exchange.getIn().getHeader("BatchJobId", String.class));
        store.put("ingestionBatchJobId", exchange.getIn().getHeader("BatchJobId", String.class));
    }

    protected void sendPerformanceMessage(String callerName, long startTimestamp, long endTimestamp) {
        performanceLog.info("<ingestionPerformance>" + "<batchId>" + store.get("ingestionBatchJobId") + "</batchId>"
                + "<caller>" + callerName + "</caller>" + "<timing>" + "<startTimestamp>" + startTimestamp
                + "</startTimestamp>" + "<endTimestamp>" + endTimestamp + "</endTimestamp>" + "<elapsedMs>"
                + (endTimestamp - startTimestamp) + "</elapsedMs>" + "</timing>" + "</ingestionPerformance>");

    }

}
