package org.slc.sli.aggregation.jobs.school.assessment;

import java.io.IOException;
import java.util.logging.Logger;

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import org.slc.sli.aggregation.mapreduce.io.JobConfiguration;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.BandConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.BandsConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.MetadataConfig;
import org.slc.sli.aggregation.mapreduce.io.MongoAggFormatter;
import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;
import org.slc.sli.aggregation.util.BSONUtilities;

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

    BSONObject counts = new BasicBSONObject();
    private BandsConfig bands;

    @Override
    public void reduce(final TenantAndIdEmittableKey pKey,
                       final Iterable<Text> pValues,
                       final Context context)
            throws IOException, InterruptedException {

        for (Text result : pValues) {
            count(result);
        }
        Logger.getLogger("SchoolProficiencyReducer").warning("writing reduce record to: " + pKey.toString());

        String field = context.getConfiguration().get(MongoAggFormatter.UPDATE_FIELD);
        BSONObject obj = BSONUtilities.setValue(field, counts);
        BSONWritable output = new BSONWritable(obj);
        context.write(pKey, output);
    }

    protected void count(Text key) {
        System.err.println(key.toString());
        Long w =  (Long) counts.get(key.toString());
        if (w != null) {
            w = w + 1;
            counts.put(key.toString(), w);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void setup(Reducer.Context context) throws IOException, InterruptedException {
        super.setup(context);

        MetadataConfig cfg = JobConfiguration.getAggregateMetadata(context.getConfiguration());
        bands = cfg.getBands();

        BandConfig[] config = bands.toArray(new BandConfig[0]);

        for (BandConfig band : config) {
            counts.put(band.getAbbreviation(), 0L);
        }
    }
}
