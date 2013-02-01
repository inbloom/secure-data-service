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

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import org.slc.sli.aggregation.util.BSONUtilities;

/**
 * StringValueMapper - mongo value mapper that emits String values.
 */
public class StringValueMapper extends ValueMapper {

    public StringValueMapper(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public Writable getValue(BSONWritable entity) {
        Writable rval = NullWritable.get();

        String value = BSONUtilities.getValue(entity, fieldName);
        if (value != null && value instanceof String) {
            rval = new Text(value);
        }
        return rval;
    }
}
