package org.slc.sli.search.process.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.process.Indexer;
import org.slc.sli.search.util.IndexEntityUtil;
import org.slc.sli.search.util.NestedMapUtil;
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
    
    private String esUri;
    
    private String bulkUri;

    private String mGetUri;
    
    private RestOperations searchTemplate;
    
    private String esUsername;
    
    private String esPassword;
    
    private int bulkSize = DEFAULT_BULK_SIZE;
    // queue of indexrequests limited to bulkSize
    LinkedBlockingQueue<IndexEntity> indexRequests = new LinkedBlockingQueue<IndexEntity>(DEFAULT_BULK_SIZE * 2);
    LinkedBlockingQueue<IndexEntity> updateRequests = new LinkedBlockingQueue<IndexEntity>(DEFAULT_BULK_SIZE);
    
    private final ScheduledExecutorService indexExecutor = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService updateExecutor = Executors.newScheduledThreadPool(3);
    // last message timestamp
    private long lastUpdate = 0L;

    private long aggregatePeriodInMillis = MAX_AGGREGATE_PERIOD;
    
    // this is helpful to avoid adding index mappings for each index operation. The map does not have to be accurate
    // and no harm will be done if re-mapping is issued
    private final ConcurrentHashMap<String, Boolean> knownIndexesMap = new ConcurrentHashMap<String, Boolean>();
    
    private String indexMappingTemplate;

    /**
     * Issue bulk index request if reached the size or not empty and last update past tolerance period
     *
     */
    private class IndexQueueMonitor implements Runnable {
        public void run() {
            try {
                if (indexRequests.size() >= bulkSize || 
                    (indexRequests.size() > 0 && (System.currentTimeMillis() - lastUpdate > aggregatePeriodInMillis))) {
                    indexExecutor.schedule(new Runnable() {public void run() {flushIndexQueue();}}, 10, TimeUnit.MILLISECONDS);
                }
            } catch (Throwable t) {
                logger.info("Unable to index with elasticsearch", t);
            }
        }
    }
    
    private class UpdateQueueMonitor implements Runnable {
        public void run() {
            try {
                updateExecutor.execute(new Runnable() {public void run() {flushUpdateQueue();}});
            } catch (Throwable t) {
                logger.info("Unable to mget with elasticsearch", t);
            }
        }
    }
    
    /**
     * flush/index accumulated index records
     */
    public void flushIndexQueue() {
        final List<IndexEntity> col = new ArrayList<IndexEntity>();
        indexRequests.drainTo(col);
        if (!col.isEmpty())
            executeBulkHttp(col);
    }
    
    /**
     * flush/index accumulated index records
     */
    public void flushUpdateQueue() {
        final List<IndexEntity> col = new ArrayList<IndexEntity>();
        updateRequests.drainTo(col);
        if (!col.isEmpty())
            executeBulkUpdate(col);
    }
    
    public void init() {
        logger.info("Indexer started");
        indexExecutor.scheduleAtFixedRate(new IndexQueueMonitor(), aggregatePeriodInMillis, aggregatePeriodInMillis, TimeUnit.MILLISECONDS);
        updateExecutor.scheduleAtFixedRate(new UpdateQueueMonitor(), aggregatePeriodInMillis, aggregatePeriodInMillis, TimeUnit.MILLISECONDS);
        updateExecutor.execute(new UpdateQueueMonitor());
    }
    
    public void destroy() {
        indexExecutor.shutdown();
        updateExecutor.shutdown();
    }
    
    /* (non-Javadoc)
     * @see org.slc.sli.search.process.Indexer#index(org.slc.sli.search.entity.IndexEntity)
     */
    public void index(IndexEntity ie) {
        try {
            if (ie.isUpdate()) updateRequests.add(ie); else indexRequests.put(ie);
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
    @SuppressWarnings("unchecked")
    public void executeBulkUpdate(List<IndexEntity> updates) {
        ResponseEntity<String> response = null;
        if (updates.isEmpty())
            return;
        Map<String, IndexEntity> indexUpdateMap = new HashMap<String, IndexEntity>();
        for (IndexEntity ie : updates) {
            indexUpdateMap.put(ie.getId(), ie);
        }
        try {
            String request = IndexEntityUtil.getBulkGetJson(updates);
            logger.info(request);
            response = sendRESTCall(HttpMethod.POST, mGetUri, request);
            Map<String, Object> orig;
            List<Map<String, Object>> docs = (List<Map<String, Object>>)IndexEntityUtil.getEntity(response.getBody()).get("docs");
            IndexEntity ie;
            for (Map<String, Object> entity : docs) {
                if ((Boolean)entity.get("exists")) {
                    orig = (Map<String, Object>) entity.get("_source");
                    ie = indexUpdateMap.remove(IndexEntityUtil.getIndexEntity(entity).getId());
                    if (ie != null) {
                    NestedMapUtil.merge(orig, ie.getBody());
                    indexRequests.put(new IndexEntity(ie.getIndex(), ie.getType(), ie.getId(), orig));
                    } else {
                        logger.error("Unable to match response from get " + entity);
                    }
                }
            }
            logger.info("Bulk index response: " + response.getStatusCode());
        } catch (Exception re) {
            logger.error("Error on mget.", re);
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
        /*
         * format of message data
         * { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
         * { "field1" : "value1" }
         */
        
        // add each index request to the message
        String message = IndexEntityUtil.getBulkIndexJson(indexRequests);
        logger.info("Bulk index:\n " + message);
        // send the message
        ResponseEntity<String> response = sendRESTCall(bulkUri, message);
        logger.info("Bulk index response: " + response.getStatusCode());
        logger.debug("Bulk index response: " + response.getBody());
        
        // TODO: do we need to check the response status of each part of the bulk request?
        
    }
    
    /**
     * Add index mapping if no index is found in the list of known indexes. Can be repeated.
     * @param index
     */
    public void addIndexMappingIfNeeded(String index) {
        synchronized(knownIndexesMap) {
            if (indexMappingTemplate != null && !knownIndexesMap.containsKey(index)) {
                logger.info("Sending mapping for " + index + ": " + indexMappingTemplate);
                try {
                    ResponseEntity<String> response = sendRESTCall(HttpMethod.PUT, esUri + "/" + index, indexMappingTemplate);
                    logger.info(String.format("Bulk index response: %S, %s ", response.getStatusCode().name(), response.getBody()));
                } catch (Exception e) {
                    logger.info("Index " + index + " already exists", e);
                }
                knownIndexesMap.put(index, Boolean.TRUE);
            }
        }
    }
    
    private ResponseEntity<String> sendRESTCall(String url, String query, Object... uriParams) {
        return sendRESTCall(HttpMethod.POST, url, query, uriParams);
    }
    
    /**
     * Send REST query to elasticsearch server
     * 
     * @param query
     * @return
     */
    private ResponseEntity<String> sendRESTCall(HttpMethod method, String url, String query, Object... uriParams) {
        HttpHeaders headers = new HttpHeaders();
        
        // Basic Authentication when username and password are provided
        if (esUsername != null && esPassword != null) {
            headers.set("Authorization",
                    "Basic " + Base64.encodeBase64String((esUsername + ":" + esPassword).getBytes()));
        }
        logger.info(String.format("%s Request: %s", method.name(), url));
        HttpEntity<String> entity = new HttpEntity<String>(query, headers);
        
        // make the REST call
        try {
            return searchTemplate.exchange(url, method, entity, String.class, uriParams);
        } catch (RestClientException rce) {
            logger.error("Error sending elastic search request!", rce);
            throw rce;
        }
    }
    
    public void setSearchUrl(String esUrl) {
        this.esUri = esUrl;
        this.bulkUri = esUrl + "/_bulk";
        this.mGetUri = esUrl + "/_mget";
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
    
    public void setIndexMappingFile(File indexMappingFile) throws IOException {
        if (!indexMappingFile.exists()) {
            if (!indexMappingFile.getName().startsWith("/")) {
                indexMappingFile = new File(getClass().getResource("/" + indexMappingFile.getName()).getFile());
            }
            if (!indexMappingFile.exists())
                throw new IllegalArgumentException("Index mapping file does not exists: " + indexMappingFile.getAbsolutePath());
        }
        this.indexMappingTemplate = FileUtils.readFileToString(indexMappingFile);
    }
}
