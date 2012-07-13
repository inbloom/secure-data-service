package org.slc.sli.aggregation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SchoolProficiencyMapperTest {
    @Mock 
    private Mapper<Date, BSONObject, IntWritable, DoubleWritable>.Context context;
    private SchoolProficiencyMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new SchoolProficiencyMapper();
    }

    @Test
    public void testMap() throws IOException, InterruptedException {
        mapper.map(new Date(), null, context);
        
        verify(context, times(1)).write(new IntWritable(1), new DoubleWritable(1));
        verify(context, times(2)).write(new IntWritable(2), new DoubleWritable(2));
        
        verifyNoMoreInteractions(context);
    }
}
