package org.slc.sli.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.BasicBSONObject;

/**
 * Mongodb specific implementation of Entity Interface with basic conversion method
 * for convert from and to DBObject
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */
public class MongoEntity implements Entity {
    
    final String type;
    
    /** Called entity id to avoid Spring Data using this as the ID field. */
    String entityId;
    final Map<String, Object> body;
    final Map<String, Object> metaData;
    
    public MongoEntity(String type, String id, Map<String, Object> body, Map<String, Object> metaData) {
        if (body == null) {
            body = new BasicBSONObject();
        }
        if (metaData == null) {
            metaData = new BasicBSONObject();
        }
        this.type = type;
        this.entityId = id;
        this.body = body;
        this.metaData = metaData;
    }
    
    @Override
    public String getEntityId() {
        return entityId;
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public Map<String, Object> getBody() {
        return body;
    }
    
    /**
     * @return DBObject that converted from this MongoEntity
     */
    public DBObject toDBObject() {
        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", this.type);
        
        UUID uid = null;
        
        if (this.entityId == null) {
            uid = UUID.randomUUID();
            this.entityId = uid.toString();
        } else {
            uid = UUID.fromString(entityId);
        }
        
        dbObj.put("_id", uid);
        dbObj.put("body", this.body);
        dbObj.put("metadata", this.metaData);
        
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
        return new MongoEntity(type, id, body, metaData);
    }
    
}
