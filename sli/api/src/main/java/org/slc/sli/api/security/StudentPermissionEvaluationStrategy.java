package org.slc.sli.api.security;

import java.io.Serializable;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Student;

/**
 * Defines a strategy implementation for evaluating effective privileges of a student object.
 * 
 * The Class StudentPermissionEvaluationStrategy.
 */
@Component
public class StudentPermissionEvaluationStrategy implements PermissionEvaluationStrategy<Student> {
    
    /**
     * Checks for permission.
     * 
     * @param authentication
     *            the authentication
     * @param targetDomainObject
     *            the target domain object
     * @param permission
     *            the permission
     * @return true, if successful
     * 
     * @see org.springframework.security.access.PermissionEvaluator#hasPermission(org.springframework.security.core.Authentication,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // TODO
        return true;
    }
    
    /**
     * @see org.springframework.security.access.PermissionEvaluator#hasPermission(org.springframework.security.core.Authentication,
     *      java.io.Serializable, java.lang.String, java.lang.Object)
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        // TODO
        return true;
    }

    public Class<Student> getDomainClass() {
        return Student.class;
    }
    
}
