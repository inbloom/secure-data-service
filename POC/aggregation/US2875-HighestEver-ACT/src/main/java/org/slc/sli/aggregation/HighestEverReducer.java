package org.slc.sli.aggregation;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class HighestEverReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable>{

    @Override
    protected void reduce(Text studentId, Iterable<DoubleWritable> scores, Context context)
            throws IOException, InterruptedException {
        DoubleWritable highest = null;
        for(DoubleWritable score: scores){
            if(highest == null || score.get() > highest.get()){
                highest = score;
            }
        }
        context.write(studentId, highest);
    }
    
}
