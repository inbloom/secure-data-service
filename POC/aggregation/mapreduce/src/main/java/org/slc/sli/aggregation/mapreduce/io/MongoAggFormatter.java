package org.slc.sli.aggregation.mapreduce.io;

import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.bson.BSONObject;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * output mongo reduce results to a collection.
 *
 * @param <K>
 *            EmittableKey used to lookup the record to write.
 */
public class MongoAggFormatter<K extends EmittableKey<K>> extends MongoOutputFormat<K, BSONObject> {

    public static final String KEY_FIELD = "mongo.output.key.field";
    public static final String UPDATE_FIELD = "mongo.output.update.field";

    @Override
    public RecordWriter<K, BSONObject> getRecordWriter(TaskAttemptContext context) {
        Configuration config = context.getConfiguration();
        return new MongoAggWriter<K>(MongoConfigUtil.getOutputCollection(config), context);
    }

}
