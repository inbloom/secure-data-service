package org.slc.sli.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
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

    private Logger log = LoggerFactory.getLogger(MongoEntity.class);

    private final String type;

    /** Called entity id to avoid Spring Data using this as the ID field. */
    private String entityId;
    private Map<String, Object> body;
    private final Map<String, Object> metaData;

    public MongoEntity(String type, Map<String, Object> body) {
        this(type, null, body, null);
    }

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
     * @param uuidGeneratorStrategy
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
     * @param dbObj
     *            DBObject that need to be converted to MongoEntity
     * @return converted MongoEntity from DBObject
     */
    @SuppressWarnings("unchecked")
    public static MongoEntity fromDBObject(DBObject dbObj) {
        String type = (String) dbObj.get("type");

        UUID uuid = (UUID) dbObj.get("_id");
        String id = uuid.toString();

        Map<String, Object> metaData = (Map<String, Object>) dbObj.get("metaData");
        Map<String, Object> body = (Map<String, Object>) dbObj.get("body");

        return new MongoEntity(type, id, body, metaData);
    }

    public static MongoEntity create(String type, Map<String, Object> body) {
        return new MongoEntity(type, body);
    }

    /**
     * Returns the meta data map.
     */
    @Override
    public Map<String, Object> getMetaData() {
        return metaData;
    }
}
