package org.slc.sli.aggregation;

import java.io.IOException;

import org.apache.hadoop.mapreduce.TaskAttemptContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.hadoop.output.MongoRecordWriter;

/**
 * M/R result records to mongo.
 *
 * @param <K>
 * @param <V>
 */
public class MongoAggWriter<K, V> extends MongoRecordWriter<K, V> {
    
    private final DBCollection output;
    private final String keyField;
    private final String updateField;
    
    public MongoAggWriter(DBCollection c, String keyField, String updateField, TaskAttemptContext ctx) {
        super(c, ctx);
        this.output = c;
        this.keyField = keyField;
        this.updateField = updateField;
    }
    
    @Override
    public void write(K key, V value) throws IOException {
        output.findAndModify(makeDBKey(key), makeModifier(value));
    }
    
    private DBObject makeDBKey(K key) {
        return new BasicDBObject(keyField, key.toString());
    }
    
    private DBObject makeModifier(V value) {
        return new BasicDBObject("$set", new BasicDBObject(updateField, value.toString()));
    }
    
}
