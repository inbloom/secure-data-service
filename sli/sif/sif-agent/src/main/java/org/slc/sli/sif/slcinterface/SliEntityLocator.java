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
package org.slc.sli.sif.slcinterface;

import java.util.HashMap;
import java.util.Map;

/**
* Helper class
*/
public class SliEntityLocator {
    private String type, value, field;

    public SliEntityLocator(String type, String fieldValue, String fieldName) {
        this.type = type;
        this.value = fieldValue;
        this.field = fieldName;
    }

    public SliEntityLocator(Map<String, String> map) {
        type = map.get("type");
        value = map.get("value");
        field = map.get("field");
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("type", type);
        map.put("value", value);
        map.put("field", field);
        return map;
    }

    @Override
    public String toString() {
        return "type = " + type + " ; field = " + field + " ; value = " + value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getField() {
        return field;
    }


}
