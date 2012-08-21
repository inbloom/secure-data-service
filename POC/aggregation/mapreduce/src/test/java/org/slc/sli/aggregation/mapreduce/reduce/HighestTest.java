/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.aggregation.mapreduce.reduce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.bson.BSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.slc.sli.aggregation.mapreduce.io.MongoAggFormatter;
import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;
import org.slc.sli.aggregation.mapreduce.map.key.IdFieldEmittableKey;
import org.slc.sli.aggregation.util.BSONUtilities;

/**
 * HighestTest - Tests for he Highest value reducer
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Highest.class, Highest.Context.class })
public class HighestTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testReduce() throws Exception {
        Highest toTest = new Highest();

        IdFieldEmittableKey id = new IdFieldEmittableKey("body.test.id");
        id.setId(new Text("MytestId"));

        ArrayList<BSONWritable> results = new ArrayList<BSONWritable>();

        BSONWritable result1 = new BSONWritable(BSONUtilities.setValue("body.scoreResults.result", 16.0));
        BSONWritable result2 = new BSONWritable(BSONUtilities.setValue("body.scoreResults.result", 31.0));
        BSONWritable result3 = new BSONWritable(BSONUtilities.setValue("body.scoreResults.result", 5.0));
        BSONWritable result4 = new BSONWritable(BSONUtilities.setValue("body.scoreResults.result", 12.0));
        BSONWritable result5 = new BSONWritable(BSONUtilities.setValue("body.scoreResults.result", 3.0));
        BSONWritable result6 = new BSONWritable(BSONUtilities.setValue("body.scoreResults.result", 27.0));

        results.add(result1);
        results.add(result2);
        results.add(result3);
        results.add(result4);
        results.add(result5);
        results.add(result6);

        Highest.Context context = Mockito.mock(Highest.Context.class);
        PowerMockito.when(context, "write", Matchers.any(EmittableKey.class),
            Matchers.any(BSONObject.class)).thenAnswer(new Answer<BSONObject>() {

            @Override
            public BSONObject answer(InvocationOnMock invocation) throws Throwable {

                Object[] args = invocation.getArguments();

                assertNotNull(args);
                assertEquals(args.length, 2);

                assertTrue(args[0] instanceof IdFieldEmittableKey);
                assertTrue(args[1] instanceof BSONWritable);

                IdFieldEmittableKey id = (IdFieldEmittableKey) args[0];
                assertEquals(id.getIdField().toString(), "body.test.id");
                assertEquals(id.getId().toString(), "MytestId");

                BSONWritable e = (BSONWritable) args[1];
                String val = BSONUtilities.getValue(e, "test.reduce.output.field");
                assertNotNull(val);
                assertEquals(31.0D, Double.parseDouble(val), 0.001D);
                return null;
            }
        });

        Configuration conf = new Configuration();
        conf.set(MongoAggFormatter.UPDATE_FIELD, "test.reduce.output.field");
        PowerMockito.when(context, "getConfiguration").thenReturn(conf);

        toTest.reduce(id, results, context);
    }
}
