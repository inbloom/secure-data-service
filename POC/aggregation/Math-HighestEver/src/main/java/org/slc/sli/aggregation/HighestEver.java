package org.slc.sli.aggregation;

import java.io.InputStream;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.codehaus.jackson.map.ObjectMapper;

import org.slc.sli.aggregation.mapreduce.io.MongoAggFormatter;
import org.slc.sli.aggregation.mapreduce.io.MongoIdInputFormat;
import org.slc.sli.aggregation.mapreduce.map.ConfigurableMapperBuilder;
import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;


/**
 * Aggregate the highest ever score
 */
public class HighestEver extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new HighestEver(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {

        String assmtIDCode = args.length == 0 ? "Grade 7 2011 State Math" : args[0];

        Mongo m = new Mongo("localhost");
        DB db = m.getDB("sli");
        ConfigurableMapperBuilder builder = new ConfigurableMapperBuilder(db);
        Configuration conf = getConf();
        conf.set(MongoAggFormatter.UPDATE_FIELD, "calculatedValues.assessments." + assmtIDCode + ".HighestEver.ScaleScore");
        ObjectMapper om = new ObjectMapper();
        InputStream s = getClass().getClassLoader().getResourceAsStream("HighestEverForAssessment.json");
        @SuppressWarnings("unchecked")
        Map<String, Object> mapDefn = om.readValue(s, Map.class);

        // we must specify an input and output collection to calculate splits.
        MongoURI input = new MongoURI("mongodb://localhost/sli.studentAssessmentAssociation");
        MongoURI output = new MongoURI("mongodb://localhost/sli.student");
        MongoConfigUtil.setInputURI(getConf(), input);
        MongoConfigUtil.setOutputURI(getConf(), output);

        Job job = builder.makeJob(getConf(), mapDefn);
        job.setCombinerClass(Highest.class);
        job.setReducerClass(Highest.class);

        job.setInputFormatClass(MongoIdInputFormat.class);
        job.setOutputKeyClass(TenantAndIdEmittableKey.class);
        job.setOutputValueClass(DoubleWritable.class);
        job.setOutputFormatClass(MongoAggFormatter.class);

        boolean success = job.waitForCompletion(true);

        return success ? 0 : 1;
    }
}
