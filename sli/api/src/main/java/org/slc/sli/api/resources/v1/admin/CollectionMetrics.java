package org.slc.sli.api.resources.v1.admin;

import java.util.HashMap;

/**
 * Captures the the number of records and estimated total size of all rows in a collection.
 */
class CollectionMetric {
    protected long entityCount;
    protected double size;
    
    CollectionMetric(long recordCount, double size) {
        entityCount = recordCount;
        this.size = size;
    }
    
    public long getRecordCount() {
        return entityCount;
    }
    
    public double getSize() {
        return size;
    }
    
    public void add(CollectionMetric c) {
        entityCount += c.entityCount;
        size += c.size;
    }
}

/**
 * Captures metrics for all collections in the mongo database, organized by collection name.
 */
public class CollectionMetrics extends HashMap<String, CollectionMetric> {
    
    private static final long serialVersionUID = -5616329370579405926L;
    private CollectionMetric allCollections = new CollectionMetric(0, 0.0);
    
    public CollectionMetrics aggregate(final String collectionName, long recordCount, double size) {
        
        if (containsKey(collectionName)) {
            CollectionMetric c = get(collectionName);
            c.entityCount += recordCount;
            c.size += size;
        } else {
            put(collectionName, new CollectionMetric(recordCount, size));
        }
        
        allCollections.entityCount += recordCount;
        allCollections.size += size;
        
        return this;
    }
    
    public CollectionMetric getTotals() {
        return allCollections;
    }
    
}
