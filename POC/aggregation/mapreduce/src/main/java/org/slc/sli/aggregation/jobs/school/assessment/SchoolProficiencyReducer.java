package org.slc.sli.aggregation.jobs.school.assessment;

import java.io.IOException;
import java.util.logging.Logger;

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;

/**
 * Aggregate Math scores and at the school level based on scale score.
 *
 * Buckets are defined in the job configuration file.  For example,
 *
 *  Scale value      1-character code    Description
 *  6 <= n <= 14            W            Warning
 * 15 <= n <= 20            B
 * 21 <= n <= 27            S
 * 28 <= n <= 33            E
 *                          !            Invalid Score or Data Error
 *                          -            Score not available (N/A)
 *
 * @author asaarela
 */
public class SchoolProficiencyReducer
        extends Reducer<TenantAndIdEmittableKey, Text, TenantAndIdEmittableKey, BSONWritable> {

    BSONWritable counts = new BSONWritable();

    @Override
    public void reduce(final TenantAndIdEmittableKey pKey,
                       final Iterable<Text> pValues,
                       final Context context)
            throws IOException, InterruptedException {

        for (Text result : pValues) {
            count(result);
        }
        Logger.getLogger("SchoolProficiencyReducer").warning("writing reduce record to: " + pKey.toString());
        context.write(pKey, counts);
    }

    protected void count(Text key) {
        System.err.println(key.toString());
        Long w =  (Long) counts.get(key.toString());
        if (w != null) {
            w = w + 1;
            counts.put(key.toString(), w);
        }
    }

    public SchoolProficiencyReducer() {
        // TODO - replace this with rankings from the configuration.
        counts.put("W", 0L);
        counts.put("B", 0L);
        counts.put("S", 0L);
        counts.put("E", 0L);
        counts.put("!", 0L);
        counts.put("-", 0L);
    }

}
