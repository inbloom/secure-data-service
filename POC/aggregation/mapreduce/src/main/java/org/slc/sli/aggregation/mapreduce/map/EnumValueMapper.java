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

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import org.slc.sli.aggregation.util.BSONUtilities;

/**
 * EnumValueMapper - mongo value mapper that emits the enumerated value for the given value.
 *
 * @param <T>
 *            Enum type.
 */
public class EnumValueMapper<T extends Enum<T>> extends ValueMapper {

    private Logger log = Logger.getLogger("EnumValueMapper");

    Class<T> enumClass = null;

    public EnumValueMapper(String fieldName, Class<T> v) {
        this.fieldName = fieldName;
        this.enumClass = v;
    }

    @Override
    public Writable getValue(BSONWritable entity) {
        Writable rval = NullWritable.get();
        String value = null;
        try {
            value = BSONUtilities.getValue(entity, fieldName);
            if (value != null) {
                value = Enum.valueOf(enumClass, value).toString();
                rval = new Text(value);
            }
        } catch (IllegalArgumentException e) {
            log.severe(String.format("Failed to convert value {%s} to Enum", value));
        }
        return rval;
    }
}
