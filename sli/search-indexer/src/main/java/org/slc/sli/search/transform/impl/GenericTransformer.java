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
package org.slc.sli.search.transform.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfig.Append;
import org.slc.sli.search.util.DotPath;
import org.slc.sli.search.util.NestedMapUtil;

/**
 * Transforms entity to index record
 *
 */
public class GenericTransformer {
    public void transform(IndexConfig config, Map<String, Object> entity) {
        filterExcept(config.getFlattendedFields(), entity);
        rename(config.getRename(), entity);
        append(config.getAppend(), entity);
    }
    
    private void rename(Map<DotPath, DotPath> rename, Map<String, Object> entity) {
        if (rename == null) {
            return;
        }
        for (Map.Entry<DotPath, DotPath> entry : rename.entrySet()) {
            NestedMapUtil.rename(entry.getKey(), entry.getValue(), entity);
        }
    }
    
    private void filterExcept(List<String> fields, Map<String, Object> entity) {
        NestedMapUtil.filterExcept(fields, entity);
    }
    
    @SuppressWarnings("unchecked")
    private void append(Map<DotPath, Append> append, Map<String, Object> entity) {
        if (append == null) {
            return;
        }
        Object value;
        Append ap;
        for (Entry<DotPath, Append> entry : append.entrySet()) {
            value = null;
            ap = entry.getValue();
            if (ap.getValue() != null) {
                value = ap.getValue(); 
            }
            else {
                Object subdoc = (ap.getSubdoc() == null) ? entity : NestedMapUtil.get(ap.getSubdoc(), entity);
                if (subdoc != null) {
                    if (subdoc instanceof List){
                        List<Object> subdocList = new ArrayList<Object>();
                        for (Object o : ((List<Object>)subdoc)) {
                            if (isMatch(ap.getCondition(), o)) {
                                subdocList.add((ap.getField() != null) ? ((Map<String, Object>)o).get(ap.getField()) : o);
                            }
                        }
                        value = subdocList.isEmpty() ? null : subdocList;
                    } else if (subdoc instanceof Map) {
                        value = (ap.getField() != null) ? ((Map<String, Object>)subdoc).get(ap.getField()) : subdoc;
                    }
                }
            }
            if (value != null) {
                NestedMapUtil.put(entry.getKey(), value, entity);
            }
        }
    }
    
    public boolean isMatch(IndexConfig config, Map<String, Object> entity) {
        return isMatch(config.getCondition(), entity);
    }
    
    private boolean isMatch(Map<DotPath, Object> filters, Object entity) {
        if (filters != null) {
            for (Map.Entry<DotPath, Object> entry : filters.entrySet()) {
                if (!isMatch(entry.getKey(), entry.getValue(), entity)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isMatch(DotPath fieldChain, Object value, Object entity) {
        Object target = NestedMapUtil.get(fieldChain, entity);
        return (value != null && value.equals(target) || value == null && target == null);
    }
}
