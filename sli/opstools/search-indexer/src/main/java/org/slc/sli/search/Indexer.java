package org.slc.sli.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Indexer is responsible for building elastic search index requests and
 * sending them to the elastic search server for processing.
 * 
 * @author dwu
 *
 */
public class Indexer {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    static final String MAPPING_TEMPLATE = "{\"%s\":{\"type\" : \"object\", \"properties\" : {\"metaData\": {\"properties\" : {\"edOrgs\": {\"type\" : \"string\", \"index\" : \"not_analyzed\"}, \"teacherContext\": {\"type\" : \"string\", \"index\" : \"not_analyzed\"}, \"isOrphaned\": {\"type\" : \"string\", \"index\" : \"not_analyzed\"}, \"createdBy\": {\"type\" : \"string\", \"index\" : \"not_analyzed\"}}}}}}";

    private Client client;

    private String esUri;

    private RestTemplate searchTemplate;

    private String esUsername;

    private String esPassword;
    
    Collection<IndexRequestBuilder> indexRequests = new ArrayList<IndexRequestBuilder>();
    int count = 0;
    
    
    public void init() {
        client = getClient();
        searchTemplate = new RestTemplate();
    }

    public void destroy() {
        client.close();
    }
    
    public void index(String entityType, Entity entity) {
        
        IndexRequestBuilder irb = buildIndexRequest(entityType, entity);
        indexRequests.add(irb);
        
        // when we reach a set number of index requests, execute the bulk request
        if (count ++ >= 1000) {
            count = 0;
            executeBulkHttp(indexRequests);
            indexRequests.clear();
        }
    }
    
    public void index(String entityType, String entityId, String tenant, String entity) {
        IndexRequestBuilder irb = buildIndexRequest(entityType, entityId, tenant, entity);
        indexRequests.add(irb);
        
        // when we reach a set number of index requests, execute the bulk request
        if (count ++ >= 1000) {
            count = 0;
            executeBulkHttp(indexRequests);
            indexRequests.clear();
        }
    }
    
    public void flush() {
        executeBulkHttp(indexRequests);
    }
    
    /**
     * Builds a single elastic search index request from an entity
     * @param entityJson
     * @return
     */
    public IndexRequestBuilder buildIndexRequest(String entityType, Entity entity) {
        
        Map<String, Object> entItem;
        Set<String> tenants = new HashSet<String>();
        String tenant;
        entItem = new HashMap<String, Object>();
        
        // decrypt here?
        
        entItem.put("body", entity.getBody());
        entItem.put("metaData", entity.getMetaData());
        tenant = ((String)entity.getMetaData().get("tenantId")).toLowerCase();
        
        // TODO: do we need to keep track of tenants?
        if (!tenants.contains(tenant)) {
            tenants.add(tenant);
        }
        
        // create irb
        IndexRequestBuilder irb = client.prepareIndex(tenant, entityType, entity.getEntityId()).setSource(entItem);
        
        return irb;
    }
    
    public IndexRequestBuilder buildIndexRequest(String entityType, String entityId, String tenant, String entity) {
        
        return client.prepareIndex(tenant.toLowerCase(), entityType, entityId).setSource(entity);
    }

    /**
     * Takes a collection of index requests, builds a bulk http message to send to elastic search
     * 
     * @param indexRequests
     */
    public void executeBulkHttp(Collection<IndexRequestBuilder> indexRequests) {
        
        // create bulk http message
        String message = "";        
        
        /* format of message data
        { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
        { "field1" : "value1" }
        */
        Map<String, Object> indexMetaData = new HashMap<String, Object>();
        Map<String, Object> indexMetaDataBody = new HashMap<String, Object>();
        
        
        // add each index request to the message
        for (IndexRequestBuilder irb : indexRequests) {
            
            indexMetaDataBody.put("_index", irb.request().index());
            indexMetaDataBody.put("_type", irb.request().type());
            indexMetaDataBody.put("_id", irb.request().id());
            indexMetaData.put("index", indexMetaDataBody);
            
            ObjectMapper mapper = new ObjectMapper();
            String indexStr = null;
            try {
                indexStr = mapper.writeValueAsString(indexMetaData);
            } catch (Exception e) {
                logger.error("Error building index message", e);
                continue;
            }
            message += indexStr;
            message += "\n";
            message += irb.request().source().toUtf8();
            message += "\n";
        }
        
        // send the message
        logger.info("Sending bulk index request: " + message);
        HttpEntity<String> response = sendRESTCall(message);
        logger.info("Bulk index response: " + response.getBody() + "\n");
        
        // TODO: do we need to check the response status of each part of the bulk request?
        
    }
    
    
    /**
     * Send REST query to elasticsearch server
     *
     * @param query
     * @return
     */
    private HttpEntity<String> sendRESTCall(String query) {

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
            return searchTemplate.exchange(
                    esUri, method, entity, String.class /*, TenantContext.getTenantId().toLowerCase()*/);
        } catch (RestClientException rce) {
            logger.error("Error sending elastic search request!", rce);
            throw rce;
        }
    }

    
    private Client getClient() {
        return new TransportClient();
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
}
