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

    private final String type;

    /** Called entity id to avoid Spring Data using this as the ID field. */
    private String entityId;
    private final Map<String, Object> body;
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
        dbObj.put("metadata", metaData);

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

    public static MongoEntity create(String type, Map<String, Object> body) {
        return new MongoEntity(type, body);
    }
}
