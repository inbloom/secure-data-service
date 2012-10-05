package org.slc.sli.search.transform.impl;

import java.util.List;
import java.util.Map;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.transform.Transformer;
import org.slc.sli.search.util.NestedMapUtil;

public class GenericTransformer implements Transformer {
    public void transform(IndexConfig config, Map<String, Object> entity) {
        filterExcept(config.getFlattendedFields(), entity);
        rename(config.getRenameMap(), entity);
    }
    
    private void rename(Map<List<String>, List<String>> rename, Map<String, Object> entity) {
        if (rename == null) {
            return;
        }
        for (Map.Entry<List<String>, List<String>> entry : rename.entrySet()) {
            NestedMapUtil.moveField(entry.getKey(), entry.getValue(), entity);
        }
    }
    
    private void filterExcept(List<String> fields, Map<String, Object> entity) {
        NestedMapUtil.filterExcept(fields, entity);
    }
}
