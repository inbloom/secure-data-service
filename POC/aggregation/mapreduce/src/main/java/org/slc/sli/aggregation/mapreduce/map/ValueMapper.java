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

import java.io.IOException;

import com.mongodb.DBObject;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * ValueMapper - Lookup the value of a mongo collection field for each input value.  If found,
 * emit the value using type Writable.  If not found, emit NullWritable.
 */
public abstract class ValueMapper extends MapReduceBase implements Mapper<EmittableKey, DBObject, EmittableKey, Writable> {

    protected final String fieldName;

    public ValueMapper(final String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void map(EmittableKey key, DBObject entity,
        OutputCollector<EmittableKey, Writable> output, Reporter reporter) throws IOException {

        Writable value = getValue(entity.get(fieldName));
        output.collect(key, value);
    }

    /**
     * getValue - Attempt to lookup the field in the entity and covert it to an appropriate
     * Writable.  If the field does not exist or is the wrong type, return NullWritable.
     *
     * @param fieldValue - Value of the field to convert.
     * @return Writable instance of the field, or NullWritable if the field does not exist
     *  or contains incompatible values.
     */
    public abstract Writable getValue(Object fieldValue);
}
