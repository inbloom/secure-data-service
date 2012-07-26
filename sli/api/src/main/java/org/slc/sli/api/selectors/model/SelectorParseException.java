package org.slc.sli.api.selectors.model;

/**
 * Exception thrown when there is a problem parsing the selector
 * @author jstokes
 */
public class SelectorParseException extends Exception {
    public SelectorParseException(String message) {
        super(message);
    }
}
