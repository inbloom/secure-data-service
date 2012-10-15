package org.slc.sli.search.transform;

import java.util.Map;

import org.slc.sli.search.config.IndexConfig;

/**
 * Custom transformer to index json
 *
 */
public interface CustomTransformer {
    /**
     * Mutates the original entity according to specified.
     * @param config - index config
     * @param entity - entity to transform
     */
    public void transform(IndexConfig config, Map<String, Object> entity);
    
}
