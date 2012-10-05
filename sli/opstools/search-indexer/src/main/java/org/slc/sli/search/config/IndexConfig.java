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
    
    private Map<String, String> filterCondition;
    
    @JsonIgnore
    private Map<List<String>, List<String>> renameMap;
    
    @JsonIgnore
    private List<String> flattenedFields;

    public List<String> getFields() {
        return fields;
    }

    public Map<String, String> getRename() {
        return rename;
    }
    
    public Map<List<String>, List<String>> getRenameMap() {
        return renameMap;
    }
    
    public String getIndexType() {
        return indexType;
    }
    
    public Map<String, String> getfilterCondition() {
        return filterCondition;
    }
    
    public List<String> getFlattendedFields() {
        return flattenedFields;
    }
    
    public void prepare() {
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
    }
}