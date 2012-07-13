package org.slc.sli.aggregation;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoURI;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.slc.sli.aggregation.mapreduce.MongoAggFormatter;


/**
 * Aggregate the highest ever score
 */
public class SchoolProficiency extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new SchoolProficiency(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        String assmtIDCode = "Grade 7 2011 State Math";

        MongoURI input = new MongoURI("mongodb://localhost/sli.studentAssessmentAssociation");

        Configuration conf = getConf();
        conf.set(SchoolProficiencyMapper.SCORE_TYPE, "Scale score");

        conf.set(MongoAggFormatter.UPDATE_FIELD, "aggregations.assessments." + assmtIDCode + ".HighestEver.Aggregate");

        MongoConfigUtil.setInputURI(conf, input);

        MongoConfigUtil.setQuery(conf, new BasicDBObject("body.aggregations.assessments", assmtIDCode));
        MongoConfigUtil.setOutputURI(conf, "mongodb://localhost/sli.educationalOrganization");
        MongoConfigUtil.setSplitSize(conf, 2);
        MongoConfigUtil.setCreateInputSplits(conf,  true);

        Job job = new Job(conf, "SchoolProficiency");
        job.setJarByClass(getClass());

        job.setMapperClass(SchoolProficiencyMapper.class);
        job.setCombinerClass(SchoolProficiencyReducer.class);
        job.setReducerClass(SchoolProficiencyReducer.class);

        job.setInputFormatClass(MongoInputFormat.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(MapWritable.class);
        job.setOutputFormatClass(MongoAggFormatter.class);

        boolean success = job.waitForCompletion(true);

        return success ? 0 : 1;
    }

}
