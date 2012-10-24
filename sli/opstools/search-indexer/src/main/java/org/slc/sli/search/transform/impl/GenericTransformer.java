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
import java.util.Map.Entry;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.transform.CustomTransformer;
import org.slc.sli.search.util.NestedMapUtil;

/**
 * Transforms entity to index record
 * @author agrebneva
 *
 */
public class GenericTransformer implements CustomTransformer {
    public void transform(IndexConfig config, Map<String, Object> entity) {
        filterExcept(config.getFlattendedFields(), entity);
        rename(config.getRename(), entity);
        append(config.getAppend(), entity);
    }
    
    private void rename(Map<List<String>, List<String>> rename, Map<String, Object> entity) {
        if (rename == null) {
            return;
        }
        for (Map.Entry<List<String>, List<String>> entry : rename.entrySet()) {
            NestedMapUtil.rename(entry.getKey(), entry.getValue(), entity);
        }
    }
    
    private void filterExcept(List<String> fields, Map<String, Object> entity) {
        NestedMapUtil.filterExcept(fields, entity);
    }
    
    private void append(Map<List<String>, Object> append, Map<String, Object> entity) {
        if (append == null) {
            return;
        }
        for (Entry<List<String>, Object> entry : append.entrySet()) {
            NestedMapUtil.put(entry.getKey(), entry.getValue(), entity);
        }
    }
}
