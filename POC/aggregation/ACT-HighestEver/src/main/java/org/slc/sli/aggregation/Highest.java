package org.slc.sli.aggregation;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Reducer to get the highest value
 * 
 * @author nbrown
 *
 */
public class Highest extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

    @Override
    protected void reduce(Text id, Iterable<DoubleWritable> scoreResults, Context context)
            throws IOException, InterruptedException {
        Double highest = null;
        for (DoubleWritable scoreResult: scoreResults) {
            double score = scoreResult.get();
            if (highest == null || score > highest) {
                highest = score;
            }
        }
        context.write(id, new DoubleWritable(highest));
    }
    
}
