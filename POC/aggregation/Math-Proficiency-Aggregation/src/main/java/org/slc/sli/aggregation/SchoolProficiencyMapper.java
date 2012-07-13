package org.slc.sli.aggregation;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;

/**
 * Map ACT scores and aggregate them at the school level based on scale score.
 *
 * The following buckets are defined:
 *
 *  <no score>       ==> 0
 *  0 < score <   20 ==> 1
 * 20 < score <   70 ==> 2
 * 70 < score <   85 ==> 3
 * 86 < score <= 100 ==> 4
 *
 * @author asaarela
 */
public class SchoolProficiencyMapper
        extends Mapper<Text, BSONObject, Text, MapWritable> {

    public static final String SCORE_TYPE = "ProficiencyCounts";

    @Override
    public void map(final Text schoolUUID,
                    final BSONObject student,
                    final Context context)
            throws IOException, InterruptedException {

        String type = context.getConfiguration().get(SCORE_TYPE);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) student.get("body");

        Map<IntWritable, IntWritable> buckets = new HashMap<IntWritable, IntWritable>();

        // TODO -- find the highest ever math score for each student in the school.

        MapWritable w = new MapWritable();
        w.putAll(buckets);
        context.write(schoolUUID, w);


    }
}
