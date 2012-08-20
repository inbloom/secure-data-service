package org.slc.sli.api.resources.generic;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/20/12
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodNotAllowedException extends RuntimeException {
    private Set<String> allowedMethods;

    public MethodNotAllowedException(Set<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }
}
