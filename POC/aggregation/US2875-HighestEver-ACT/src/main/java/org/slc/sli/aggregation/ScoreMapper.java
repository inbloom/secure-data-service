package org.slc.sli.aggregation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;


/**
 * Maps a SAA to an Integer score
 */
public class ScoreMapper extends Mapper<String, BSONObject, Text, DoubleWritable> {

    public static final String SCORE_TYPE = "ScoreType";

    @Override
    protected void map(String studentId, BSONObject studentAssessment, Context context)
            throws IOException, InterruptedException {
        String type = context.getConfiguration().get(SCORE_TYPE);
        @SuppressWarnings("unchecked")
        List<Map<String, String>> results = (List<Map<String, String>>) ((Map<String, Object>) studentAssessment.get("body")).get("scoreResults");
        for(Map<String, String> result: results){
            if(type.equals(result.get("assessmentReportingMethod"))){
                String scoreString = result.get("result");
                double score = Double.parseDouble(scoreString);
                System.out.println("Score is "+score);
                context.write(new Text(studentId), new DoubleWritable(score));
                return;
            }
        }
    }
    
}
