package org.slc.sli.aggregation.jobs.school.assessment;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.slc.sli.aggregation.mapreduce.map.ConfigurableMapReduceJob;


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
        Configuration conf = new Configuration();

        JobConf jobConf = ConfigurableMapReduceJob.parse(conf, "school/Assessment-Aggregate-Higest.json");

        Job job = new Job(jobConf);
        boolean success = job.waitForCompletion(true);

        return success ? 0 : 1;
    }
}
