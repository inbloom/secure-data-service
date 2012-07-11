package org.slc.sli.aggregation;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;

public class MongoAggFormatter<K, V> extends MongoOutputFormat<K, V> {

    public final static String KEY_FIELD = "mongo.output.key.field";
    public final static String UPDATE_FIELD = "mongo.output.update.field";
    
    @Override
    public RecordWriter<K, V> getRecordWriter(TaskAttemptContext context) {
        Configuration config = context.getConfiguration();
        return new MongoAggWriter<K, V>(MongoConfigUtil.getOutputCollection(config), config.get(KEY_FIELD, "_id"), config.get(UPDATE_FIELD), context);
    }
    
}
