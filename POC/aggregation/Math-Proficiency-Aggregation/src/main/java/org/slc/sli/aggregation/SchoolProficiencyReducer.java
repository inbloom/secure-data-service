package org.slc.sli.aggregation;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;

/**
 * Aggregate Math scores and at the school level based on scale score.
 *
 * The following buckets are defined:
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
        extends Reducer<TenantAndIdEmittableKey, Text, TenantAndIdEmittableKey, MapWritable> {

    MapWritable counts = new MapWritable();

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
        LongWritable w = (LongWritable) counts.get(key);
        if (w != null) {
            w.set(w.get() + 1);
            counts.put(key, w);
        }
    }

    public SchoolProficiencyReducer() {
        Text t = new Text();
        t.set("W");
        counts.put(t, new LongWritable(0));
        t = new Text();
        t.set("B");
        counts.put(t, new LongWritable(0));
        t = new Text();
        t.set("S");
        counts.put(t, new LongWritable(0));
        t = new Text();
        t.set("E");
        counts.put(t, new LongWritable(0));
        t = new Text();
        t.set("!");
        counts.put(t, new LongWritable(0));
        t = new Text();
        t.set("-");
        counts.put(t, new LongWritable(0));
    }

}
