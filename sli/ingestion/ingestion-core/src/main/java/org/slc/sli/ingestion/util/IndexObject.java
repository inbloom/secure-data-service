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

package org.slc.sli.ingestion.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mongodb.DBObject;

/**Class to hold the index keys
 *
 * @author tke
 *
 */
public class IndexObject {
    private Map<String, Integer> keys = new LinkedHashMap<String, Integer>();

    public IndexObject(DBObject obj) {
        setKeys(obj);
    }

    public IndexObject() {
        //default constructor is needed for when MongoIndex does not
        //know about the keys yet.
    }

    @SuppressWarnings("boxing")
    public final void setKeys(DBObject obj) {
        //This DBObject.toMap returns a LinkedHashMap, so order
        // is preserved
        @SuppressWarnings("unchecked")
        Map<String, Object> objMap = obj.toMap();
        for (Map.Entry<String, Object> entry : objMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Double) {
                keys.put(entry.getKey(), ((Double) value).intValue());
            } else {
                keys.put(entry.getKey(), (Integer) value);
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((keys == null) ? 0 : keys.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IndexObject other = (IndexObject) obj;
        if (keys == null) {
            if (other.keys != null) {
                return false;
            }
        } else if (!keys.equals(other.keys)) {
            return false;
        }
        return true;
    }

    /**
     * @return the keys
     */
    public Map<String, Integer> getKeys() {
        return keys;
    }
}
