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

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * ValueMapper - Lookup the value of a mongo collection field for each input value. If found,
 * emit the value using type Writable. If not found, emit NullWritable.
 */
public abstract class ValueMapper extends Mapper<EmittableKey, BSONObject, EmittableKey, Writable> {

    private String fieldName = null;

    @Override
    public void map(EmittableKey key, BSONObject entity, Context context) throws InterruptedException, IOException {

        Writable value = getValue(context.getConfiguration().get("fields"), entity);
        context.write(key, value);
    }

    /**
     * getValue - Attempt to lookup the field in the entity and covert it to an appropriate
     * Writable. If the field does not exist or is the wrong type, return NullWritable.
     *
     * @param fieldValue
     *            - Value of the field to convert.
     * @return Writable instance of the field, or NullWritable if the field does not exist
     *         or contains incompatible values.
     */
    public abstract Writable getValue(String fieldName, BSONObject entity);
}
