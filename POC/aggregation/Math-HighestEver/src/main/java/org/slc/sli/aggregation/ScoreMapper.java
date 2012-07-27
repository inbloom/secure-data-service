package org.slc.sli.aggregation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;

import org.slc.sli.aggregation.mapreduce.TenantAndID;


/**
 * Maps a SAA to a double score
 */
public class ScoreMapper extends Mapper<String, BSONObject, TenantAndID, DoubleWritable> {

    public static final String SCORE_TYPE = "ScoreType";
    DoubleWritable score = new DoubleWritable();
    TenantAndID tenantAndId = new TenantAndID();

    @Override
    @SuppressWarnings("unchecked")
    protected void map(String id, BSONObject studentAssessment, Context context)
            throws IOException, InterruptedException {

        Map<String, Object> metaData = (Map<String, Object>) studentAssessment.get("metaData");
        String tenant = (String) metaData.get("tenantId");

        Map<String, Object> body = (Map<String, Object>) studentAssessment.get("body");
        String studentId = (String) body.get("studentId");

        List<Map<String, Object>> scoreResults = (List<Map<String, Object>>) body.get("scoreResults");
        for (Map<String, Object> result: scoreResults) {
            String scoreString = (String) result.get("result");
            double s = Double.parseDouble(scoreString);

            tenantAndId.setId(studentId);
            tenantAndId.setTenant(tenant);
            score.set(s);

            context.write(tenantAndId, score);
            return;
        }
    }
}
