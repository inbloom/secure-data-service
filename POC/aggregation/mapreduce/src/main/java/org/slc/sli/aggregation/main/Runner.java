package org.slc.sli.aggregation.main;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.slc.sli.aggregation.mapreduce.map.ConfigurableMapReduceJob;


/**
 * Runner for configurable aggregation jobs.
 */
public class Runner extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new Runner(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = new Configuration();

        if (args.length == 0) {
            System.err.println("usage: org.slc.sli.aggregation.man.Runner job_configuration.conf");
            return -1;
        }

        String text = FileUtils.readFileToString(new File(args[0]));
        JobConf jobConf = ConfigurableMapReduceJob.parse(conf, text);

        Job job = new Job(jobConf);
        boolean success = job.waitForCompletion(true);

        return success ? 0 : 1;
    }
}
