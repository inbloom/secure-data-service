package org.slc.sli.aggregation;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.slc.sli.aggregation.mapreduce.reduce.Highest;

/**
 * HighestTest - Test the Highest Ever Map/Reduce job using known values.
 */
@RunWith(MockitoJUnitRunner.class)
public class HighestTest {

    @Mock private Highest.Context context;

    @Test
    public void testReduceBSONWritableIterableOfDoubleWritableContext() throws IOException, InterruptedException {
        Highest reducer = new Highest();

 //       List<BSONWritable> scoreResults = Arrays.asList(new BSONWritable().put(key, value), new BSONWritable(42.0), new BSONWritable(12.0));
 //       IdFieldEmittableKey key = new IdFieldEmittableKey("student123");
 //       reducer.reduce(key, scoreResults, context);
 //       verify(context).write(key, new DoubleWritable(42.0));
    }

}
