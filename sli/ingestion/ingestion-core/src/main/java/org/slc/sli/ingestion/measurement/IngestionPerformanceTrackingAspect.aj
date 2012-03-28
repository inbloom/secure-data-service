package org.slc.sli.ingestion.measurement;

import org.apache.camel.Exchange;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.util.performance.PerformanceTrackingAspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for performance measurement of ingestion operations
 * 
 * @author ifaybyshev
 * 
 */
@Aspect
@Component("IngestionPerformanceTrackingAspect")
public class IngestionPerformanceTrackingAspect extends
		PerformanceTrackingAspect {

	// setting ingestion job context: setting of batch job id
	@Pointcut("@annotation(org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext)")
	public void extractBatchJobIdToContext() {
	}

	@Before("extractBatchJobIdToContext() && args(exchange)")
	public void handleExtractBatchJobIdToContextPointcut(Exchange exchange) {
		log.info("================== Assigned Ingestion Batch Id (Setter) = {}", 
				exchange.getIn().getBody(BatchJob.class).getId());
		store.put("ingestionBatchJobId",
				exchange.getIn().getBody(BatchJob.class).getId());
	}

	protected void sendPerformanceMessage(String callerName,
			long startTimestamp, long endTimestamp) {
		performanceLog.info("<ingestionPerformance>" + "<batchId>"
				+ store.get("ingestionBatchJobId") + "</batchId>" + "<caller>"
				+ callerName + "</caller>" + "<timing>" + "<startTimestamp>"
				+ startTimestamp + "</startTimestamp>" + "<endTimestamp>"
				+ endTimestamp + "</endTimestamp>" + "<elapsedMs>"
				+ (endTimestamp - startTimestamp) + "</elapsedMs>"
				+ "</timing>" + "</ingestionPerformance>");

	}

}
