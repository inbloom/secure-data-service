package org.slc.sli.aggregation.mapreduce;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.hadoop.output.MongoRecordWriter;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

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

    private DBObject makeModifier(V obj) {

        // TODO - need to be able to configure the contents of a MapWritable
        if (obj instanceof ArrayWritable) {

            ArrayWritable a = (ArrayWritable) obj;
            LongWritable[] input = (LongWritable[]) a.get();
            long[] rval = new long[input.length];

            int i = 0;
            for (LongWritable entry : input) {
                rval[i++] = entry.get();
            }

            return new BasicDBObject("$set", new BasicDBObject(updateField, rval));
        }

        if (obj instanceof MapWritable) {
            MapWritable input = (MapWritable) obj;
            Map<String, Long> rval = new TreeMap<String, Long>();

            for (Map.Entry<Writable, Writable> entry : input.entrySet()) {
                Text key = (Text) entry.getKey();
                LongWritable value = (LongWritable) entry.getValue();

                rval.put(key.toString(), value.get());
            }
            return new BasicDBObject("$set", new BasicDBObject(updateField, rval));
        }

        return new BasicDBObject("$set", new BasicDBObject(updateField, obj.toString()));
    }

}
