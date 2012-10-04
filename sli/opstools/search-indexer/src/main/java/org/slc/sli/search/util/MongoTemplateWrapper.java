package org.slc.sli.search.util;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Aggregation class for MongoTemplate and DBCollection to accomplish UT with Mock data
 * 
 * @author tosako
 * 
 */
public class MongoTemplateWrapper {

    private MongoTemplate mongoTemplate;

    /**
     * wrapper for getCollection method
     * 
     * @param collectionName
     * @return
     */
    public DBCollectionWrapper getCollection(String collectionName) {
        return new DBCollectionWrapper(this.mongoTemplate.getCollection(collectionName));
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public class DBCollectionWrapper {
        private DBCollection dBCollection;

        public DBCollectionWrapper(DBCollection dBCollection) {
            this.dBCollection = dBCollection;
        }

        /**
         * wrapper for find method
         * 
         * @param ref
         * @param keys
         * @return
         */
        public DBCursor find(DBObject ref, DBObject keys) {
            return this.dBCollection.find(ref, keys);
        }
    }
}
