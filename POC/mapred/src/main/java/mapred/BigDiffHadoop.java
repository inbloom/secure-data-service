package mapred;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class BigDiffHadoop {

	public static class Map extends
			Mapper<LongWritable, Text, Text, Text> {

		private Text src = new Text();
		private Text recValue = new Text();
		
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String line = value.toString();
			int recStartPos = line.indexOf(" ");
			src.set(line.substring(0, recStartPos));
			recValue.set(line.substring(recStartPos+1));
			context.write(recValue, src);
			
		}
	}

	public static class Reduce extends
			Reducer<Text, Text, Text, Text> {

		private Text src = new Text();

		public void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			int c = 0;
			for (Text val : values) {
				src.set(val);
				c++;
			}
			if (c<2) {
				context.write(src, key);
			}
		}
	}

	public void execute(String inputPath1, String inputPath2, String outputPath) throws Exception {
		Configuration conf = new Configuration();

		Job job = new Job(conf, "bigdiff");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(inputPath1));
		FileInputFormat.addInputPath(job, new Path(inputPath2));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		job.waitForCompletion(true);
	}

}