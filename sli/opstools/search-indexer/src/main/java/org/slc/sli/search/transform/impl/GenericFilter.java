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
package org.slc.sli.search.transform.impl;

import java.util.List;
import java.util.Map;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.transform.Filter;
import org.slc.sli.search.util.NestedMapUtil;

/**
 * Filters by key/value pairs and can potentially mutate the original object
 * 
 */
public class GenericFilter implements Filter {
    
    @SuppressWarnings("unchecked")
    public boolean matchesCondition(IndexConfig config, Map<String, Object> entity) {
        Map<List<String>, Object> filters = config.getFilterCondition();
        Object val;
        if (filters != null) {
            for (Map.Entry<List<String>, Object> entry : filters.entrySet()) {
                val = NestedMapUtil.get(entry.getKey(), entity);
                if (val != null && val instanceof List) {
                    boolean found = false;
                    // if an array, look through the values if one match found
                    for (Object arVal: (List<Object>)val) {
                        if (isMatch(entry.getValue(), arVal)) {
                            found = true;
                            break;
                        }  
                        if (!found) return false;
                    }
                }
                else if (!isMatch(entry.getValue(), val))
                    return false;
            }
        }
        return true;
    }
    
    private boolean isMatch(Object expected, Object actual) {
        return (actual != null && actual.equals(expected) || actual == null && expected == null);
    }
}
