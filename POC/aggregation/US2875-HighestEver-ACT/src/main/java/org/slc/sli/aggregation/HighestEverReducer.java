package org.slc.sli.aggregation;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.mongodb.hadoop.io.BSONWritable;

public class HighestEverReducer extends Reducer<Text, BSONWritable, Text, BSONWritable>{

    @Override
    protected void reduce(Text studentId, Iterable<BSONWritable> scoresResults, Context context)
            throws IOException, InterruptedException {
        BSONWritable highest = null;
        double highestScore = 0;
        for(BSONWritable scoreResult: scoresResults){
            double score = (Double) scoreResult.get("score");
            if(highest == null || score > highestScore){
                highest = scoreResult;
                highestScore = score;
            }
        }
        context.write(studentId, highest);
    }
    
}
