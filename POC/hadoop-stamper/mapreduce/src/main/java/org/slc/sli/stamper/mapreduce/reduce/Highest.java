package org.slc.sli.stamper.mapreduce.reduce;

import java.io.IOException;
import java.util.Map;

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BSONObject;
import org.codehaus.jackson.map.ObjectMapper;

import org.slc.sli.stamper.mapreduce.io.JobConfiguration;
import org.slc.sli.stamper.mapreduce.io.MongoAggFormatter;
import org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey;
import org.slc.sli.stamper.util.BSONUtilities;

/**
 * Reducer to get the highest value
 *
 * @author nbrown
 *
 */
public class Highest extends Reducer<IdFieldEmittableKey, BSONWritable, IdFieldEmittableKey, BSONWritable> {

    static long callCount = 1;
    ObjectMapper om = new ObjectMapper();

    @SuppressWarnings("unchecked")
    @Override
    protected void reduce(IdFieldEmittableKey id, Iterable<BSONWritable> results, Context context)
            throws IOException, InterruptedException {
        double highest = Double.MIN_VALUE;
        String mapField = context.getConfiguration().get(JobConfiguration.MAP_FIELD_PROPERTY);

        for (BSONWritable scoreResult: results) {
            String[] s = BSONUtilities.getValues(scoreResult, mapField);

            for (String val : s) {
                double score = Double.parseDouble(val);
                if (score > highest) {
                    highest = score;
                }
            }
        }

        String field = context.getConfiguration().get(MongoAggFormatter.UPDATE_FIELD);

        // If there is an id_map, transform the input id to the output id.
        String tmp = context.getConfiguration().get(JobConfiguration.ID_MAP_PROPERTY);
        if (tmp != null) {
            Map<String, String> mapping = om.readValue(tmp, Map.class);
            Text idText = id.getId();
            id.setFieldName(mapping.get(id.getFieldName().toString()));
            id.setId(idText);
        }

        BSONObject obj = BSONUtilities.setValue(field,  highest);
        BSONWritable result = new BSONWritable(obj);
        context.write(id, result);

        System.err.println("" + callCount++);
    }

}
