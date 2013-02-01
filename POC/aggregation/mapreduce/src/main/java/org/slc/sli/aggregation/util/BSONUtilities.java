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

package org.slc.sli.aggregation.util;

import java.util.LinkedList;
import java.util.List;

import com.mongodb.hadoop.io.BSONWritable;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

/**
 * BSONUtilities - Helper class that looks up a leaf node in a BSON object given a typical
 * mongo dot notation field name.
 */
public class BSONUtilities {

    /**
     * Given a dot-separated field, return the resulting value if it exists in the entity.
     * If the value does not exist, return null;
     *
     * @param entity
     *            Entity to query
     * @param field
     *            Field to retrieve.
     * @return String value of the field, or null if the field is not found.
     */
    public static String getValue(BSONObject entity, final String field) {
        String rval = null;

        BSONObject node = entity;
        String[] fieldPath = field.toString().split("\\.");
        for (String path : fieldPath) {
            if (node.containsField(path)) {
                Object val = node.get(path);
                if (val instanceof BSONObject) {
                    node = (BSONObject) val;
                } else {
                    rval = String.valueOf(val);
                    break;
                }
            }
        }
        return rval;
    }

    public static BSONObject setValue(String field, Object value) {
        BSONObject root = new BasicBSONObject();
        String[] fieldPath = field.toString().split("\\.");

        BSONObject node = root;
        BSONObject child = null;
        String path = null;
        for (int i = 0; i < fieldPath.length - 1; ++i) {
            path = fieldPath[i];
            child = new BasicBSONObject();
            node.put(path, child);
            node = child;
        }

        if (value == null) {
            value = "-";
        }
        node.put(fieldPath[fieldPath.length - 1], value);

        return root;
    }

    /**
     * Given a dot-separated field, return the resulting values if they exists in the entity.
     * If the values do not exist, return null;
     *
     * @param entity
     *            Entity to query
     * @param field
     *            Field to retrieve.
     * @return String array of values of the field, or null if the field is not found.
     */
    public static String[] getValues(BSONObject entity, final String field) {
        List<String> rval = new LinkedList<String>();

        BSONObject node = entity;
        String[] fieldPath = field.toString().split("\\.");

        for (String path : fieldPath) {
            if (!node.containsField(path)) {
                break;
            }

            Object val = node.get(path);
            if (val instanceof BSONObject) {
                node = (BSONObject) val;
            }
        }

        if (node instanceof BasicBSONList) {
            for (Object e : ((BasicBSONList) node)) {
                if (e instanceof BSONObject) {
                    BSONObject obj = (BSONObject) e;
                    for (String k : obj.keySet()) {
                        rval.add(obj.get(k).toString());
                    }
                } else {
                    rval.add(e.toString());
                }
            }
        } else {
            for (String key : node.keySet()) {
                rval.add(node.get(key).toString());
            }
        }
        return rval.toArray(new String[0]);
    }

    /**
     * @param entity
     * @param field
     */
    public static BSONObject removeField(BSONWritable entity, String field) {
        BSONObject node = entity;
        String[] fieldPath = field.toString().split("\\.");
        for (String path : fieldPath) {
            if (node.containsField(path)) {
                Object val = node.get(path);
                if (val instanceof BSONObject) {
                    node = (BSONObject) val;
                } else {
                    node.removeField(path);
                    break;
                }
            }
        }
        return entity;
    }
}
