package org.slc.sli.search.transform.impl;

import java.util.Map;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.transform.Filter;

public class GenericFilter implements Filter {
    
    public boolean matchesCondition(IndexConfig config, Map<String, Object> entity) {
        return false;
    }
    
}
