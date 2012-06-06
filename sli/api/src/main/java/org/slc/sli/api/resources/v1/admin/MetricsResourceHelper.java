package org.slc.sli.api.resources.v1.admin;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.QueryBuilder;

import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

public class MetricsResourceHelper {
    
    public static ObjectMapper jsonMapper = new ObjectMapper();
    
    /**
     * Collect metrics for a collection, qualified by the field with name 'qualifier'
     * and value 'id'.
     * 
     * @param qualifier
     * @param id
     * @param coll
     * @return
     */
    public static CollectionMetric gatherCollectionMetrics(final String qualifier, final String id, DBCollection coll) {
        CollectionMetric metrics = new CollectionMetric(0, 0.0);
        CommandResult stats = coll.getStats();
        
        long count = coll.count(QueryBuilder.start(qualifier).is(id).get());
        if (count > 0) {
            double size = (stats.getDouble("avgObjSize") + (stats.getDouble("totalIndexSize") / count)) * count;
            metrics.entityCount = count;
            metrics.size = size;
        }
        return metrics;
    }
    
    /**
     * Collect metrics for all collections, qualified by the field with name 'qualifier'
     * and value 'id'.
     * 
     * @param repo
     * @param fieldKey
     * @param fieldValue
     * @return
     */
    public static CollectionMetrics getAllCollectionMetrics(Repository<Entity> repo, final String fieldKey,
            final String fieldValue) {
        
        CollectionMetrics metrics = new CollectionMetrics();
        
        for (DBCollection coll : repo.getCollections(false)) {
            
            CollectionMetric collMetrics = gatherCollectionMetrics(fieldKey, fieldValue, coll);
            
            if (collMetrics.entityCount > 0) {
                metrics.aggregate(coll.getName(), collMetrics.entityCount, collMetrics.size);
            }
        }
        
        return metrics;
    }
}