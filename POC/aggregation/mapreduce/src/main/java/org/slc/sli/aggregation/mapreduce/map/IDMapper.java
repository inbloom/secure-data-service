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

import java.io.IOException;
import java.util.Map;

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.mapreduce.Mapper;

import org.slc.sli.aggregation.mapreduce.io.JobConfiguration;
import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;
import org.slc.sli.aggregation.util.BSONUtilities;

/**
 * IDMapper
 *
 * The most basic of mapper functions -- emit what is given minus the identifier fields.  These fields are implicitly
 * included in the query output fields but are included in the input/output keys so there is no need to emit them.
 *
 * Map input / output:
 * EmittableKey - Input key type. Defines the fields that represent a key in the entity.
 * BSONOBject - Entity to examine.
 * EmittableKey - Output key for the mapper.
 * BSONObject -- The entity the key corresponds to, minus the key fields.
 *
 * @param <T> EmittableKey type.
 */
public class IDMapper<T extends EmittableKey> extends Mapper<T, BSONWritable, T, BSONWritable> {

    private static Map<String, String> idFields = null;

    @Override
    protected void setup(Mapper<T, BSONWritable, T, BSONWritable>.Context context) throws IOException, InterruptedException {
        super.setup(context);
        JobConfiguration.ConfigSections sections = JobConfiguration.readFromConfiguration(context.getConfiguration());
        idFields = sections.getMapper().getMapIdFields();
    }

    @Override
    public void map(T id, BSONWritable entity, Context context) throws IOException, InterruptedException {

        for (String field : idFields.values()) {
            BSONUtilities.removeField(entity, field);
        }

        context.write(id, entity);
    }

}
