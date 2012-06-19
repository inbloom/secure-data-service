package org.slc.sli.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import org.bson.BasicBSONObject;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Mongodb specific implementation of Entity Interface with basic conversion method
 * for convert from and to DBObject
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */
public class MongoEntity implements Entity, Serializable {
    
    private static final long serialVersionUID = -3661562228274704762L;
    
    private Logger log = LoggerFactory.getLogger(MongoEntity.class);
    
    private final String type;
    
    /** Called entity id to avoid Spring Data using this as the ID field. */
    private String entityId;
    private Map<String, Object> body;
    private final Map<String, Object> metaData;
    
    /**
     * Default constructor for the MongoEntity class.
     * 
     * @param type
     *            Mongo Entity type.
     * @param body
     *            Body of Mongo Entity.
     */
    public MongoEntity(String type, Map<String, Object> body) {
        this(type, null, body, null);
    }
    
    /**
     * Specify the type, id, body, and metadata for the Mongo Entity using this constructor.
     * 
     * @param type
     *            Mongo Entity type.
     * @param id
     *            Mongo Entity id.
     * @param body
     *            Body of Mongo Entity.
     * @param metaData
     *            Metadata of Mongo Entity.
     */
    public MongoEntity(String type, String id, Map<String, Object> body, Map<String, Object> metaData) {
        this.type = type;
        this.entityId = id;
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
     * Converts Mongo Entity to db object (for writing to mongo) using the specified UUID
     * strategy for creating the Mongo Entity Id.
     * 
     * @param uuidGeneratorStrategy
     *            UUID generator strategy (type 1, 2, 3, 4).
     * @return DBObject that converted from this MongoEntity
     */
    public DBObject toDBObject(UUIDGeneratorStrategy uuidGeneratorStrategy) {
        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", type);
        
        final String uid;
        
        if (entityId == null) {
            if (uuidGeneratorStrategy != null) {
                uid = uuidGeneratorStrategy.randomUUID();
            } else {
                log.warn("Generating Type 4 UUID by default because the UUID generator strategy is null.  This will cause issues if this value is being used in a Mongo indexed field (like _id)");
                uid = UUID.randomUUID().toString();
            }
            entityId = uid.toString();
        } else {
            uid = entityId;
        }
        
        dbObj.put("_id", uid);
        dbObj.put("body", body);
        dbObj.put("metaData", metaData);
        
        return dbObj;
    }
    
    /**
     * Convert the specified db object to a Mongo Entity.
     * 
     * @param dbObj
     *            DBObject that need to be converted to MongoEntity
     * @return converted MongoEntity from DBObject
     */
    @SuppressWarnings("unchecked")
    public static MongoEntity fromDBObject(DBObject dbObj) {
        String type = (String) dbObj.get("type");
        
        String id = null;
        Object mongoId = dbObj.get("_id");
        if (mongoId instanceof UUID) {
            UUID mongoUuidId = (UUID) mongoId;
            id = mongoUuidId.toString();
        } else {
            id = (String) mongoId;
        }
        
        Map<String, Object> metaData = (Map<String, Object>) dbObj.get("metaData");
        Map<String, Object> body = (Map<String, Object>) dbObj.get("body");
        
        return new MongoEntity(type, id, body, metaData);
    }
    
    /**
     * Create and return a Mongo Entity.
     * 
     * @param type
     *            Mongo Entity type.
     * @param body
     *            Body of Mongo Entity.
     * @return Newly created Mongo Entity.
     */
    public static MongoEntity create(String type, Map<String, Object> body) {
        return new MongoEntity(type, body);
    }
    
    @Override
    public Map<String, Object> getMetaData() {
        return metaData;
    }
}
