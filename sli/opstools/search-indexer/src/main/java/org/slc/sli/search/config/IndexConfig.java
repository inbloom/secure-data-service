package org.slc.sli.search.config;

import java.util.ArrayList;
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
    // name of the index type
    public String indexType;
    // fields of the collections
    private List<String> fields;
    // rename to fields
    private Map<String, String> rename;
    
    // if child entity, parentId field
    private String parentField;
    
    private List<String> parentFieldChain;
    
    private Map<String, String> filterCondition;
    
    @JsonIgnore
    private Map<List<String>, List<String>> renameMap;
    
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
    
    public String getIndexType() {
        return indexType == null ? collectionName : indexType;
    }
    
    public List<String> getParentField() {
        return parentFieldChain;
    }
    
    public Map<String, String> getfilterCondition() {
        return filterCondition;
    }
    
    public List<String> getFlattendedFields() {
        return flattenedFields;
    }
    
    public boolean hasDependents() {
        return !dependents.isEmpty();
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
    
    public void prepare(String name) {
        this.collectionName = name;
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
        Set<String> flattenedFields = new HashSet<String>();
        for (String field: fields)
            flattenedFields.addAll(NestedMapUtil.getPathLinkFromDotNotation(field));
        this.flattenedFields = Collections.unmodifiableList(new ArrayList<String>(flattenedFields));
        if (this.parentField != null)
            this.parentFieldChain = NestedMapUtil.getPathLinkFromDotNotation(this.parentField);
    }
}