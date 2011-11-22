package org.slc.sli.api.security;

import org.springframework.security.access.PermissionEvaluator;

/**
 * Extends the permission evaluator interface to provide class to evaluation strategy capabilities.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 * @param <T>
 *            The domain class that this strategy can evaluate the permissions of.
 */
public interface PermissionEvaluationStrategy<T> extends PermissionEvaluator {
    
    /**
     * The domain class that this strategy can evaluate the permissions of.
     * 
     * @return The domain class
     */
    public Class<T> getDomainClass();
    
}
