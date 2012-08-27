package org.slc.sli.stamper.jobs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob;

public class Stamper extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new Stamper(), args);
        System.exit(res);
    }
	
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		JobConf jobConf = ConfigurableMapReduceJob.parseMapper(conf, "stamper.json");
		jobConf.setNumMapTasks(30);
		jobConf.setNumReduceTasks(6);
		jobConf.setMemoryForMapTask(4096);
		jobConf.setMemoryForReduceTask(8192);
		jobConf.setInt("mongo.input.split_size", 2);
		Job job = new Job(jobConf);
		job.setNumReduceTasks(6);
		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

}
