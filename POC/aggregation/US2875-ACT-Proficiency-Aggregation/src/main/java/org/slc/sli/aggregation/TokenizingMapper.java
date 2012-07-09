package org.slc.sli.aggregation;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


/**
 * Turns lines from a text file into (word, 1) tuples.
 */
public class TokenizingMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private static final IntWritable one = new IntWritable(1);
    
    protected void map(LongWritable offset, Text value, Context context) 
            throws IOException, InterruptedException {
        
        StringTokenizer tok = new StringTokenizer(value.toString());
        while (tok.hasMoreTokens()) {
            Text word = new Text(tok.nextToken());
            context.write(word, one);
        }
    }
}
