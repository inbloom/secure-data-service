package org.slc.sli.common.util.performance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for storing the value that the method returns in the performance context.
 *
 * @author ifaybyshev
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PutResultInContext {

  /**
   * Get the name.
   * @return name
   */
  String returnName() default "";

}
