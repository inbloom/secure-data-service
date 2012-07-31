package org.slc.sli.aggregation;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.slc.sli.aggregation.mapreduce.MongoAggFormatter;
import org.slc.sli.aggregation.mapreduce.TenantAndID;


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
        String assmtId = getAssessmentId(assmtIDCode);

        MongoURI input = new MongoURI("mongodb://localhost/sli.studentAssessmentAssociation");

        Configuration conf = getConf();
        conf.set(ScoreMapper.SCORE_TYPE, "Scale score");
        conf.set(MongoAggFormatter.UPDATE_FIELD, "calculatedValues.assessments." + assmtIDCode + ".HighestEver.ScaleScore");

        MongoConfigUtil.setInputURI(conf, input);
        MongoConfigUtil.setCreateInputSplits(conf,  true);
        MongoConfigUtil.setShardChunkSplittingEnabled(conf, true);

        BasicDBObject query = new BasicDBObject();
        query.put("body.assessmentId", assmtId);
        query.put("body.scoreResults.assessmentReportingMethod", "Scale score");

        BasicDBObject fields = new BasicDBObject();
        fields.put("body.studentId", 1);
        fields.put("body.scoreResults.result", 1);
        fields.put("metaData.tenantId", 1);

        MongoConfigUtil.setQuery(conf, query);
        MongoConfigUtil.setFields(conf, fields);
        MongoConfigUtil.setOutputURI(conf, "mongodb://localhost/sli.student");

        Job job = new Job(conf, "HighestEver");
        job.setJarByClass(HighestEver.class);

        job.setMapperClass(ScoreMapper.class);
        job.setCombinerClass(Highest.class);
        job.setReducerClass(Highest.class);

        job.setInputFormatClass(MongoInputFormat.class);

        job.setOutputKeyClass(TenantAndID.class);
        job.setOutputValueClass(DoubleWritable.class);
        job.setOutputFormatClass(MongoAggFormatter.class);

        boolean success = job.waitForCompletion(true);

        return success ? 0 : 1;
    }

    protected String getAssessmentId(String assmtIDCode) throws UnknownHostException {
        Mongo m = new Mongo("localhost");
        DB db = m.getDB("sli");
        DBCollection assessments = db.getCollection("assessment");
        DBObject assmt = assessments.findOne(new BasicDBObject("body.assessmentIdentificationCode.ID", assmtIDCode));
        String assmtId = (String) assmt.get("_id");
        return assmtId;
    }
}
