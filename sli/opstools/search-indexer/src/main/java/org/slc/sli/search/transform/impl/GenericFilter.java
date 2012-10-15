package org.slc.sli.search.transform.impl;

import java.util.List;
import java.util.Map;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.transform.Filter;
import org.slc.sli.search.util.NestedMapUtil;

/**
 * Filters by key/value pairs
 * 
 */
public class GenericFilter implements Filter {
    
    public boolean matchesCondition(IndexConfig config, Map<String, Object> entity) {
        Map<List<String>, Object> filters = config.getFilterCondition();
        Object val;
        if (filters != null) {
            for (Map.Entry<List<String>, Object> entry : filters.entrySet()) {
                val = NestedMapUtil.get(entry.getKey(), entity);
                if (entry.getValue() != null && !entry.getValue().equals(val) || entry.getValue() == null && val != null)
                    return false;
            }
        }
        return true;
    }
}
