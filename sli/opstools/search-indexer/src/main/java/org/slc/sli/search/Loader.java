package org.slc.sli.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slc.sli.dal.TenantContext;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class Loader {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    static final String MAPPING_TEMPLATE = "{\"%s\":{\"type\" : \"object\", \"properties\" : {\"metaData\": {\"properties\" : {\"edOrgs\": {\"type\" : \"string\", \"index\" : \"not_analyzed\"}, \"teacherContext\": {\"type\" : \"string\", \"index\" : \"not_analyzed\"}, \"isOrphaned\": {\"type\" : \"string\", \"index\" : \"not_analyzed\"}, \"createdBy\": {\"type\" : \"string\", \"index\" : \"not_analyzed\"}}}}}}";
    
    @Autowired
    MongoTemplate template;
    
    @Autowired
    @Qualifier("entityEncryption")
    EntityEncryption encrypt;
    
    private Client client;
    
    private String esUri;

    private RestTemplate searchTemplate;

    private String esUsername;

    private String esPassword;
    
    public void init() {
        client = getClient();
    }
    
    public void destroy() {
        client.close();
    }
    
    public Loader() {
        
        searchTemplate = new RestTemplate();
    }
    
    
    /**
     * Load partial collections from mongo to es
     * @throws IOException 
     */
    public void load() throws IOException {
        
        index("course", Arrays.asList("_id", "body.courseTitle", "body.subjectArea", "metaData", "type"), false);
        index("student", Arrays.asList("_id", "body.name.firstName", "body.name.middleName", "body.name.lastSurname", "body.otherName.firstName", "body.name.lastSurname", "metaData", "type"), true);
        index("section", Arrays.asList("_id", "body.uniqueSectionCode", "metaData", "type"), false);
        index("educationOrganization", Arrays.asList("_id", "body.nameOfInstitution", "body.shortNameOfInstitution", "metaData", "type"), false);
        index("staff", Arrays.asList("_id", "body.name.firstName", "body.name.middleName", "body.name.lastSurname", "body.otherName.firstName", "body.name.lastSurname", "metaData", "type"), false);  
    }
    
    /**
     * bulk load to ES from mongo
     * @param collection
     * @param fields
     * @throws IOException 
     */
    private void index(String collection, List<String> fields, boolean decrypt) throws IOException {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        Query query = new Query();
        Field queryFields = query.fields();
        for (String field: fields) {
            queryFields.include(field);
        }
        logger.info("retrieving " + collection);
        List<Entity> results = template.find(query, Entity.class, collection);
        logger.info("indexing " + collection);
        IndexRequestBuilder irb;
        Collection<IndexRequestBuilder> indexRequests = new ArrayList<IndexRequestBuilder>();
        int count = 0;
        
        // for each entity extracted from mongo, create an index request, add it to bulk request
        for (Entity item : results) {
            
            irb = buildIndexRequest(collection, item);
            indexRequests.add(irb);
            
            // when we reach a set number of index requests, execute the bulk request
            if (count ++ >= 10) {
                count = 0;
                executeBulkHttp(indexRequests);
                indexRequests.clear();
            }
        }
        executeBulkHttp(indexRequests);
    }
    
    /**
     * Builds an elastic search index request from an entity
     * @param entityJson
     * @return
     */
    private IndexRequestBuilder buildIndexRequest(String collection, Entity item) {
        
        Map<String, Object> entItem;
        Set<String> tenants = new HashSet<String>();
        String tenant;
        entItem = new HashMap<String, Object>();
        
        // decrypt here?
        
        entItem.put("body", item.getBody());
        entItem.put("metaData", item.getMetaData());
        tenant = ((String)item.getMetaData().get("tenantId")).toLowerCase();
        if (!tenants.contains(tenant)) {
            tenants.add(tenant);
        }
        
        // create irb
        IndexRequestBuilder irb = client.prepareIndex(tenant, collection, item.getEntityId()).setSource(entItem);
        
        return irb;
    }
    
    
    private void executeBulkHttp(Collection<IndexRequestBuilder> indexRequests) {
        
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
        System.out.println(message);
        HttpEntity<String> response = sendRESTQuery(message);
        System.out.println(response);
    }
    
    
    /**
     * Send REST query to elasticsearch server
     *
     * @param query
     * @return
     */
    private HttpEntity<String> sendRESTQuery(String query) {

        HttpMethod method = HttpMethod.POST;
        HttpHeaders headers = new HttpHeaders();

        // Basic Authentication when username and password are provided
        /*
        if (esUsername != null && esPassword != null) {
            headers.set("Authorization",
                    "Basic " + Base64.encodeBase64String((esUsername + ":" + esPassword).getBytes()));
        }
        */
        HttpEntity<String> entity = new HttpEntity<String>(query, headers);

        // make the REST call
        //esUri = "http://localhost:9200/" + TenantContext.getTenantId().toLowerCase() + "_bulk";
        esUri = "http://localhost:9200/_bulk";
        
        try {
            return searchTemplate.exchange(
                    esUri, method, entity, String.class /*, TenantContext.getTenantId().toLowerCase()*/);
        } catch (RestClientException rce) {
            logger.error("Error sending elastic search request!", rce);
            throw rce;

        }
    }

    
    private Client getClient() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name","elasticsearch") 
                //.put("client.transport.sniff", true)
                .put("client.transport.nodes_sampler_interval", "30s")
                //.put("transport.tcp.ssl", true)
                //.put("transport.tcp.ssl.keystore", "/Users/dwu/elasticsearch-java/es-loader/esnode3.jks")
                //.put("transport.tcp.ssl.keystore_password", "esnode3")
                //.put("transport.tcp.ssl.truststore", "/Users/dwu/elasticsearch-java/es-loader/esnode3.jks")
                //.put("transport.tcp.ssl.truststore_password", "esnode3")
                .put("client.transport.sniff", false)
                .build();
                //.put("client.transport.ignore_cluster_name", true).build();
        //return new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
        return new TransportClient();
    }
}