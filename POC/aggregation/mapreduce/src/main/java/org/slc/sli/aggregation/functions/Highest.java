package org.slc.sli.aggregation.functions;

import java.io.IOException;
import java.util.TreeSet;

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BSONObject;
import org.codehaus.jackson.map.ObjectMapper;

import org.slc.sli.aggregation.mapreduce.io.JobConfiguration;
import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;
import org.slc.sli.aggregation.util.BSONUtilities;

/**
 * Function that supports calculating the n-th highest value over a collection of values. The
 * job configuration defines 'n' for the job and the period to examine.  The field we're
 * working against is defined as the 'map.fields' value from the map configuration.
 */
public class Highest extends Reducer<TenantAndIdEmittableKey, BSONWritable, TenantAndIdEmittableKey, BSONWritable> {

    static long callCount = 1;
    ObjectMapper om = new ObjectMapper();

    @Override
    public void reduce(TenantAndIdEmittableKey id, Iterable<BSONWritable> results, Context context)
            throws IOException, InterruptedException {

        BoundedTreeSet nthHighest = null;

        JobConfiguration.ConfigSections sections = JobConfiguration.readFromConfiguration(context.getConfiguration());

        JobConfiguration.ParametersConfig params = sections.getMetadata().getParameters();
        int n = params.getN();
        nthHighest = new BoundedTreeSet(n);

        // Fields is expressed as an array, but in this situation we only expect one. Pull off the first one.
        String field = sections.getMapper().getFields().keySet().toArray(new String[0])[0];
        String updateField = sections.getReduce().getField();

        for (BSONWritable scoreResult: results) {
            String[] values = BSONUtilities.getValues(scoreResult, field);
            for (String value : values) {
                try {
                    double score = Double.parseDouble(value);
                    nthHighest.add(score);
                } catch (NumberFormatException e) {
                    // ignore invalid values.
                    continue;
                }
            }
        }

        int i = 0;
        Double value = Double.MIN_VALUE;

        // Get the nth highest value if we have n values, or the closes value we can find.
        while (i < n && !nthHighest.isEmpty()) {
            value = nthHighest.pollLast();
            i++;
        }

        BSONObject obj = BSONUtilities.setValue(updateField,  value);
        BSONWritable result = new BSONWritable(obj);
        context.write(id, result);
    }


    /**
     * Helper class to collect the n highest values.
     */
    class BoundedTreeSet extends TreeSet<Double> {
        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 2795956513757734802L;
        protected int maxElements;

        BoundedTreeSet(int max) {
            super();
            this.maxElements = max;
        }

        @Override
        public boolean add(Double v) {
            boolean rval = true;

            // ensure we don't add more elements than max and that the highest n remain in the set.
            if (size() < maxElements) {
                super.add(v);
            } else {
                if (comparator() != null) {
                    if (comparator().compare(v, first()) == 1) {
                        pollFirst();
                        super.add(v);
                    } else {
                        rval = false;
                    }
                } else {
                    if (v.compareTo(first()) == 1) {
                        pollFirst();
                        super.add(v);
                    }
                }
            }
            return rval;
        }
    }
}
