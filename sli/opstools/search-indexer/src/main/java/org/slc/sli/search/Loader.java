package org.slc.sli.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class Loader {
    private final Logger logger = LoggerFactory.getLogger(getClass());
        
    @Autowired
    MongoTemplate template;
    
    @Autowired
    @Qualifier("entityEncryption")
    EntityEncryption encrypt;
    
    @Autowired
    Indexer indexer;

    public Loader() {  
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
        
        // TODO: get entities from file, not mongo
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
            
            irb = indexer.buildIndexRequest(collection, item);
            indexRequests.add(irb);
            
            // when we reach a set number of index requests, execute the bulk request
            if (count ++ >= 100) {
                count = 0;
                indexer.executeBulkHttp(indexRequests);
                indexRequests.clear();
            }
        }
        indexer.executeBulkHttp(indexRequests);
    }
    

}