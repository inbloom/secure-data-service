package org.slc.sli.manager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A base class for all the data managers
 * @author agrebneva
 *
 */
public interface Manager {
    /**
     * Annotation to mark entity reference methods
     * @author agrebneva
     *
     */
    @Target(value = { ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EntityMapping {
        String value();
    }
}
