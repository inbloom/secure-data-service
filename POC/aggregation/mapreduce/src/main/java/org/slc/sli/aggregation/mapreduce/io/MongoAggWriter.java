package org.slc.sli.aggregation.mapreduce.io;

import java.io.IOException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.output.MongoRecordWriter;

import org.apache.hadoop.mapreduce.TaskAttemptContext;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * M/R result aggregation records to mongo. The MongoAggWriter takes an EmittableKey used to
 * locate the record to write to, and a BSONObject to write.
 *
 */
public class MongoAggWriter extends MongoRecordWriter<EmittableKey, BSONWritable> {

    private final DBCollection output;

    public MongoAggWriter(DBCollection c, TaskAttemptContext ctx) {
        super(c, ctx);
        this.output = c;
    }

    @Override
    public void write(EmittableKey key, BSONWritable value) throws IOException {
        DBObject k = new BasicDBObject();
        k.putAll(key.toBSON());

        DBObject v = new BasicDBObject();
        v.put("$set", value);

        output.findAndModify(k, v);
    }
}
