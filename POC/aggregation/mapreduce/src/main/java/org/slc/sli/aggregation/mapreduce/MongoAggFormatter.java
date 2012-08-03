package org.slc.sli.aggregation.mapreduce;

import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

/**
 * output mongo reduce results a collection.
 *
 * @param <K> key
 * @param <V> value
 */
public class MongoAggFormatter<V> extends MongoOutputFormat<TenantAndID, V> {

    public static final String KEY_FIELD = "mongo.output.key.field";
    public static final String UPDATE_FIELD = "mongo.output.update.field";

    @Override
    public RecordWriter<TenantAndID, V> getRecordWriter(TaskAttemptContext context) {
        Configuration config = context.getConfiguration();
        return new MongoAggWriter<V>(MongoConfigUtil.getOutputCollection(config), config.get(KEY_FIELD, "_id"), config.get(UPDATE_FIELD), context);
    }

}
