package org.slc.sli.api.service.query;

/**
 * Exception indicating a http query request string could not be parsed
 * to spring data query object.
 * 
 */

public class QueryParseException extends RuntimeException{
    
    private static final long serialVersionUID = 3777997711710333578L;
    private String queryString;
    QueryParseException(String queryString){
        super();
        this.queryString=queryString;
    }
    
    public String getQueryString(){
        return this.queryString;
    }
}
