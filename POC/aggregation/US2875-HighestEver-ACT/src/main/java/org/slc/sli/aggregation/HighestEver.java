package org.slc.sli.aggregation;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;


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
        Configuration conf = getConf();
        String[] remainingArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        
        if (remainingArgs.length < 1) {
            System.err.println("Usage: HighestEver <assmtIDCode>");
            ToolRunner.printGenericCommandUsage(System.err);
            return 1;
        }
        
        String assmtIDCode = remainingArgs[0];
        Mongo m = new Mongo("localhost");
        DB db = m.getDB("sli");
        DBCollection assessments = db.getCollection("assessment");
        DBObject assmt = assessments.findOne(new BasicDBObject("body.assessmentIdentificationCode.ID", assmtIDCode));
        String assmtId = (String) assmt.get("_id");
        MongoURI input = new MongoURI("mongodb://localhost/sli.studentAssessmentAssociation");
        MongoConfigUtil.setInputURI(conf, input);
        MongoConfigUtil.setQuery(conf, new BasicDBObject("body.assessmentId", assmtId));
        MongoConfigUtil.setOutputURI(conf, "mongodb://localhost/sli.aggregatesAssessments");
        conf.set(ScoreMapper.SCORE_TYPE, "Scale score");
        
        Job job = new Job(conf, "HighestEver");
        job.setJarByClass(getClass());
        
        job.setMapperClass(ScoreMapper.class);
        job.setCombinerClass(HighestEverReducer.class);
        job.setReducerClass(HighestEverReducer.class);
        
        job.setInputFormatClass(MongoInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        job.setOutputFormatClass(MongoOutputFormat.class);
        
        boolean success = job.waitForCompletion(true);
        
        return success ? 0 : 1;
    }
}
