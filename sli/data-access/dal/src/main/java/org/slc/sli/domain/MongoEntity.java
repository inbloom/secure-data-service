package org.slc.sli.domain;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.BSON;
import org.bson.BasicBSONObject;
import org.bson.types.Binary;

public class MongoEntity implements Entity {
    
    final String type;
    final String id;
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
        this.id = id;
        this.body = body;
        this.metaData = metaData;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public Map<String, Object> getBody() {
        return body;
    }
    
    public DBObject toDBObject() {
        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", this.type);
        
        Binary binaryId = null;
        UUID uid = null;
        
        if (this.id == null) {
            uid = UUID.randomUUID();
            binaryId = convertUUIDtoBinary(uid);
            
        } else {
            uid = UUID.fromString(id);
            binaryId = convertUUIDtoBinary(uid);
            
        }
        
        dbObj.put("_id", binaryId);
        dbObj.put("body", this.body);
        dbObj.put("metadata", this.metaData);
        return dbObj;
    }
    
    @SuppressWarnings("unchecked")
    public static MongoEntity fromDBObject(DBObject dbObj) {
        String type = (String) dbObj.get("type");
        String id = getIdFromObject(dbObj);
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
    
    /**
     * Fetches the ID from the database object. Assumes that the ID was stored as a Binary.
     * 
     * @param dbObj
     * @return
     */
    private static String getIdFromObject(DBObject dbObj) {
        
        Binary binary = (Binary) dbObj.get("_id");
        byte[] arr = binary.getData();
        ByteBuffer buff = ByteBuffer.wrap(arr);
        
        long msb = buff.getLong(0);
        long lsb = buff.getLong(8);
        UUID uid = new UUID(msb, lsb);
        
        return uid.toString();
        
    }
    
    /**
     * Converts the given UUID into a Binary object that represents the underlying byte array in
     * Mongo. This is recommended by the mongo docs
     * to store UUIDs .
     * 
     * @param uid
     *            The object's UUID
     * @return a Binary representation of the given UUID.
     */
    private static Binary convertUUIDtoBinary(UUID uid) {
        ByteBuffer buff = ByteBuffer.allocate(16);
        buff.putLong(uid.getMostSignificantBits());
        buff.putLong(uid.getLeastSignificantBits());
        byte[] arr = buff.array();
        
        Binary binary = new Binary(BSON.B_UUID, arr);
        
        return binary;
    }
    
}
