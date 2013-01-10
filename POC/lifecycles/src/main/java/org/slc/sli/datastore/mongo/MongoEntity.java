package org.slc.sli.datastore.mongo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.slc.sli.model.ModelEntity;
import org.slc.sli.model.ModelLifecycle;


/**
 * Define a Mongo DB Entity which extends the core DatastoreEntity and adds support for Mongo-specific ID and DBObject conversions.
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */

public class MongoEntity extends ModelEntity {

    private static final long serialVersionUID = 5558930138102120258L;

    
    // Attributes
    
    
    // Constructors
    public MongoEntity() {  
        super();
    }
    
    public MongoEntity(String type) {
        super(type);
    }

    public MongoEntity(String type, Map<String, Object> body) {
        super(type, body);
    }

    public MongoEntity(String type, Object id, Map<String, Object> body,  Map<String, Object> metaData, Map<String, String[]> keys, ModelLifecycle lifecycle) {
        super(type, id, body, metaData, keys, lifecycle);
    }

    public MongoEntity(ModelEntity modelEntity) {
        super(modelEntity.getType(), modelEntity.getId(), modelEntity.getBody(), modelEntity.getMetaData(), modelEntity.getKeys(), modelEntity.getLifecycle());
    }

    
    // Methods  
    
    /**
     * @return DBObject that converted from this MongoEntity
     */
    public DBObject toDBObject() {
        
        BasicDBObject dbObj = new BasicDBObject();
        
        dbObj.put("type", type);
        
        UUID uid = null;
        
        if (id == null) {
            uid = UUID.randomUUID();
            id = uid.toString();
        } else {
            uid = UUID.fromString((String)id);
        }
        
        dbObj.put("_id", uid);
        dbObj.put("body", body);
        dbObj.put("metadata", metaData);
        dbObj.put("keys", keys);
        dbObj.put("lifecycle", lifecycle.toMap());
        
        return dbObj;
    }
    
    /**
     * @param dbObj
     *            DBObject that need to be converted to MongoEntity
     * @return converted MongoEntity from DBObject
     */
    @SuppressWarnings("unchecked")
    public static MongoEntity fromDBObject(DBObject dbObj) {
        
        String type = (String) dbObj.get("type");
        
        UUID uuid = (UUID) dbObj.get("_id");
        String id = uuid.toString();
        
        Map<?, ?> map = dbObj.toMap();
        
        Map<String, Object> body = new HashMap<String, Object>();
        if (map.containsKey("body")) {
            body.putAll((Map<String, ?>) map.get("body"));
        }
        
        Map<String, Object> metaData = new HashMap<String, Object>();
        if (map.containsKey("metadata")) {
            metaData.putAll((Map<String, ?>) map.get("metadata"));
        }
        
        Map<String, String[]> keys = null;
        Map<String, Object> keysMap = (Map<String, Object>)map.get("keys");
        if (keysMap != null) {
            keys = convertDBMap(keysMap);
        }
        
        Map lifecycleMap = (Map<String, Object>)map.get("lifecycle");
        ModelLifecycle lifecycle = new ModelLifecycle(lifecycleMap);
        
        return new MongoEntity(type, id, body, metaData, keys, lifecycle);
    }
    
    private static Map<String, String[]> convertDBMap(Map<String, Object> dbMap) {
        Map<String, String[]> map = new HashMap<String, String[]>();

        for (String key : dbMap.keySet()) {
            Object value = dbMap.get(key);
            if (value instanceof BasicDBList) {
                BasicDBList list = (BasicDBList)value;
                String[] array = new String[list.size()];
                int index = 0;
                for (Object item : list) {
                    array[index] = item.toString();
                    index++;
                }
                map.put(key, array);
            }
        }

        return map;
    }
    
}
