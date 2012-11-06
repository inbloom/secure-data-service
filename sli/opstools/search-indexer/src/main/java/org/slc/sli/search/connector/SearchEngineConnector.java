package org.slc.sli.search.connector;

import java.util.List;

import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.springframework.http.HttpStatus;

/**
 * Interface for communicating wiht the search engine via REST
 *
 */
public interface SearchEngineConnector {
    public String executeGet(String url, Object... uriParams);
    public String executePost(String url, String body, Object... uriParams);
    public HttpStatus executePut(String url, String body, Object... uriParams);
    public HttpStatus executeDelete(String url, Object... uriParams);
    public HttpStatus executeHead(String url, Object... uriParams);
    
    public void createIndex(String index);
    public void deleteIndex(String index);
    
    public void putMapping(String index, String type, String mapping);
    
    public String getBaseUrl();
    
    public void execute(Action a, List<IndexEntity> docs);
}
