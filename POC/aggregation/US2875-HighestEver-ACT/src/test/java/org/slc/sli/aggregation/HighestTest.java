package org.slc.sli.aggregation;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HighestTest {
    
    @Mock private Highest.Context context;
    
    @Test
    public void testReduceBSONWritableIterableOfDoubleWritableContext() throws IOException, InterruptedException {
        Highest reducer = new Highest();
        List<DoubleWritable> scoreResults = Arrays.asList(new DoubleWritable(15.0), new DoubleWritable(42.0), new DoubleWritable(12.0));
        Text key = new Text("student123");
        reducer.reduce(key, scoreResults, context);
        verify(context).write(key, new DoubleWritable(42.0));
    }
    
}
