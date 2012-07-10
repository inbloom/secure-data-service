package org.slc.sli.aggregation;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mongodb.hadoop.io.BSONWritable;

@RunWith(MockitoJUnitRunner.class)
public class HighestTest {
    
    @Mock private Highest.Context context;
    
    @Test
    public void testReduceBSONWritableIterableOfDoubleWritableContext() throws IOException, InterruptedException {
        Highest reducer = new Highest();
        BSONWritable key = new BSONWritable();
        key.put("assessmentId", "ACT");
        key.put("studentId", "student123");
        key.put("assessmentReportingMethod", "Scale score");
        List<DoubleWritable> scoreResults = Arrays.asList(new DoubleWritable(15.0), new DoubleWritable(42.0), new DoubleWritable(12.0));
        reducer.reduce(key, scoreResults, context);
        verify(context).write(key, new DoubleWritable(42.0));
    }
    
}
