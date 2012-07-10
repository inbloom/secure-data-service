package org.slc.sli.aggregation;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.hadoop.io.BSONWritable;

@RunWith(MockitoJUnitRunner.class)
public class ScoreMapperTest {
    private ScoreMapper mapper = new ScoreMapper();
    @Mock
    private Mapper<String, BSONObject, Text, BSONWritable>.Context context;
    private Configuration config = new Configuration();
    
    @Before
    public void setUp() {
        config.setStrings(ScoreMapper.SCORE_TYPE, "Scale Score");
    }
    
    @Test
    public void test() throws IOException, InterruptedException {
        BSONObject percentile = BasicDBObjectBuilder.start("assessmentReportingMethod", "Percentile")
                .add("result", "98").get();
        BSONObject scaleScore = BasicDBObjectBuilder.start("assessmentReportingMethod", "Scale Score")
                .add("result", "42").get();
        BSONObject saa = BasicDBObjectBuilder.start("studentId", "student123").add("assessmentId", "ACT")
                .add("scoreResults", Arrays.asList(percentile, scaleScore)).get();
        when(context.getConfiguration()).thenReturn(config);
        mapper.map("student123", new BasicDBObject("body", saa), context);
        verify(context).write(eq(new Text("student123")), argThat(new ArgumentMatcher<BSONWritable>() {

            @Override
            public boolean matches(Object argument) {
                return ((BSONWritable) argument).get("score").equals(42.0);
            }
        }));
    }
}
