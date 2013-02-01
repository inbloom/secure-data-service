/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.Test;


/**
 * EnumValueMapperTest
 */
public class EnumValueMapperTest {

    private enum Testing { TEST1, TEST2, TEST3 };

    @Test
    public void testGetValue() {

        BSONObject field = new BasicBSONObject("field", "TEST1");
        BSONObject entry = new BasicBSONObject("enum", field);
        BSONWritable entity = new BSONWritable(entry);

        EnumValueMapper<Testing> m = new EnumValueMapper<Testing>("enum.field", Testing.class);

        Writable value = m.getValue(entity);
        assertFalse(value instanceof NullWritable);
        assertTrue(value instanceof Text);
        assertEquals(((Text) value).toString(), Testing.TEST1.toString());
    }

    @Test
    public void testGetValueNotFound() {

        BSONObject field = new BasicBSONObject("field", "Unknown");
        BSONObject entry = new BasicBSONObject("enum", field);
        BSONWritable entity = new BSONWritable(entry);

        EnumValueMapper<Testing> m = new EnumValueMapper<Testing>("enum.field", Testing.class);

        Writable value = m.getValue(entity);
        assertTrue(value instanceof NullWritable);
    }
}
