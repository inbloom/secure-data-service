package org.slc.sli.aggregation.jobs.school.assessment;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.slc.sli.aggregation.mapreduce.io.JobConfiguration;
import org.slc.sli.aggregation.mapreduce.map.ConfigurableMapReduceJob;
import org.slc.sli.aggregation.util.IdByNameLookup;


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
        String assmtIDCode = args.length == 0 ? "Grade 7 2011 State Math" : args[0];
        String assmtId = IdByNameLookup.getAssessmentId(assmtIDCode);

        Configuration conf = new Configuration();

        conf.set(JobConfiguration.ASSESSMENT_SCORE_TYPE, "Scale score");
        conf.set(JobConfiguration.ID_PLACEHOLDER, assmtId);

        JobConf jobConf = ConfigurableMapReduceJob.parseMapper(conf, "school/Assessment-Aggregate-HighestEver.json");

        Job job = new Job(jobConf);
        boolean success = job.waitForCompletion(true);

        return success ? 0 : 1;
    }
}
