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


package org.slc.sli.api.representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contents of an entity body
 *
 * @author nbrown
 *
 */
public class EntityBody extends HashMap<String, Object> {

    private static final long serialVersionUID = -301785504415342449L;

    public EntityBody() {
        super();
    }

    public EntityBody(Map<? extends String, ? extends Object> m) {
        super(m);
    }

    /**
     * Return a list of values for the key
     * If the value is a String, a list with a single member is returned
     * If the value is a List, String representations of the members are returned
     *
     * @param key the key to look for
     * @return the list of values that match
     */
    public List<String> getValues(String key) {
        List<String> valueList = new ArrayList<String>();
        Object value = this.get(key);
        if (value instanceof String) {
            valueList.add((String) value);
        } else if (value instanceof List<?>) {
            for (Object subValues : (List<?>) value) {
                valueList.add(subValues.toString());
            }
        }
        return valueList;
    }
}
