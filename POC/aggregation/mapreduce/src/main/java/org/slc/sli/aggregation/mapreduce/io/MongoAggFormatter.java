package org.slc.sli.aggregation.mapreduce.io;

import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.RecordWriter;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * output mongo reduce results to a collection.
 *
 */
public class MongoAggFormatter extends MongoOutputFormat<EmittableKey, BSONWritable> {

    public static final String KEY_FIELD = "mongo.output.key.field";
    public static final String UPDATE_FIELD = "mongo.output.update.field";

    @Override
    public RecordWriter<EmittableKey, BSONWritable> getRecordWriter(
        org.apache.hadoop.mapreduce.TaskAttemptContext context) {
        Configuration config = context.getConfiguration();
        return new MongoAggWriter(MongoConfigUtil.getOutputCollection(config), context);
    }
}
