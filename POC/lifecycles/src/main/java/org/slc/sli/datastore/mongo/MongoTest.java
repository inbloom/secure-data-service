package org.slc.sli.datastore.mongo;

import org.slc.sli.model.ModelEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MongoTest {

    // Logging
    private static final Logger log = LoggerFactory.getLogger(MongoTest.class);
    
    @Value("${datastore.collection.test}")
    private String testCollection;

    public String getTestCollection() {
        return testCollection;
    }
    
    public static void main( String[] args ) {
        
        log.info("Starting Mongo Test...");
        
        try {
            
            ApplicationContext ctx = new GenericXmlApplicationContext("applicationContext.xml");
            
            SimpleMongoRepository mongoRepository = (SimpleMongoRepository)ctx.getBean("mongoRepository");
            
            MongoTest mongoTest = (MongoTest)ctx.getBean(MongoTest.class);
            String principle = "rbloh";
            String collectionName = mongoTest.getTestCollection();
            
            log.info("Collection: " + collectionName);
            
            // Purge
            mongoRepository.purge(principle, collectionName);
            
            log.info(collectionName + " Size: " + mongoRepository.count(collectionName));
            
            // Create
            TestEntity testEntity = new TestEntity("Robert", "Bloh", 48);        
            mongoRepository.create(principle, collectionName, testEntity);
            
            log.info("Created: " + testEntity.toString());

            // Update
            testEntity.getBody().clear();
            testEntity.getBody().put("newAttribute", "newValue");
            mongoRepository.update(principle, collectionName, testEntity);
            
            log.info("Updated: " + testEntity.toString());

            // Find
            ModelEntity findEntity = mongoRepository.findById(collectionName, testEntity.getId().toString());
            
            log.info("Find: " + findEntity.toString());
            
            // Delete
            mongoRepository.delete(principle, collectionName, testEntity);
            
            log.info("Deleted: " + testEntity.toString());

            log.info(collectionName + " Size: " + mongoRepository.count(collectionName));
            
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
}
