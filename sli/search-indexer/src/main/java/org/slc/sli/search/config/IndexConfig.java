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
package org.slc.sli.search.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.slc.sli.search.util.DotPath;

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
    private Map<DotPath, DotPath> rename;
    
    // append to map
    private Map<DotPath, Append> append;
    
    private Map<DotPath, Object> condition;
    
    private Map<String, Object> mapping;
    
    @JsonIgnore
    private List<String> flattenedFields;
    
    @JsonIgnore
    private String collectionName;
    
    public static class Append {
        private DotPath subdoc;
        private String field;
        private String value;
        private Map<DotPath, Object> condition;

        public DotPath getSubdoc() {
            return subdoc;
        }
        public String getField() {
            return field;
        }
        public String getValue() {
            return value;
        }
        public Map<DotPath, Object> getCondition() {
            return condition;
        }
        @Override
        public String toString() {
            return "Append [subdoc=" + subdoc + ", field=" + field + ", value=" + value + ", filterCondition=" + condition + "]";
        }
    }
    
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
    
    public Map<DotPath, DotPath> getRename() {
        return rename;
    }
    

    public Map<DotPath, Append> getAppend() {
        return this.append;
    }
    
    public String getIndexType() {
        return indexType == null ? collectionName : indexType;
    }
    
    public Map<DotPath, Object> getCondition() {
        return condition;
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
        Set<String> flattenedFields = new HashSet<String>();
        for (String field: fields) {
            flattenedFields.addAll(DotPath.to(new DotPath(field)));
        }
        this.flattenedFields = Collections.unmodifiableList(new ArrayList<String>(flattenedFields));
    }
}
