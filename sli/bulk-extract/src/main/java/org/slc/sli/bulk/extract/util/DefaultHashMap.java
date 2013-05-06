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
package org.slc.sli.bulk.extract.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Map that returns a default value when the key is not present.
 * @author ablum
 *
 * @param <K>
 * @param <V>
 */
public class DefaultHashMap<K, V> extends HashMap<K, V> {
    private V defaultValue;
    
    /**
     * Constructor.
     * 
     * @param map
     *            map
     * @param defaultValue
     *            defaultValue
     */
    public DefaultHashMap(Map<K, V> map, V defaultValue) {
        super(map);
        this.defaultValue = defaultValue;
    }
    
    /**
     * get the value.
     * 
     * @param key
     *            key
     * @return the value, or the default value if the key does not exist
     */
    public V getValue(K key) {
        if (super.containsKey(key)) {
            return super.get(key);
        }
        return defaultValue;
    }
    
    /**
     * get the default value without passing any keys
     * 
     * @return the default value
     */
    public V getDefault() {
        return defaultValue;
    }
}
