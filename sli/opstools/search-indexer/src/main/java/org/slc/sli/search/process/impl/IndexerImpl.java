package org.slc.sli.search.process.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.process.Indexer;
import org.slc.sli.search.transform.IndexEntityConverter;
import org.slc.sli.search.util.SearchIndexerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

/**
 * Indexer is responsible for building elastic search index requests and
 * sending them to the elastic search server for processing.
 * 
 * @author dwu
 * 
 */
public class IndexerImpl implements Indexer {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final int DEFAULT_BULK_SIZE = 5000;
    private static final int MAX_AGGREGATE_PERIOD = 2000;
    
    private IndexEntityConverter indexEntityConverter;
    
    private String esUri;
    
    private RestOperations searchTemplate;
    
    private String esUsername;
    
    private String esPassword;
    
    private int bulkSize = DEFAULT_BULK_SIZE;
    // queue of indexrequests limited to bulkSize
    LinkedBlockingQueue<IndexEntity> indexRequests = new LinkedBlockingQueue<IndexEntity>(DEFAULT_BULK_SIZE * 2);
    
    private ScheduledExecutorService indexExecutor = Executors.newScheduledThreadPool(1);
    // last message timestamp
    private long lastUpdate = 0L;

    private long aggregatePeriodInMillis = MAX_AGGREGATE_PERIOD;

    /**
     * Issue bulk index request if reached the size or not empty and last update past tolerance period
     *
     */
    private class IndexQueueMonior implements Runnable {
        public void run() {
            try {
                if (indexRequests.size() >= bulkSize || 
                    (indexRequests.size() > 0 && (System.currentTimeMillis() - lastUpdate > aggregatePeriodInMillis))) {
                    indexExecutor.schedule(new Runnable() {public void run() {flushQueue();}}, 10, TimeUnit.MILLISECONDS);
                }
            } catch (Throwable t) {
                logger.info("Unable to index with elasticsearch", t);
            }
        }
        
    }
    
    /**
     * flush/index accumulated index records
     */
    public void flushQueue() {
        final List<IndexEntity> col = new ArrayList<IndexEntity>();
        indexRequests.drainTo(col);
        if (!col.isEmpty())
            executeBulkHttp(col);
    }
    
    public void init() {
        logger.info("Indexer started");
        indexExecutor.scheduleAtFixedRate(new IndexQueueMonior(), aggregatePeriodInMillis, aggregatePeriodInMillis, TimeUnit.MILLISECONDS);
    }
    
    public void destroy() {
        indexExecutor.shutdown();
    }
    
    /* (non-Javadoc)
     * @see org.slc.sli.search.process.Indexer#index(org.slc.sli.search.entity.IndexEntity)
     */
    public void index(IndexEntity entity) {
        try {
            indexRequests.put(entity);
            lastUpdate = System.currentTimeMillis();
        } catch (InterruptedException e) {
            throw new SearchIndexerException("Shutting down...");
        } 
    }
    
    /**
     * Takes a collection of index requests, builds a bulk http message to send to elastic search
     * 
     * @param indexRequests
     */
    public void executeBulkHttp(List<IndexEntity> indexRequests) {
        logger.info("Sending bulk index request with " + indexRequests.size() + " records");
        // create bulk http message
        StringBuilder message = new StringBuilder();
        
        /*
         * format of message data
         * { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
         * { "field1" : "value1" }
         */
        
        // add each index request to the message
        while (!indexRequests.isEmpty()) {
            message.append(indexEntityConverter.toIndexJson(indexRequests.remove(0)));
        }
        // send the message
        ResponseEntity<String> response = sendRESTCall(message.toString());
        logger.info("Bulk index response: " + response.getStatusCode());
        logger.debug("Bulk index response: " + response.getBody());
        
        // TODO: do we need to check the response status of each part of the bulk request?
        
    }
    
    /**
     * Send REST query to elasticsearch server
     * 
     * @param query
     * @return
     */
    private ResponseEntity<String> sendRESTCall(String query) {
        HttpMethod method = HttpMethod.POST;
        HttpHeaders headers = new HttpHeaders();
        
        // Basic Authentication when username and password are provided
        if (esUsername != null && esPassword != null) {
            headers.set("Authorization",
                    "Basic " + Base64.encodeBase64String((esUsername + ":" + esPassword).getBytes()));
        }
        
        HttpEntity<String> entity = new HttpEntity<String>(query, headers);
        
        // make the REST call
        try {
            return searchTemplate.exchange(esUri, method, entity, String.class);
        } catch (RestClientException rce) {
            logger.error("Error sending elastic search request!", rce);
            throw rce;
        }
    }
    
    public void setSearchUrl(String esUrl) {
        this.esUri = esUrl + "/_bulk";
    }
    
    public void setSearchUsername(String esUsername) {
        this.esUsername = esUsername;
    }
    
    public void setSearchPassword(String esPassword) {
        this.esPassword = esPassword;
    }
    
    public void setBulkSize(int bulkSize) {
        this.bulkSize = bulkSize;
    }
    
    public void setSearchTemplate(RestOperations searchTemplate) {
        this.searchTemplate = searchTemplate;
    }
    
    public void setAggregatePeriod(long aggregatePeriodInMillis) {
        this.aggregatePeriodInMillis  = aggregatePeriodInMillis;
    }
    
    public void setIndexEntityConverter(IndexEntityConverter indexEntityConverter) {
        this.indexEntityConverter = indexEntityConverter;
    }
}
