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

package org.slc.sli.sif.domain.slientity;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

//import org.slc.sli.api.client.impl.GenericEntity;

/**
 * An GenericEntity in the SLI domain. Each SLI Entity can be converted to a
 * JSON Node ready for SLI operations.
 *
 * @author slee
 *
 */
public abstract class SliEntity {
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Constructor
     */
    public SliEntity() {

    }

    /**
     * Get the SLI entity type name
     */
    public abstract String getEntityType();

    /**
     * Output body of this Entity
     */
    //public GenericEntity getGenericEntity() {
    //    return new GenericEntity(getEntityType(), body());
    //}

    /**
     * Output body of this Entity
     */
    public Map<String, Object> body() {
        Map<String, Object> body = MAPPER.convertValue(this, new TypeReference<Map<String, Object>>() {
        });
        clearNullValueKeys(body);
        return body;
    }

    /**
     * Output this Entity as a JSON Node
     */
    public JsonNode json() {
        return MAPPER.valueToTree(this);
    }

    /**
     * Output this object as a JSON String
     */
    @Override
    public String toString() {
        return json().toString();
    }

    // ============ private helper

    // removes all keys from this map that has a null value. If some values are
    // maps,
    // do it recursively
    private static void clearNullValueKeys(Map m) {
        Set keySet = m.keySet();
        Set keysToRemove = new HashSet();
        for (Object k : keySet) {
            if (isNullValue(m.get(k))) {
                keysToRemove.add(k);
            }
        }
        for (Object k : keysToRemove) {
            m.remove(k);
        }
    }

    private static void clearNullValueFromList(List l) {
        ListIterator it = l.listIterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (isNullValue(o)) {
                it.remove();
            }
        }
    }

    private static boolean isNullValue(Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof Map) {
            clearNullValueKeys((Map) o);
            return ((Map) o).isEmpty();
        } else if (o instanceof List) {
            clearNullValueFromList((List) o);
            return ((List) o).isEmpty();
        }
        return false;
    }
}
