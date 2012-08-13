package org.slc.sli.aggregation;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.bson.BSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;

/**
 * ScoreMapperTest - Test the highest ever score mapper using known values.
 */
@RunWith(MockitoJUnitRunner.class)
public class ScoreMapperTest {
    private ScoreMapper mapper = new ScoreMapper();
    @Mock
    private ScoreMapper.Context context;
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
        BasicDBObject studentAssessment = new BasicDBObject("body", saa);
        studentAssessment.put("metaData", new BasicDBObject("tenantId", "tenantId"));
        mapper.map("student123", studentAssessment, context);
        verify(context).write(eq(new TenantAndIdEmittableKey("student123", "tenantId")), eq(new DoubleWritable(42.0)));
    }
}
