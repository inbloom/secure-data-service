package org.slc.sli.aggregation.jobs.student.assessment;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.slc.sli.aggregation.mapreduce.io.JobConfiguration;
import org.slc.sli.aggregation.mapreduce.map.ConfigurableMapReduceJob;
import org.slc.sli.aggregation.util.IdByNameLookup;


/**
 * Aggregate the highest ever score
 */
public class HighestEver extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {

        String assmtIDCode = args.length == 0 ? "Grade 7 2011 State Math" : args[0];
        String assmtId = IdByNameLookup.getAssessmentId(assmtIDCode);

        Configuration conf = new Configuration();

        conf.set(JobConfiguration.ASSESSMENT_SCORE_TYPE, "Scale score");
        conf.set(JobConfiguration.ID_PLACEHOLDER, assmtId);

        JobConf jobConf = ConfigurableMapReduceJob.parseMapper(conf, "student/Assessment-Calculated-HighestEver.json");
        FileSystem fs = FileSystem.get(jobConf);

        //DistributedCache.addFileToClassPath(new Path("/libs/" + jobConf.getJar()), fs);
        DistributedCache.addArchiveToClassPath(new Path("/libs/mongo-java-driver-2.8.0.jar"), jobConf);
        DistributedCache.addArchiveToClassPath(new Path("/libs/mongo-hadoop-core_cdh4b1-1.0.0.jar"), jobConf);

        Job job = new Job(jobConf);

        // Load dependencies from distributed cache
        boolean success = job.waitForCompletion(true);

        return success ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new HighestEver(), args);
        System.exit(res);
    }
}
