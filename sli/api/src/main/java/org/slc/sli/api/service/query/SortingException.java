package org.slc.sli.api.service.query;

/**
 * Exception for when issues occur with the sorting parameters.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@SuppressWarnings("serial")
public class SortingException extends RuntimeException {
    
    public SortingException(String message) {
        super(message);
    }
    
}
