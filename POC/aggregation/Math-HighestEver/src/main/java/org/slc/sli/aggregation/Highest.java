package org.slc.sli.aggregation;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Reducer;

import org.slc.sli.aggregation.mapreduce.TenantAndID;

/**
 * Reducer to get the highest value
 *
 * @author nbrown
 *
 */
public class Highest extends Reducer<TenantAndID, DoubleWritable, TenantAndID, DoubleWritable> {

    DoubleWritable val = new DoubleWritable();

    @Override
    protected void reduce(TenantAndID id, Iterable<DoubleWritable> scoreResults, Context context)
            throws IOException, InterruptedException {
        Double highest = null;
        for (DoubleWritable scoreResult: scoreResults) {
            double score = scoreResult.get();
            if (highest == null || score > highest) {
                highest = score;
            }
        }
        val.set(highest);
        context.write(id, val);
    }

}
