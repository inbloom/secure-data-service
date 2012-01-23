package org.slc.sli.api.security.resolve;

/**
 * Used to determine whether a user from the given realm is capable of being
 * an SLI Administrator.
 * 
 * @author pwolf
 * 
 */
public interface SliAdminValidator {
    
    public boolean isSliAdminRealm(String realm);
    
}
