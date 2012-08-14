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

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * IDMapper
 *
 * A basic mapper that emits the unique identifiers for a provided collection.
 *
 * Map input / output:
 * EmittableKey - Input key type. Defines the fields that represent a key in the entity.
 * BSONOBject - Entity to examine.
 * EmittableKey - Output key for the mapper.
 * BSONObject -- The entity the key corresponds to.
 */
public class IDMapper extends Mapper<EmittableKey, BSONObject, EmittableKey, BSONObject> {

    protected EmittableKey identifier;

    /**
     * IDMapper Constructor - Construct a new IDMapper and initialize the identifier.
     *
     * @param cls
     *            EmittableKey class to instantiate. The class must implement a no-argument
     *            constructor.
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public IDMapper(Class<? extends EmittableKey> keyType, final String[] keyFields)
        throws InstantiationException, IllegalAccessException {
        super();
        identifier = keyType.newInstance();
        identifier.setFieldNames(keyFields);
    }

    @Override
    public void map(EmittableKey id, BSONObject entity, Context context) throws IOException, InterruptedException {

        // Values in the getIdNames Set are dot-separated Mongo field names.
        Text[] idFieldNames = identifier.getFieldNames();
        for (Text field : idFieldNames) {
            String value = BSONValueLookup.getValue(entity, field.toString());
            if (value != null) {
                identifier.put(field, new Text(value));
            }
        }
        context.write(identifier, entity);
    }
}
