package org.slc.sli.aggregation;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Reducer;

import com.mongodb.hadoop.io.BSONWritable;

public class HighestEverReducer extends Reducer<BSONWritable, DoubleWritable, BSONWritable, DoubleWritable>{

    @Override
    protected void reduce(BSONWritable id, Iterable<DoubleWritable> scoresResults, Context context)
            throws IOException, InterruptedException {
        DoubleWritable highest = null;
        for(DoubleWritable scoreResult: scoresResults){
            double score = scoreResult.get();
            if(highest == null || score > highest.get()){
                highest = scoreResult;
            }
        }
        context.write(id, highest);
    }
    
}
