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

import org.bson.BSONObject;

/**
 * BSONValueLookup - Helper class that looks up a leaf node in a BSON object given a typical
 * mongo dot notation field name.
 */
public class BSONValueLookup {

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
}
