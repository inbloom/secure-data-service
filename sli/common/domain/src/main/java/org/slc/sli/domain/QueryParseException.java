package org.slc.sli.domain;

/**
 * Exception indicating a http query request string could not be parsed
 * to spring data query object.
 * 
 * @author dong liu <dliu@wgen.net>
 */

public class QueryParseException extends RuntimeException {
    
    private static final long serialVersionUID = 3777997711710333578L;
    private String queryString;
    
    public QueryParseException(String message, String queryString) {
        super(message);
        this.queryString = queryString;
    }
    
    public String getQueryString() {
        return this.queryString;
    }
}
