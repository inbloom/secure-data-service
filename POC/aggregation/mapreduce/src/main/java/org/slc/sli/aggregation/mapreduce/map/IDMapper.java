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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * IDMapper
 *
 * A basic mapper that emits the unique identifiers for a provided collection.
 *
 * @param <K>
 *            An implementation of EmittableKey that accepts a map of id/value pairs.
 *
 */
@SuppressWarnings("rawtypes")
public class IDMapper<K extends EmittableKey> extends Mapper<Text, BSONObject, K, LongWritable> {

    protected K identifier;

    /**
     * IDMapper Constructor - Construct a new IDMapper and initialize the identifier.
     *
     * @param cls
     *            EmittableKey class to instantiate. The class must implement a no-argument
     *            constructor.
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public IDMapper(Class<K> cls) throws InstantiationException, IllegalAccessException {
        identifier = cls.newInstance();
    }

    @Override
    protected void map(Text id, BSONObject entity, Context context) throws IOException,
        InterruptedException {

        @SuppressWarnings("unchecked")
        // Values in the getIdNames Set are dot-separated Mongo field names.
        LinkedHashSet<String> idFieldNames = identifier.getFieldNames();
        Map<Text, Text> ids = new TreeMap<Text, Text>();
        for (String field : idFieldNames) {
            String value = getLeaf(entity, field);
            if (value != null) {
                ids.put(new Text(field), new Text(value));
            }
        }

        identifier.putAll(ids);
        context.write(identifier, new LongWritable(1));
    }

    /**
     * Given a dot-separated field, return the resulting value if it exists in the entity.
     *
     * @param entity
     *            Entity to query
     * @param field
     *            Field to retrieve.
     * @return String value of the field, or null if the field is not found.
     */
    @SuppressWarnings("unchecked")
    protected String getLeaf(BSONObject entity, final String field) {
        String rval = null;
        Map<String, Object> node = entity.toMap();

        String[] fieldPath = field.split("\\.");
        for (String path : fieldPath) {
            if (node.containsKey(path)) {
                Object val = entity.get(path);
                if (val instanceof Map) {
                    node = (Map<String, Object>) val;
                } else {
                    rval = (String) val;
                    break;
                }
            }
        }
        return rval;
    }

}
