package org.slc.sli.search.connector;

import org.springframework.http.HttpStatus;

/**
 * Interface for communicating wiht the search engine via REST
 * @author agrebneva
 *
 */
public interface SearchEngineConnector {
    public String executeGet(String url, Object... uriParams);
    public String executePost(String url, String body, Object... uriParams);
    public HttpStatus executePut(String url, String body, Object... uriParams);
    public HttpStatus executeDelete(String url, Object... uriParams);
    public HttpStatus executeHead(String url, Object... uriParams);
    
    public String getBaseUrl();
    
    public String getBulkUri();


    public String getMGetUri();


    public String getUpdateUri();


    public String getIndexUri();


    public String getIndexTypeUri();
}
