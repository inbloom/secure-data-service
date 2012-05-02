package org.slc.sli.ingestion.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Simple queue service that uses Mongo to store the queued work items.
 * @author smelody
 *
 */
@Component
public class MongoQueueService implements QueueService {

    @Autowired
    /** I feel like there's something amiss here, can we just use Repository? */
   // private MongoTemplate batchJobMongoTemplate;

    private String COLLECTION_NAME = "ingestionJobQueue";

    @Override
    public void postItem(Map<Object, Object> item) {


    }

    @Override
    public Map<Object, Object> reserveItem() {
       // Mongo mongo = batchJobMongoTemplate.getMongo();

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<Object, Object>> fetchItems(String workerId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void purgeExpiredItems() {
        // TODO Auto-generated method stub

    }

}
