package org.slc.sli.domain;

import com.mongodb.BasicDBObject;

import java.util.List;
import java.util.Map;

/**
 * Adds a shard key to a BasicDBObject.
 * <p/>
 * User: wscott
 */
public class ShardKeyMongoEntityDecorator implements MongoEntityDecorator {
    public static final String FIELD_SHARDKEY = "shardkey";
    public static final String FIELD_TYPE = "type";

    private Map<String, List<String>> shardDefinitions;

    public void setShardDefinitions(Map<String, List<String>> shardDefinitions) {
        this.shardDefinitions = shardDefinitions;
    }

    public Map<String, List<String>> getShardDefinitions() {
        return shardDefinitions;
    }

    @Override
    public void decorate(BasicDBObject dbObj) {
        if (dbObj.get(FIELD_TYPE) == null) {
            return;
        }

        List<String> typeShardFields = shardDefinitions.get(dbObj.get("type"));

        if (typeShardFields == null) {
            return;
        }

        StringBuffer shardKey = new StringBuffer();
        for (String shardField : typeShardFields) {
            shardKey.append(getField(dbObj, shardField));
        }
        dbObj.put(FIELD_SHARDKEY, shardKey);
    }

    protected String getField(BasicDBObject dbObject, String key) {
        String fieldValue = "";
        Object crnt = dbObject;

        for (String field : key.split("\\.")) {
            if (crnt == null) {
                break;
            }
            crnt = ((BasicDBObject) crnt).get(field);
        }

        if (crnt != null && !(crnt instanceof BasicDBObject)) {
            fieldValue = crnt.toString();
        }

        return fieldValue;
    }
}
