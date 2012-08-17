package org.slc.sli.aggregation;

import java.io.IOException;

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BSONObject;

import org.slc.sli.aggregation.mapreduce.io.MongoAggFormatter;
import org.slc.sli.aggregation.mapreduce.map.BSONValueLookup;
import org.slc.sli.aggregation.mapreduce.map.key.IdFieldEmittableKey;

/**
 * Reducer to get the highest value
 *
 * @author nbrown
 *
 */
public class Highest extends Reducer<IdFieldEmittableKey, BSONWritable, IdFieldEmittableKey, BSONWritable> {

    @Override
    protected void reduce(IdFieldEmittableKey id, Iterable<BSONWritable> scoreResults, Context context)
            throws IOException, InterruptedException {
        Double highest = null;
        for (BSONWritable scoreResult: scoreResults) {
            String[] s = BSONValueLookup.getValues(scoreResult, "body.scoreResults.result");
            for (String val : s) {
                double score = Double.parseDouble(val);
                if (highest == null || score > highest) {
                    highest = new Double(score);
                }
            }
        }

        String field = context.getConfiguration().get(MongoAggFormatter.UPDATE_FIELD);

        BSONObject obj = BSONValueLookup.setValue(field,  highest);
        BSONWritable result = new BSONWritable(obj);
        context.write(id, result);
    }

}
