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

import java.util.logging.Logger;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.bson.BSONObject;

/**
 * DoubleValueMapper - mongo value mapper that emits double values.
 */
class DoubleValueMapper extends ValueMapper {

    private Logger log = Logger.getLogger("DoubleValueMapper");

    public DoubleValueMapper(String fieldName) {
        super(fieldName);
    }

    @Override
    public Writable getValue(Object entity) {
        Writable rval = NullWritable.get();
        String value = null;
        try {
            value = BSONValueLookup.getValue((BSONObject) entity, fieldName);

            if (value != null) {
                rval = new DoubleWritable(Double.parseDouble(value.toString()));
            }
        } catch (NumberFormatException e) {
            log.severe(String.format("Failed to convert value {%s} to Double", value));
        }
        return rval;
    }
}