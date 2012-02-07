package org.slc.sli.ingestion.measurement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for monitoring batch job id setting
 *
 * @author ifaybyshev
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExtractBatchJobIdToContext {

}
