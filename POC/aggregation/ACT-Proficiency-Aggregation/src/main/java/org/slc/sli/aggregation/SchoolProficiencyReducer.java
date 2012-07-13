package org.slc.sli.aggregation;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Aggregate reducer for school math assessments.
 *
 * @author asaarela
 *
 */
public class SchoolProficiencyReducer
        extends Reducer<IntWritable, MapWritable, IntWritable, MapWritable> {

    @Override
    public void reduce(final IntWritable pKey,
                       final Iterable<MapWritable> pValues,
                       final Context pContext)
            throws IOException, InterruptedException {

    	// TODO -- emit the school level aggregate

    }

}
