package org.slc.sli.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.BasicBSONObject;

import org.slc.sli.dal.encrypt.EntityEncryption;

/**
 * Mongodb specific implementation of Entity Interface with basic conversion method
 * for convert from and to DBObject
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */
public class MongoEntity implements Entity, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3661562228274704762L;
    
    private final String type;
    
    /** Called entity id to avoid Spring Data using this as the ID field. */
    private String entityId;
    private Map<String, Object> body;
    private final Map<String, Object> metaData;
    
    public MongoEntity(String type, Map<String, Object> body) {
        this.type = type;
        this.body = body;
        this.metaData = new BasicBSONObject();
        this.entityId = null;
    }
    
    public MongoEntity(String type, String id, Map<String, Object> body, Map<String, Object> metaData) {
        if (body == null) {
            this.body = new BasicBSONObject();
        } else {
            this.body = body;
        }
        if (metaData == null) {
            this.metaData = new BasicBSONObject();
        } else {
            this.metaData = metaData;
        }
        this.type = type;
        entityId = id;
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
     * This method enables encryption of the entity without exposing the internals to mutation via a
     * setBody() method.
     * 
     * @param crypt
     *            The EntityEncryptor to sue
     */
    public void encrypt(EntityEncryption crypt) {
        this.body = crypt.encrypt(getType(), body);
    }
    
    /**
     * This method enables decryption of the entity without exposing the internals to mutation via a
     * setBody() method.
     * 
     * @param crypt
     *            The EntityEncryptor to sue
     */
    public void decrypt(EntityEncryption crypt) {
        this.body = crypt.decrypt(getType(), body);
    }
    
    /**
     * @return DBObject that converted from this MongoEntity
     */
    public DBObject toDBObject() {
        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", type);
        
        UUID uid = null;
        
        if (entityId == null) {
            uid = UUID.randomUUID();
            entityId = uid.toString();
        } else {
            uid = UUID.fromString(entityId);
        }
        
        dbObj.put("_id", uid);
        dbObj.put("body", body);
        dbObj.put("metaData", metaData);
        
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
        
        BasicBSONObject metaData = (BasicBSONObject) dbObj.get("metaData");
        BasicBSONObject body = (BasicBSONObject) dbObj.get("body");

        return new MongoEntity(type, id, body, metaData);
    }
    
    public static MongoEntity create(String type, Map<String, Object> body) {
        return new MongoEntity(type, body);
    }
    
    /**
     * Returns the meta data map.
     */
    public Map<String, Object> getMetaData() {
        return metaData;
    }
}
