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

import java.util.logging.Logger;

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;

import org.slc.sli.aggregation.util.BSONUtilities;

/**
 * DoubleValueMapper - mongo value mapper that emits double values.
 */
public class DoubleValueMapper extends ValueMapper {

    private Logger log = Logger.getLogger("DoubleValueMapper");

    public DoubleValueMapper(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public Writable getValue(BSONWritable entity) {
        Writable rval = NullWritable.get();
        String value = null;
        try {
            value = BSONUtilities.getValue(entity, fieldName);

            if (value != null) {
                rval = new DoubleWritable(Double.parseDouble(value.toString()));
            }
        } catch (NumberFormatException e) {
            log.severe(String.format("Failed to convert value {%s} to Double", value));
        }
        return rval;
    }
}
