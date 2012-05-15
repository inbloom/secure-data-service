package org.slc.sli.common.util.performance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slc.sli.common.util.threadutil.ThreadLocalStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect for common performance measurements
 *
 * @author ifaybyshev
 *
 */
@Aspect
abstract public class PerformanceTrackingAspect {

	protected Logger performanceLog = LoggerFactory.getLogger("PerformanceLogger");

	protected Logger log = LoggerFactory.getLogger(PerformanceTrackingAspect.class);

	protected ThreadLocalStorage store = new ThreadLocalStorage(String.valueOf(System.currentTimeMillis()));

	// storing a field/value into the store
	@Pointcut("@annotation(org.slc.sli.common.util.performance.PutResultInContext)")
	public void putResultInContext() {
	}

	@AfterReturning(pointcut = "@annotation(returnName)", argNames = "returnName", returning = "returnValue")
	public void handleStoringReturnPointcut(String returnValue,
			PutResultInContext returnName) {
		log.debug("StoreReturnPointcut -- Storing a field Into Performance Local Store: "
				+ returnName.returnName() + " = " + returnValue);
		store.put(returnName.returnName(), returnValue);
	}

	@Pointcut("@annotation(org.slc.sli.common.util.performance.Profiled)")
	public void profiled() {
	}

	@Around("profiled()")
	public Object handleProfiledAnnotation(ProceedingJoinPoint pjp)
			throws Throwable {
		long start = System.currentTimeMillis();

		Object output = pjp.proceed();

		this.profileMethod(pjp, start);

		return output;
	}

	public void profileMethod(ProceedingJoinPoint pjp, long start) {
		this.sendPerformanceMessage(pjp.getThis().getClass().getName() + "."
				+ pjp.getSignature().getName(), start, System.currentTimeMillis());
	}

	protected void sendPerformanceMessage(String callerName,
			long startTimestamp, long endTimestamp) {
		// send message to performance store
		performanceLog.info("<ingestionPerformance>" + "<caller>" + callerName
				+ "</caller>" + "<timing>" + "<startTimestamp>"
				+ startTimestamp + "</startTimestamp>" + "<endTimestamp>"
				+ endTimestamp + "</endTimestamp>" + "<elapsedMs>"
				+ (endTimestamp - startTimestamp) + "</elapsedMs>"
				+ "</timing>" + "</ingestionPerformance>");
	}

}
