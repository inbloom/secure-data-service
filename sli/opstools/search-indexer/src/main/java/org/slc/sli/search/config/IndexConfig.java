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
package org.slc.sli.search.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.slc.sli.search.util.NestedMapUtil;

/**
 * Config holder entity
 *
 */
public final class IndexConfig {

    private static final List<String> REQUIRED_FIELDS = Arrays.asList(new String[]{"type", "_id"});
    // name of the index type
    public String indexType;
    // fields of the collections
    private List<String> fields;
    // rename to fields
    private Map<String, String> rename;
    
    // append to map
    private Map<String, Object> append;
    
    // if child entity, parentId field
    private String parentField;
    
    private List<String> parentFieldChain;
    
    private Map<String, Object> filterCondition;
    
    private Map<String, Object> mapping;
    
    @JsonIgnore
    private Map<List<String>, List<String>> renameMap;
    
    @JsonIgnore
    private Map<List<String>, Object> appendMap;
    
    @JsonIgnore
    private Map<List<String>, Object> filtersMap;
    
    @JsonIgnore
    private List<String> flattenedFields;
    
    @JsonIgnore
    private String collectionName;
    
    /**
     * list of entity names that depends on this entity as the parent doc
     */
    @JsonIgnore
    private final List<String> dependents = new ArrayList<String>();

    public String getCollectionName() {
        return collectionName;
    }
    
    public List<String> getFields() {
        return fields;
    }
    
    public Map<List<String>, List<String>> getRename() {
        return renameMap;
    }
    

    public Map<List<String>, Object> getAppend() {
        return this.appendMap;
    }
    
    public String getIndexType() {
        return indexType == null ? collectionName : indexType;
    }
    
    public List<String> getParentField() {
        return parentFieldChain;
    }
    
    public Map<List<String>, Object> getFilterCondition() {
        return filtersMap;
    }
    
    public List<String> getFlattendedFields() {
        return flattenedFields;
    }
    
    public boolean hasDependents() {
        return !dependents.isEmpty();
    }
    
    public Map<String, Object> getMapping() {
        return mapping;
    }
    
    public List<String> getDependents() {
        return dependents;
    }
    
    public void addDependent(String dependent) {
        this.dependents.add(dependent);
    }
    
    public boolean isChildDoc() {
        return indexType != null && !this.collectionName.equals(indexType);
    }
    
    /**
     * Modifies the original structures for performance reasons
     * @param name
     */
    public void prepare(String name) {
        this.collectionName = name;
        Set<String> fieldSet = new HashSet<String>(this.fields);
        fieldSet.addAll(REQUIRED_FIELDS);
        this.fields = new ArrayList<String>(fieldSet);
        if (rename != null) {
            List<String> fieldChainFrom, fieldChainTo;
            Map<List<String>, List<String>> renameMap = new HashMap<List<String>, List<String>>();
            for (Map.Entry<String, String> entry : rename.entrySet()) {
                fieldChainFrom = NestedMapUtil.getPathLinkFromDotNotation(entry.getKey());
                fieldChainTo = NestedMapUtil.getPathLinkFromDotNotation(entry.getValue());
                renameMap.put(fieldChainFrom, fieldChainTo);
            }
            this.renameMap = Collections.unmodifiableMap(renameMap);
        }
        if (append != null) {
            Map<List<String>, Object> appendMap = new HashMap<List<String>, Object>();
            for (Map.Entry<String, Object> entry : append.entrySet()) {
                appendMap.put(NestedMapUtil.getPathLinkFromDotNotation(entry.getKey()), entry.getValue());
            }
            this.appendMap = Collections.unmodifiableMap(appendMap);
        }
        if (filterCondition != null) {
            Map<List<String>, Object> filterMap = new HashMap<List<String>, Object>();
            for (Map.Entry<String, Object> entry : filterCondition.entrySet()) {
                filterMap.put(NestedMapUtil.getPathLinkFromDotNotation(entry.getKey()), entry.getValue());
            }
            this.filtersMap = Collections.unmodifiableMap(filterMap);
        }
        Set<String> flattenedFields = new HashSet<String>();
        for (String field: fields)
            flattenedFields.addAll(NestedMapUtil.getPathLinkFromDotNotation(field));
        this.flattenedFields = Collections.unmodifiableList(new ArrayList<String>(flattenedFields));
        if (this.parentField != null)
            this.parentFieldChain = NestedMapUtil.getPathLinkFromDotNotation(this.parentField);
    }
}