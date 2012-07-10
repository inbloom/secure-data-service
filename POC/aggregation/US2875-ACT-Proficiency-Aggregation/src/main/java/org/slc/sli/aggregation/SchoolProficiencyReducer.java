package org.slc.sli.aggregation;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;


public class SchoolProficiencyReducer
        extends Reducer<IntWritable, DoubleWritable, IntWritable, DoubleWritable> {
    @Override
    public void reduce( final IntWritable pKey,
                        final Iterable<DoubleWritable> pValues,
                        final Context pContext )
            throws IOException, InterruptedException{
        int count = 0;
        double sum = 0;
        for ( final DoubleWritable value : pValues ){
            sum += value.get();
            count++;
        }

        final double avg = sum / count;

        pContext.write( pKey, new DoubleWritable( avg ) );
    }

}
