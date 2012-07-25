package org.slc.sli.aggregation;

import com.mongodb.MongoURI;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.slc.sli.aggregation.mapreduce.MongoAggFormatter;


/**
 * Aggregate the state math assessment proficiency for a school.
 */
public class SchoolProficiency extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new SchoolProficiency(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        String assmtIDCode = "Grade 7 2011 State Math";

        MongoURI input = new MongoURI("mongodb://localhost/sli.student");
        MongoURI output = new MongoURI("mongodb://localhost/sli.educationOrganization");

        Configuration conf = getConf();

        // track the assessment ID
        conf.set("AssessmentIdCode", assmtIDCode);

        // The value to aggregate
        conf.set(SchoolProficiencyMapper.SCORE_TYPE, "aggregations.assessments." + assmtIDCode + ".HighestEver.Aggregate");

        // Where to store the resulting aggregate.
        conf.set(MongoAggFormatter.UPDATE_FIELD, "aggregations.assessments." + assmtIDCode + ".HighestEver.Aggregate");

        MongoConfigUtil.setInputURI(conf, input);
        MongoConfigUtil.setOutputURI(conf, output);

        MongoConfigUtil.setSplitSize(conf, 2);
        MongoConfigUtil.setCreateInputSplits(conf,  true);

        Job job = new Job(conf, "SchoolProficiency");
        job.setJarByClass(getClass());

        job.setInputFormatClass(MongoInputFormat.class);

        job.setMapperClass(SchoolProficiencyMapper.class);
        job.setReducerClass(SchoolProficiencyReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputFormatClass(MongoAggFormatter.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(ArrayWritable.class);

        boolean success = job.waitForCompletion(true);

        return success ? 0 : 1;
    }

}
