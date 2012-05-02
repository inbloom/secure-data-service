package org.slc.sli.ingestion.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Simple queue service that uses Mongo to store the queued work items.
 * @author smelody
 *
 */
@Component
public class MongoQueueService implements QueueService {

    @Autowired
    private Repository<Entity> repo;

    private String COLLECTION_NAME = "ingestionJobQueue";

    @Override
    public void postItem(Map<Object, Object> item) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<Object, Object> reserveItem() {
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
