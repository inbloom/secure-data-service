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

package org.slc.sli.aggregation.mapreduce.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.Test;

/**
 * DoubleValueMapperTest
 */
public class DoubleValueMapperTest {

    @Test
    public void testGetValue() {
        BSONObject field = new BasicBSONObject("field", 1.312D);
        BSONObject entity = new BasicBSONObject("double", field);

        DoubleValueMapper mapper = new DoubleValueMapper();

        Writable value = mapper.getValue("double.field", entity);
        assertFalse(value instanceof NullWritable);
        assertTrue(value instanceof DoubleWritable);
        assertEquals(((DoubleWritable) value).get(), 1.312D, 0.05);
    }

    @Test
    public void testValueNotFound() {
        BSONObject field = new BasicBSONObject("field", 1.312D);
        BSONObject entity = new BasicBSONObject("double", field);

        DoubleValueMapper mapper = new DoubleValueMapper();

        Writable value = mapper.getValue("double.missing_field", entity);
        assertTrue(value instanceof NullWritable);
    }

    @Test
    public void testGetValueNotDouble() {
        BSONObject field = new BasicBSONObject("field", "Bob");
        BSONObject entity = new BasicBSONObject("double", field);

        DoubleValueMapper mapper = new DoubleValueMapper();

        Writable value = mapper.getValue("double.field", entity);
        assertTrue(value instanceof NullWritable);
    }
}
