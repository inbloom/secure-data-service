package org.mongo.performance;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataAccessWrapper {
    
    MongoTemplate mongoTemplate;
    
    public void setTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MongoTemplate getTemplate() {
        return mongoTemplate;
    }
    
}
