package org.slc.sli.aggregation;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;

import org.slc.sli.aggregation.mapreduce.TenantAndID;

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
        extends Reducer<TenantAndID, MapWritable, TenantAndID, MapWritable> {

    MapWritable counts = new MapWritable();

    public SchoolProficiencyReducer() {
        Text t = new Text();
        t.set("W");
        counts.put(t, new LongWritable());
        t = new Text();
        t.set("B");
        counts.put(t, new LongWritable());
        t = new Text();
        t.set("S");
        counts.put(t, new LongWritable());
        t = new Text();
        t.set("E");
        counts.put(t, new LongWritable());
        t = new Text();
        t.set("!");
        counts.put(t, new LongWritable());
        t = new Text();
        t.set("-");
        counts.put(t, new LongWritable());
    }

    @Override
    public void reduce(final TenantAndID pKey,
                       final Iterable<MapWritable> pValues,
                       final Context context)
            throws IOException, InterruptedException {

        for (MapWritable values : pValues) {
            for (MapWritable.Entry<Writable, Writable> result : values.entrySet()) {
                count((Text) result.getKey(), (LongWritable) result.getValue());
            }
        }
        System.err.println(counts.toString());
        context.write(pKey, counts);
    }

    protected void count(Text key, LongWritable count) {
        LongWritable w = (LongWritable) counts.get(key);
        w.set(w.get() + count.get());
        counts.put(key,  w);
    }
}
