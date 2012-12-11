package org.slc.sli.dal.migration.strategy;

/**
 * An exception that occurs during migration of data from one version to another.
 * 
 * @author kmyers
 *
 */
public class MigrationException extends Exception {
    
    private Exception e;
    
    /**
     * 
     * @param e
     */
    public MigrationException(Exception e) {
        this.e = e;
    }
    
    public Exception getUnderlyingException() {
        return this.e;
    }
}
