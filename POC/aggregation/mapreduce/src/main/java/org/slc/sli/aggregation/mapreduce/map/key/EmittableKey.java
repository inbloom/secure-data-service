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

package org.slc.sli.aggregation.mapreduce.map.key;

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
    
    protected Text[] fieldNames = null;
    
    @Override
    public abstract int compareTo(EmittableKey other);
    
    /**
     * Get this key as a BSONObject instance. This allows for arbitrary complex keys.
     * 
     * @return BSONObject instance
     */
    public abstract BSONObject toBSON();
    
    /**
     * setFieldName - set the one and only field name for this emitter.
     * 
     * @param keyFields
     */
    public void setFieldName(final String name) {
        fieldNames = new Text[1];
        fieldNames[0] = new Text(name);
    }
    
    /**
     * getFieldName - get the one and only field name for this emitter.
     * 
     * @return Text
     */
    public Text getFieldName() {
        return fieldNames[0];
    }
    
    /**
     * addFieldNames - add a set of emittable field to this emitter.
     * 
     * @param keyFields
     */
    public void setFieldNames(final String[] names) {
        fieldNames = new Text[names.length];
        int i = 0;
        for (String name : names) {
            fieldNames[i++] = new Text(name);
        }
    }
    
    /**
     * Get the name of the fields that this class represents. For Mongo identifiers,
     * these should be the fully qualified field name(s).
     * 
     * @return LinkedHashSet<String> ordered set of field names.
     */
    public final Text[] getFieldNames() {
        return fieldNames;
    }
}
