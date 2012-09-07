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

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.mapreduce.Mapper;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * IDMapper
 *
 * The most basic of mapper functions -- emit what is given.
 *
 * Map input / output:
 * EmittableKey - Input key type. Defines the fields that represent a key in the entity.
 * BSONOBject - Entity to examine.
 * EmittableKey - Output key for the mapper.
 * BSONObject -- The entity the key corresponds to.
 *
 * @param <T> EmittableKey type.
 */
public class IDMapper<T extends EmittableKey> extends Mapper<T, BSONWritable, T, BSONWritable> {

    @Override
    public void map(T id, BSONWritable entity, Context context) throws IOException, InterruptedException {
        context.write(id, entity);
    }
}
