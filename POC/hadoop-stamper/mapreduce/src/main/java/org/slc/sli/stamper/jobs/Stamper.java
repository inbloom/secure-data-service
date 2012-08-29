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
		jobConf.setInt("mapred.tasktracker.reduce.tasks.maximum", 3);		
		jobConf.setInt("mapred.reduce.tasks", 3);
		jobConf.setInt("mapred.tasktracker.map.tasks.maximum", 10);
		jobConf.setInt("mapred.map.tasks", 10);
		
		Job job = new Job(jobConf);
		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

}
