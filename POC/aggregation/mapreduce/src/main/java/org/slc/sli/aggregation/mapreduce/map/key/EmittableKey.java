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

package org.slc.sli.aggregation.mapreduce.map.key;

import java.util.LinkedHashSet;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.bson.BSONObject;

/**
 * EmittableKey
 *
 * Abstract class that defines a single or multi-value identifier used as an emit key in a
 * map / reduce job. Classes that implement this interface are usable by the IDMapper class to
 * generate map/reduce keys.
 *
 * An EmittableKey has one or more identifier keys.
 *
 */
public abstract class EmittableKey extends MapWritable implements WritableComparable<EmittableKey> {

    protected LinkedHashSet<Text> fieldNames = new LinkedHashSet<Text>();

    @Override
    public abstract int compareTo(EmittableKey other);

    /**
     * Get this key as a BSONObject instance. This allows for arbitrary complex keys.
     *
     * @return BSONObject instance
     */
    public abstract BSONObject toBSON();

    /**
     * addFieldName - add an emittable field to this emitter.
     *
     * @param keyFields
     */
    public final void setFieldName(final String name) {
        fieldNames.clear();
        fieldNames.add(new Text(name));
    }

    /**
     * addFieldNames - add a set of emittable field to this emitter.
     *
     * @param keyFields
     */
    public final void setFieldNames(final String[] names) {
        fieldNames.clear();
        for (String name : names) {
            fieldNames.add(new Text(name));
        }
    }

    /**
     * Get the name of the fields that this class represents. For Mongo identifiers,
     * these should be the fully qualified field name(s).
     *
     * @return LinkedHashSet<String> ordered set of field names.
     */
    public final Text[] getFieldNames() {
        return fieldNames.toArray(new Text[0]);
    }
}
